package lk.exon.aethenosapi.payload.response;

import lk.exon.aethenosapi.entity.Country;
import lk.exon.aethenosapi.entity.Currency;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PricingRangeResponse {
    private String minPrice;
    private String maxPrice;
    private String countryCurrency;
    private String country;
    private String tip;
    private String minimumPrice;

}
