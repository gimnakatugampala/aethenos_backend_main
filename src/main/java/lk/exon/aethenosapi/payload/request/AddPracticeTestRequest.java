package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Data
@ToString
public class AddPracticeTestRequest {
    private Integer courseSectionId;
    private String practiceTestCode;
    private String title;
    private String description;
    private String duration;
    private Double minimumPassMark;
    private String instructions;
    private String externalLink;
    private String generatedQuestionSheetName;
    private String originalQuestionSheetName;
    private String questionLink;
    private String generatedSolutionSheetName;
    private String originalSolutionSheetName;
    private String solutionLink;
}
