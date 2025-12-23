package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetMonthRevenueResponse {
    private int day;
    private double revenue;
}
