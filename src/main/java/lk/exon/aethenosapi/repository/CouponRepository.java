package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.Coupon;
import lk.exon.aethenosapi.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {
    Coupon getCouponByCode(String code);

    List<Coupon> getCouponByCourse(Course course);

    Coupon getCouponByCodeAndCourse(String couponCode, Course course);

    List<Coupon> findByEndDateBeforeAndIsActive(Date now, int isActive);


}
