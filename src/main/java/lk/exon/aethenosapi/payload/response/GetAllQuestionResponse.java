package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class GetAllQuestionResponse {
    private String courseTitle;
    private String QuestionCode;
    private String userName;
    private String question;
    private String answer;
    private String isRead;
    private String profileImg;
    private String date;
}
