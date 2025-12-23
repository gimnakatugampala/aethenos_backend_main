package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.Coupon;
import lk.exon.aethenosapi.entity.CouponPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponPriceRepository extends JpaRepository<CouponPrice, Integer> {
    List<CouponPrice> getCouponPricesByCoupon(Coupon coupon);
}