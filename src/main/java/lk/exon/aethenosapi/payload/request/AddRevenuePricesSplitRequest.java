package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AddRevenuePricesSplitRequest {
    private Double aethenosRevenueReferralLinkSplit;
    private Double instructorRevenueReferralLinkSplit;
    private Double aethenosRevenueAethenosSplit;
    private Double instructorRevenueAethenosSplit;

}
