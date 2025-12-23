package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SetDefaultPriceRangeRequest {
    private Double minPrice;
    private Double maxPrice;
    private String tip;
    private Double minimumPrice;
}
