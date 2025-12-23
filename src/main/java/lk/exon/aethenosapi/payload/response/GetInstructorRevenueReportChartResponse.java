package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class GetInstructorRevenueReportChartResponse {
    private List<AmountDataSetsResponse> aethenosDataSets;
    private List<AmountDataSetsResponse> refundsDataSets;
    private List<AmountDataSetsResponse> referalLinkDataSets;
    private List<AmountDataSetsResponse> couponDataSets;
}
