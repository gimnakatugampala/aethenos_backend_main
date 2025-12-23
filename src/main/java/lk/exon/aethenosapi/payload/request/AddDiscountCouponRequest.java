package lk.exon.aethenosapi.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@ToString
public class AddDiscountCouponRequest {
    private String code;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date start_date;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date end_date;

    private String course_code;

    private Double global_list_price;

    private Double global_discount_price;

    private Double global_discount_percentage;

    private Double global_discount;

    private List<CouponPricingRequest> prices;


}
