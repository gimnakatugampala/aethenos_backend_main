package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetMonthlyInstructorRefundsResponse {
    private String date;
    private String customerName;
    private String course;
    private String refundsAmount;
    private String changeToYourRevenue;
}
