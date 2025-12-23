package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class InstructorRevenueReportResponse {
    private int id;
    private String month;
    private String yourRevenue;
    private String ExpectedPaymentDate;
}
