package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class GetMessageByStudentResponse {
    private String student;
    private String studentUserCode;
    private String studentProfileImg;
    private List<GetChatRoomDetailsByStudentResponse> chatRoomDetails;

}
