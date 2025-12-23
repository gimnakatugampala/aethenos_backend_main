package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CourseDetailsResponse {
    private String courseTitle;
    private double courseProgress;
    private int sectionCompleteCount;
    private int allSectionCount;

    private double courseCompletion;

}
