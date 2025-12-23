package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CheckPaidCourseValidationResponse {
    private double courseLength;
    private int lectureCount;
    private boolean paymentDetails;
}
