package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetCompletedRefundsResponse {
    private String courseTitle;
    private String purchasedDate;
    private double purchasedAmount;
    private double refundAmount;
    private String studentName;
    private String transferredDate;
    private String currency;
}
