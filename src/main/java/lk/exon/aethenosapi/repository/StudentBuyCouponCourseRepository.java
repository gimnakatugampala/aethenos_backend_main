package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.Coupon;
import lk.exon.aethenosapi.entity.OrderHasCourse;
import lk.exon.aethenosapi.entity.StudentBuyCouponCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentBuyCouponCourseRepository extends JpaRepository<StudentBuyCouponCourse,Integer> {
    StudentBuyCouponCourse getStudentBuyCouponCourseByOrderHasCourse(OrderHasCourse orderHasCourse);

    List<StudentBuyCouponCourse> getStudentBuyCouponCourseByCoupon(Coupon coupon);
}
