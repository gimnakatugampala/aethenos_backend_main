package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class GetRevenueSplitHistoryResponse {
    private Double aethenosRevenueReferralLinkSplit;
    private Double instructorRevenueReferralLinkSplit;
    private Double aethenosRevenueAethenosSplit;
    private Double instructorRevenueAethenosSplit;
    private Date changedDate;
}
