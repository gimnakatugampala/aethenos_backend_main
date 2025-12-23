package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString
public class InstructorTotalRevenueResponse {
    private double grossRevenue;
    private double netRevenue;
    private LocalDate revenueCalculateDate;
    private int revenueForMonth;
    private int revenueForYear;
}
