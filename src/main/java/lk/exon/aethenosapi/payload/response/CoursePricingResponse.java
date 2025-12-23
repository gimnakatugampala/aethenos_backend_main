package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CoursePricingResponse {
    private double value;
    private String discountType;
    private int discountTypeId;
    private String country;
    private String currency;
    private double discountValue;
    private double maxPrice;
    private double minPrice;
    private String tip;
    private double minimumPrice;
}
