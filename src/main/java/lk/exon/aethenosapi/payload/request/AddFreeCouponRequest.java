package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@ToString
public class AddFreeCouponRequest {
    private String code;
    private String course_code;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date start_date;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date end_date;
}
