package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class GetCourseDetailsToBuyRequest {
    private String courseCode;
    private String currency;
    private Double itemPrice;
    private Double listPrice;
    private String couponCode;
}
