package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CheckAllInstructorPaymentDetailsResponse {
    private boolean isPaymentDetails;
    private boolean isPaidCourse;
}
