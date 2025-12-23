package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetPriceRangeResponse {

    private String CountryName;
    private double minPrice;
    private double maxPrice;
    private double minimumValue;
    private String tip;
}
