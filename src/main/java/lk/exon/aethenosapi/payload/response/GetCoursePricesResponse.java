package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class GetCoursePricesResponse {
    private double globalListPrice;
    private int discountTypeId;
    private String discountType;
    private double discountAmount;
    private double discount;
    private double globalNetPrice;
    private List<GetCoursePricingResponse> prices;
}
