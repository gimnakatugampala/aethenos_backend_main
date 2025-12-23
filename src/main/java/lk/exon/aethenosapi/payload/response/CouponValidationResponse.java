package lk.exon.aethenosapi.payload.response;

import lk.exon.aethenosapi.payload.request.CouponPricingRequest;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@ToString
public class CouponValidationResponse {
    private String validation;
    private String couponType;
    private int couponTypeId;

    private Date start_date;

    private Date end_date;

    private int course_Id;
    private String course_code;

    private Double global_list_price;

    private Double global_discount_price;

    private Double global_discount_percentage;

    private Double global_discount;

    private List<CouponPricingResponse> course_prices;

}
