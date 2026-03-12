package lk.exon.aethenosapi.controller;

import lk.exon.aethenosapi.entity.GeneralUserProfile;
import lk.exon.aethenosapi.service.MarketingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/marketing")
@CrossOrigin(origins = "*")
public class MarketingController {

    @Autowired
    private MarketingService marketingService;

    @PostMapping("/generate-ghosts")
    public ResponseEntity<?> generateGhosts(@RequestParam Integer courseId, @RequestParam int count) {
        try {
            marketingService.generateGhostsAndEnroll(courseId, count);

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", count + " system students successfully generated and enrolled.");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = (e.getMessage() != null) ? e.getMessage() : "An error occurred during generation.";
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", errorMessage));
        }
    }

    @GetMapping("/available-ghosts/{courseId}")
    public ResponseEntity<?> getAvailableGhosts(@PathVariable Integer courseId) {
        try {
            List<GeneralUserProfile> ghosts = marketingService.getAvailableGhosts(courseId);
            return ResponseEntity.ok(ghosts);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PostMapping("/add-review")
    public ResponseEntity<?> addManualReview(@RequestBody ReviewRequest request) {
        try {
            marketingService.addManualReview(
                    request.getCourseId(),
                    request.getGupId(),
                    request.getRating(),
                    request.getComment(),
                    request.getReviewDate() // <--- NEW DATE PARAMETER
            );

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Review added successfully.");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @DeleteMapping("/delete-reviews/{courseId}")
    public ResponseEntity<?> deleteGhostReviews(@PathVariable Integer courseId) {
        try {
            marketingService.deleteOnlySyntheticReviews(courseId);

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Synthetic reviews successfully deleted. Students are still enrolled.");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = (e.getMessage() != null) ? e.getMessage() : "Error deleting reviews.";
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", errorMessage));
        }
    }

    @DeleteMapping("/delete-students/{courseId}")
    public ResponseEntity<?> deleteAllGhostData(@PathVariable Integer courseId) {
        try {
            marketingService.deleteAllSyntheticData(courseId);

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Synthetic students, reviews, and enrollments completely removed.");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = (e.getMessage() != null) ? e.getMessage() : "Error deleting students.";
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", errorMessage));
        }
    }

    @GetMapping("/ghost-reviews/{courseId}")
    public ResponseEntity<?> getGhostReviews(@PathVariable Integer courseId) {
        try {
            return ResponseEntity.ok(marketingService.getSyntheticReviews(courseId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PutMapping("/update-review/{reviewId}")
    public ResponseEntity<?> updateManualReview(@PathVariable Integer reviewId, @RequestBody ReviewRequest request) {
        try {
            marketingService.updateManualReview(
                    reviewId,
                    request.getRating(),
                    request.getComment(),
                    request.getReviewDate()
            );
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Review updated successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @DeleteMapping("/delete-single-review/{reviewId}")
    public ResponseEntity<?> deleteSingleReview(@PathVariable Integer reviewId) {
        try {
            marketingService.deleteSingleManualReview(reviewId);

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Review deleted successfully.");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PutMapping("/update-student/{gupId}")
    public ResponseEntity<?> updateStudentDetails(@PathVariable Integer gupId, @RequestBody StudentDetailsRequest request) {
        try {
            marketingService.updateSyntheticStudentDetails(gupId, request.getFirstName(), request.getLastName(), request.getEmail());

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Student details updated successfully.");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    public static class ReviewRequest {
        private Integer courseId;
        private Integer gupId;
        private Double rating;
        private String comment;

        private String reviewDate; // <--- NEW FIELD

        public Integer getCourseId() { return courseId; }
        public void setCourseId(Integer courseId) { this.courseId = courseId; }
        public Integer getGupId() { return gupId; }
        public void setGupId(Integer gupId) { this.gupId = gupId; }
        public Double getRating() { return rating; }
        public void setRating(Double rating) { this.rating = rating; }
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }

        public String getReviewDate() { return reviewDate; } // <--- GETTER
        public void setReviewDate(String reviewDate) { this.reviewDate = reviewDate; } // <--- SETTER
    }

    public static class StudentDetailsRequest {
        private String firstName;
        private String lastName;
        private String email;

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}