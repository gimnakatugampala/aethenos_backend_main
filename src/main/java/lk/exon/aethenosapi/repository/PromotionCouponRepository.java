package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.Course;
import lk.exon.aethenosapi.entity.PromotionCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromotionCouponRepository extends JpaRepository<PromotionCoupon, Integer> {
    List<PromotionCoupon> getPromotionCouponByCourseId(int id);

    PromotionCoupon getPromotionCouponByCourseIdAndCode(int courseByCode, String couponCode);

    PromotionCoupon getPromotionCouponByCode(String couponCode);
}