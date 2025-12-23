package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetCourseInfoRequest {
    private String courseName;
    private String courseCode;
}
