package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetInstructorMonthlyRevenueExpandedReportResponse {
    private String date;
    private String customerName;
    private String course;
    private String couponCode;
    private String pricePaid;
    private String paymentProcessingFees;
    private String AppleOrGoogleFees;
    private String tax;
    private String netRevenue;
    private String yourRevenue;
    private String channel;
    private String platform;
}
