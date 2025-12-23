package lk.exon.aethenosapi.payload.response;

import lk.exon.aethenosapi.payload.request.GetCourseInfoRequest;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class InstructorDetailsResponse {
    private String userCode;
    private String name;
    private String profileImg;
    private List<GetCourseInfoRequest> coursesDetails;
}
