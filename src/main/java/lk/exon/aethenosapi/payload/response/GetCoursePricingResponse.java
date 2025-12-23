package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetCoursePricingResponse {
    private double listPrice;
    private double netPrice;
    private int discountTypeId;
    private String discountType;
    private String country;
    private double discount;
    private double discountAmount;
    private double minPrice;
    private double maxPrice;
    private String currency;
}
