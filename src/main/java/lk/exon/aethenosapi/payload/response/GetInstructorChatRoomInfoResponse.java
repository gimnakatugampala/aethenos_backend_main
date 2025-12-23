package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class GetInstructorChatRoomInfoResponse {
    private String chatRoomCode;
    private String courseTitle;
    private String courseCode;
    private String student;
    private String studentUserCode;
    private String studentProfileImg;
    private String lastMessage;
    private Date lastSeen;
}
