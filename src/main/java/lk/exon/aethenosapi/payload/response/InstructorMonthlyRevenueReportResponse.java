package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.List;
@Data
@ToString
public class InstructorMonthlyRevenueReportResponse {
    private List<GetInstructorMonthlyRevenueExpandedReportResponse> purchases;
    private List<GetMonthlyInstructorRefundsResponse> refunds;
    private String totalPurchases;
    private String totalRefunds;
    private String timePeriod;
}
