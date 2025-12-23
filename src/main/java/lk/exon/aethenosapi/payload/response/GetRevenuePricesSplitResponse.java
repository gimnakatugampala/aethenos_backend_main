package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetRevenuePricesSplitResponse {
    private Double aethenosRevenueReferralLinkSplit;
    private Double instructorRevenueReferralLinkSplit;
    private Double aethenosRevenueAethenosSplit;
    private Double instructorRevenueAethenosSplit;
}
