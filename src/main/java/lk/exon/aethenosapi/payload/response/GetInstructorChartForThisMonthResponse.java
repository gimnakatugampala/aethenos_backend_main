package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetInstructorChartForThisMonthResponse {
    private int[] days;
    private double[] revenue;
    private int[] enrollment;
    private int[] rating;
}
