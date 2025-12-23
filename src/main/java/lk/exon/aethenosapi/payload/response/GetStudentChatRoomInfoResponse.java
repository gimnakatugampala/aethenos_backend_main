package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.Date;
@Data
@ToString
public class GetStudentChatRoomInfoResponse {
    private String chatRoomCode;
    private String courseTitle;
    private String courseCode;
    private String Instructor;
    private String InstructorUserCode;
    private String InstructorProfileImg;
    private String lastMessage;
    private Date lastSeen;
}
