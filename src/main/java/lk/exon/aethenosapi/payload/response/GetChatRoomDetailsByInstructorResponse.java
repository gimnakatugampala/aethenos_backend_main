package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@ToString
public class GetChatRoomDetailsByInstructorResponse {
    private String chatRoomCode;
    private String courseTitle;
    private String courseCode;
    private String student;
    private String studentUserCode;
    private String studentProfileImg;
    private String lastMessage;
    private Date lastSeen;
    private List<GetMessagesResponse> messages;
}
