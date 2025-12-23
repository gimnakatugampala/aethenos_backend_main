package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetCourseAndInstructorDetailsResponse {
    private String CourseTitle;
    private String CourseCode;
    private String CourseImg;
    private String InstructorName;
    private String InstructorCode;
    private String InstructorImg;
    private String searchType;
}
