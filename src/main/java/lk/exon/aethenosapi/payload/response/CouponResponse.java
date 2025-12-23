package lk.exon.aethenosapi.payload.response;

import lk.exon.aethenosapi.entity.CouponType;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@ToString
public class CouponResponse {
    private String couponCode;
    private Date startDate;
    private Date endDate;
    private CouponType couponType;
    private int isActive;
    private String createdDate;
    private double globalDiscount;
    private double globalDiscountPercentage;
    private double globalDiscountPrice;
    private double globalListPrice;
    private String redemptions;
    private List<CouponPriceList> couponPrices;
}
