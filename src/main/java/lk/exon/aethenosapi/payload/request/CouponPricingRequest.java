package lk.exon.aethenosapi.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CouponPricingRequest {
    private Double discount;

    @JsonProperty("discount_amount")
    private Double discountAmount;

    @JsonProperty("discount_price")
    private Double discountPrice;

    @JsonProperty("list_price")
    private Double listPrice;

    @JsonProperty("country_name")
    private String countryName;

    @JsonProperty("currency_id")
    private int currencyId;

}
