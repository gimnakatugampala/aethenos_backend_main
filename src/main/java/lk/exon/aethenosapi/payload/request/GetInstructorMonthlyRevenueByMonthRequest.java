package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetInstructorMonthlyRevenueByMonthRequest {
    private String month;
    private String userCode;
}
