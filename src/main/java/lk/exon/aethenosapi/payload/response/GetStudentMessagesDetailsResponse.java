package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class GetStudentMessagesDetailsResponse {
    private String student;
    private String Instructor;
    private String InstructorUserCode;
    private String InstructorProfileImg;
    private String studentProfileImg;
    private String studentCode;
    private String chatCode;
    private String message;
    private Date time;

}
