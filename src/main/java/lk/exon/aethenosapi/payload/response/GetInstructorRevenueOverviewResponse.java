package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetInstructorRevenueOverviewResponse {
    private double totalRevenue;
    private double thisMonthRevenue;
    private int totalEnrollments;
    private int thisMonthEnrollments;
    private double instructorRating;
    private int thisMonthRating;
}
