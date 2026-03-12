package lk.exon.aethenosapi.service;

import com.github.javafaker.Faker;
import lk.exon.aethenosapi.entity.*;
import lk.exon.aethenosapi.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.SimpleDateFormat; // Add this to your imports at the top if missing

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class MarketingService {

    @Autowired
    private GeneralUserProfileRepository gupRepo;

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private OrderHasCourseRepository ohcRepo;

    @Autowired
    private CourseRepository courseRepo;

    @Autowired
    private ReviewRepository reviewRepo;

    @Autowired
    private GupTypeRepository gupTypeRepo;

    @Autowired
    private PaymentMethodRepository paymentMethodRepo;

    /**
     * Phase A: Bulk Generate "Ghost" Students and Enroll them in a course.
     */
    @Transactional
    public void generateGhostsAndEnroll(Integer courseId, int count) {
        Faker faker = new Faker();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course ID " + courseId + " not found in database."));

        GupType safeGupType = gupTypeRepo.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("CRITICAL: No GupType found in database. Please add one."));

        PaymentMethod safePaymentMethod = paymentMethodRepo.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("CRITICAL: No PaymentMethod found in database. Please add one."));

        for (int i = 0; i < count; i++) {
            // 1. Create Ghost User
            GeneralUserProfile ghost = new GeneralUserProfile();
            ghost.setFirstName(faker.name().firstName());
            ghost.setLastName(faker.name().lastName());
            ghost.setEmail(faker.internet().emailAddress());
            ghost.setPassword(passwordEncoder.encode("Aethenos2026"));
            ghost.setIsActive((byte) 1);
            ghost.setRegisteredDate(faker.date().past(90, TimeUnit.DAYS));

            ghost.setCountry("United States"); // Updated to US
            ghost.setUserCode(UUID.randomUUID().toString());
            ghost.setIsSynthetic((byte) 1);
            ghost.setGupType(safeGupType);

            ghost = gupRepo.save(ghost);

            // 2. Create Fake Order
            Order order = new Order();
            order.setBuyDate(ghost.getRegisteredDate());
            order.setCurrency("USD"); // Updated to USD
            order.setDiscount(0.0);
            order.setTotal(0.0);
            order.setGeneralUserProfile(ghost);
            order.setPaymentMethod(safePaymentMethod);

            order = orderRepo.save(order);

            // 3. Create Fake Enrollment
            OrderHasCourse ohc = new OrderHasCourse();
            ohc.setCourse(course);
            ohc.setOrder(order);
            ohc.setItemCode(UUID.randomUUID().toString()); // UUID Generator added
            ohc.setItemPrice(0.0);
            ohc.setListPrice(0.0);
            ohc.setCurrrency("USD"); // Updated to USD
            ohc.setProgress(faker.number().randomDouble(2, 10, 100));
            ohc.setIsComplete((byte) (ohc.getProgress() == 100.0 ? 1 : 0));
            ohc.setIsDelete((byte) 0);

            ohcRepo.save(ohc);
        }

        // 4. Update Course Buy Count (SAFE VERSION)
        Integer currentCount = course.getBuyCount();
        if (currentCount == null) {
            currentCount = 0;
        }
        course.setBuyCount(currentCount + count);
        courseRepo.save(course);
    }

    /**
     * Phase B: Fetch ghosts who can leave a review.
     */
    public List<GeneralUserProfile> getAvailableGhosts(Integer courseId) {
        return gupRepo.findAvailableGhostsForCourse(courseId);
    }

    /**
     * Phase C: Add the manual review on behalf of the selected ghost user.
     */
    @Transactional
    public void addManualReview(Integer courseId, Integer gupId, Double rating, String comment, String reviewDateStr) {
        OrderHasCourse ohc = ohcRepo.findEnrollmentForGhost(courseId, gupId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found for this user"));

        Review review = new Review();

        GeneralUserProfile ghostRef = new GeneralUserProfile();
        ghostRef.setId(gupId);
        review.setGeneralUserProfile(ghostRef);

        Course courseRef = new Course();
        courseRef.setId(courseId);
        review.setCourse(courseRef);

        review.setOrderHasCourse(ohc);
        review.setRating(rating);
        review.setComment(comment);
        review.setReviewCode(UUID.randomUUID().toString());

        // Parse the date from the frontend, or default to today if something goes wrong
        Date reviewDate = new Date();
        if (reviewDateStr != null && !reviewDateStr.isEmpty()) {
            try {
                reviewDate = new SimpleDateFormat("yyyy-MM-dd").parse(reviewDateStr);
            } catch (Exception e) {
                System.out.println("Date parsing failed, defaulting to today.");
            }
        }
        review.setDate(reviewDate);

        reviewRepo.save(review);
    }
    /**
     * Phase D (Cleanup 1): Delete ONLY the reviews left by synthetic users.
     */
    @Transactional
    public void deleteOnlySyntheticReviews(Integer courseId) {
        List<GeneralUserProfile> ghosts = gupRepo.findAllSyntheticUsersForCourse(courseId);

        if (ghosts.isEmpty()) {
            throw new RuntimeException("No synthetic users found for this course.");
        }

        for (GeneralUserProfile ghost : ghosts) {
            if (ghost.getIsSynthetic() != null && ghost.getIsSynthetic() == 1) {
                List<Review> reviews = reviewRepo.findAllByGeneralUserProfile(ghost);
                reviews.removeIf(r -> r.getCourse().getId() != courseId);
                reviewRepo.deleteAll(reviews);
            }
        }
    }

    /**
     * Phase D (Cleanup 2): Complete wipe of Students, Orders, Enrollments, AND Reviews.
     */
    @Transactional
    public void deleteAllSyntheticData(Integer courseId) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course ID " + courseId + " not found."));

        List<GeneralUserProfile> ghosts = gupRepo.findAllSyntheticUsersForCourse(courseId);

        if (ghosts.isEmpty()) {
            throw new RuntimeException("No synthetic users found for this course.");
        }

        int deletedCount = 0;

        for (GeneralUserProfile ghost : ghosts) {
            if (ghost.getIsSynthetic() != null && ghost.getIsSynthetic() == 1) {

                // A. Delete Reviews first
                List<Review> reviews = reviewRepo.findAllByGeneralUserProfile(ghost);
                reviewRepo.deleteAll(reviews);

                // B. Delete Enrollments and Orders
                List<Order> orders = orderRepo.findAllByGeneralUserProfile(ghost);
                for (Order order : orders) {
                    List<OrderHasCourse> enrollments = ohcRepo.getOrderHasCoursesByOrder(order);
                    ohcRepo.deleteAll(enrollments);
                }
                orderRepo.deleteAll(orders);

                // C. Delete the User Account
                gupRepo.delete(ghost);
                deletedCount++;
            }
        }

        // Drop the buy_count safely
        Integer currentCount = course.getBuyCount();
        if (currentCount == null) currentCount = 0;

        int newCount = currentCount - deletedCount;
        if (newCount < 0) newCount = 0;

        course.setBuyCount(newCount);
        courseRepo.save(course);
    }

    /**
     * Phase E: Fetch existing reviews written by synthetic users.
     */
    public List<java.util.Map<String, Object>> getSyntheticReviews(Integer courseId) {
        List<Review> reviews = reviewRepo.findSyntheticReviewsForCourse(courseId);
        List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();

        for (Review r : reviews) {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("reviewId", r.getId());
            map.put("studentName", r.getGeneralUserProfile().getFirstName() + " " + r.getGeneralUserProfile().getLastName());
            map.put("email", r.getGeneralUserProfile().getEmail());
            map.put("rating", r.getRating());
            map.put("comment", r.getComment());
            map.put("reviewDate", r.getDate());
            result.add(map);
        }
        return result;
    }

    /**
     * Phase E: Update an existing manual review.
     */
    @Transactional
    public void updateManualReview(Integer reviewId, Double rating, String comment, String reviewDateStr) {
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        review.setRating(rating);
        review.setComment(comment);

        if (reviewDateStr != null && !reviewDateStr.isEmpty()) {
            try {
                review.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(reviewDateStr));
            } catch (Exception e) {
                System.out.println("Date parsing failed on update, ignoring date change.");
            }
        }
        reviewRepo.save(review);
    }

    /**
     * Phase E: Delete a single manual review.
     */
    @Transactional
    public void deleteSingleManualReview(Integer reviewId) {
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // Safety Check: Ensure we don't accidentally delete a real user's review
        if (review.getGeneralUserProfile().getIsSynthetic() == null || review.getGeneralUserProfile().getIsSynthetic() != 1) {
            throw new RuntimeException("Error: You can only delete synthetic reviews from this interface.");
        }

        reviewRepo.delete(review);
    }

    /**
     * Phase F: Update a synthetic student's details (Name and Email).
     */
    @Transactional
    public void updateSyntheticStudentDetails(Integer gupId, String firstName, String lastName, String email) {
        GeneralUserProfile ghost = gupRepo.findById(gupId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (ghost.getIsSynthetic() == null || ghost.getIsSynthetic() != 1) {
            throw new RuntimeException("Security Error: You can only edit synthetic system students.");
        }

        ghost.setFirstName(firstName);
        ghost.setLastName(lastName);

        if (email != null && !email.trim().isEmpty()) {
            ghost.setEmail(email);
        }

        gupRepo.save(ghost);
    }
}