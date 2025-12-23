package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PricingResponse {
    private double value;
    private String discountType;
    private String country;
    private String currency;
    private double discountValue;
}
