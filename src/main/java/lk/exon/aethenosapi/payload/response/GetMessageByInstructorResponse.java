package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class GetMessageByInstructorResponse {
    private String Instructor;
    private String InstructorUserCode;
    private String InstructorProfileImg;
    private List<GetChatRoomDetailsByInstructorResponse> ChatRoomDetails;
}
