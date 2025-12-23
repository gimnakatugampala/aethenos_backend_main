package lk.exon.aethenosapi.payload.request;

import lk.exon.aethenosapi.entity.Country;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CoursePricingRequest {
    private String country;
    private String minPrice;
    private String maxPrice;
    private String tip;
    private String minimumPrice;
}
