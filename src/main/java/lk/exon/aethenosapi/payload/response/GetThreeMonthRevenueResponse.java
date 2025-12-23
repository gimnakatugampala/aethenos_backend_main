package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.List;
@Data
@ToString
public class GetThreeMonthRevenueResponse {
    private String month;
    private List<GetMonthRevenueResponse>revenues;
}
