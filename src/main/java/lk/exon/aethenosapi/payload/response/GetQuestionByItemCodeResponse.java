package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetQuestionByItemCodeResponse {
    private String QuestionCode;
    private String userName;
    private String question;
    private String answer;
    private String profileImg;
    private String date;
}
