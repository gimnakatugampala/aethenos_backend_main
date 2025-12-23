package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetInstructorRevenueReportChartDataSet {
    private double aethenosTotal;
    private double refundsTotal;
    private double referalLinkTotal;
    private double couponTotal;
    private int year;
}
