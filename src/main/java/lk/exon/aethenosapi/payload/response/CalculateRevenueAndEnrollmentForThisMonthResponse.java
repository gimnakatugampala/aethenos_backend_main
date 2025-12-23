package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CalculateRevenueAndEnrollmentForThisMonthResponse {
    private double revenue;
    private int enrollment;
}
