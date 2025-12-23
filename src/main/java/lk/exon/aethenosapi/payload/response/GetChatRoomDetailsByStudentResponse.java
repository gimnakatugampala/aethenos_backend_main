package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@ToString
public class GetChatRoomDetailsByStudentResponse {
    private String chatRoomCode;
    private String courseTitle;
    private String courseCode;
    private String Instructor;
    private String InstructorUserCode;
    private String InstructorProfileImg;
    private String lastMessage;
    private Date lastSeen;
    private List<GetMessagesResponse> messages;
}
