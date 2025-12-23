package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@ToString
public class AddCouponsRequest {
    private String course_code;
    private String coupon_code;
    private String coupon_description;
    private int promotion_type;
    private double amount;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date ex_date;
}
