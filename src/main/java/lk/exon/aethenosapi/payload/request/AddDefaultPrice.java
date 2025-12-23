package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class AddDefaultPrice {
    private String courseCode;
    private String globalListPrice;
    private int discountType;
    private String discount;
    private String discountAmount;
    private String globalNetPrice;
    private List<SetCoursePricingRequest> prices;
}
