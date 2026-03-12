package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.Course;
import lk.exon.aethenosapi.entity.Order;
import lk.exon.aethenosapi.entity.OrderHasCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OrderHasCourseRepository extends JpaRepository<OrderHasCourse, Integer> {
    List<OrderHasCourse> getOrderHasCoursesByCourse(Course course);
    List<OrderHasCourse> getOrderHasCoursesByOrder(Order orderObj);
    OrderHasCourse getOrderHasCourseByItemCode(String itemCode);

    OrderHasCourse getOrderHasCoursesByCourseAndOrder(Course course, Order order);

    @Query(value = "SELECT ohc.* FROM order_has_course ohc " +
            "JOIN orders o ON ohc.order_id = o.id " +
            "WHERE ohc.course_id = :courseId AND o.general_user_profile_id = :gupId LIMIT 1",
            nativeQuery = true)
    Optional<OrderHasCourse> findEnrollmentForGhost(@Param("courseId") Integer courseId, @Param("gupId") Integer gupId);
}
