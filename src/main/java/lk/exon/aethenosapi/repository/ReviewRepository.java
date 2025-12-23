package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.Course;
import lk.exon.aethenosapi.entity.OrderHasCourse;
import lk.exon.aethenosapi.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    Review getReviewsByOrderHasCourse(OrderHasCourse orderHasCourse);
    List<Review> getReviewsByCourse(Course course);

    Review getReviewByReviewCode(String reviewCode);
}