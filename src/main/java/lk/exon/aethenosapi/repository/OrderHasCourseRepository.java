package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.Course;
import lk.exon.aethenosapi.entity.Order;
import lk.exon.aethenosapi.entity.OrderHasCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface OrderHasCourseRepository extends JpaRepository<OrderHasCourse, Integer> {
    List<OrderHasCourse> getOrderHasCoursesByCourse(Course course);
    List<OrderHasCourse> getOrderHasCoursesByOrder(Order orderObj);
    OrderHasCourse getOrderHasCourseByItemCode(String itemCode);

    OrderHasCourse getOrderHasCoursesByCourseAndOrder(Course course, Order order);
}
