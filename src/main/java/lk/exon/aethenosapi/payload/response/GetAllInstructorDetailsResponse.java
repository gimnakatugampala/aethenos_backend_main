package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class GetAllInstructorDetailsResponse {
    private String userCode;
    private String name;
    private String joinDate;
    private int totalStudents;
    private String email;
    private boolean isActive;

}
