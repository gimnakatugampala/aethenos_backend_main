package lk.exon.aethenosapi.payload.response;

import lk.exon.aethenosapi.entity.PromotionType;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@ToString
public class CouponsResponse {

    private String coupon_code;
    private String coupon_description;
    private String promotion_type;
    private int promotion_type_id;
    private double amount;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date ex_date;
}
