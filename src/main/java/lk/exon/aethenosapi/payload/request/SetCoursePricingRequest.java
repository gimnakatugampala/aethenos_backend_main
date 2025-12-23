package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SetCoursePricingRequest {
    private String listPrice;
    private String netPrice;
    private String discountType;
    private String country;
    private String discount;
    private String discountAmount;

}
