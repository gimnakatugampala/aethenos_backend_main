package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CheckCourseCompleteDetailsResponse {
    private boolean intendedLearners;
    private boolean curriculum;
    private boolean courseLandingPage;
    private boolean pricing;
    private boolean courseMessages;
    private boolean promotions;

}
