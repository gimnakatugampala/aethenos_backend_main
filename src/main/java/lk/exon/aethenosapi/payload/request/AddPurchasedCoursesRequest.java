package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class AddPurchasedCoursesRequest {
    private String paymentMethod;
    private String currency;
    private List<GetCourseDetailsToBuyRequest> courses;
    private Double discount;
    private Double totalPrice;
    private String country;
    private Integer courseType;
    private Double processingFee;
    private String stripe_pf_currency;
}
