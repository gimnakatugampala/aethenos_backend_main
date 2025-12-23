package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetTwelveMonthRevenueResponse {
    private String month;
    private double revenue;
}
