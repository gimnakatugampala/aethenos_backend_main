package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.Course;
import lk.exon.aethenosapi.entity.GeneralUserProfile;
import lk.exon.aethenosapi.entity.OrderHasCourse;
import lk.exon.aethenosapi.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    Review getReviewsByOrderHasCourse(OrderHasCourse orderHasCourse);
    List<Review> getReviewsByCourse(Course course);

    Review getReviewByReviewCode(String reviewCode);

    List<Review> findAllByGeneralUserProfile(GeneralUserProfile gup);

    // Fetches reviews specifically left by synthetic ghost users for a course
    @Query(value = "SELECT r.* FROM review r JOIN general_user_profile g ON r.gup_id = g.id WHERE g.is_synthetic = 1 AND r.course_id = :courseId", nativeQuery = true)
    List<Review> findSyntheticReviewsForCourse(@Param("courseId") Integer courseId);
}