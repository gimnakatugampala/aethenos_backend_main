package lk.exon.aethenosapi.payload.response;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CouponPricingResponse {
    private Double discount;

    private Double discountAmount;

    private Double discountPrice;

    private Double listPrice;

    private String countryName;

    private int countryId;
}
