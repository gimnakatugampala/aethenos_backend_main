package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Data
@ToString
public class AddAssignmentRequest {
    private int courseSectionId;
    private String assignmentCode;
    private String title;
    private String description;
    private String duration;
    private String instructions;
    private String assignmentVideoGeneratedName;
    private String assignmentVideoOriginalName;
    private String assignmentResourceGeneratedName;
    private String assignmentResourceOriginalName;
    private String externalLink;
    private String questions;
    private String assignmentQuestionSheetGeneratedName;
    private String assignmentQuestionSheetOriginalName;
    private String questionLink;
    private String solution;
    private String assignmentSolutionVideoGeneratedName;
    private String assignmentSolutionVideoOriginalName;
    private String assignmentSolutionSheetGeneratedName;
    private String assignmentSolutionSheetOriginalName;
    private String solutionLink;


}
