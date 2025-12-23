package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AddCourseCompleteDetailsRequest {
    private String courseCode;
    private boolean intendedLearners;
    private boolean curriculum;
    private boolean courseLandingPage;
    private boolean pricing;
    private boolean courseMessages;
    private boolean promotions;
}
