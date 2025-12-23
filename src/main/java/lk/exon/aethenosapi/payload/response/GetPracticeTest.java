package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetPracticeTest {
    private String practiceTestCode;
    private String title;
    private String duration;
    private Double minimumuPassMark;
    private String instructions;
    private String externalLink;
    private String practiceTestQuestionSheet;
    private String practiceTestQuestionSheetTitle;
    private String questionLink;
    private String practiceTestSolutionSheet;
    private String practiceTestSolutionSheetTitle;
    private String solutionLink;
}
