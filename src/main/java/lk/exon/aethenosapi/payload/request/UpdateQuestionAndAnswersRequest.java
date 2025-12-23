package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UpdateQuestionAndAnswersRequest {
    private Integer quizId;
    private String question;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    private String answer5;
    private String explanation1;
    private String explanation2;
    private String explanation3;
    private String explanation4;
    private String explanation5;
    private Integer correctAnswer;
}
