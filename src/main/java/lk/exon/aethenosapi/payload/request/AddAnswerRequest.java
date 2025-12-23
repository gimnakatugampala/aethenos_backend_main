package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AddAnswerRequest {
    private String QuestionCode;
    private String answer;
}
