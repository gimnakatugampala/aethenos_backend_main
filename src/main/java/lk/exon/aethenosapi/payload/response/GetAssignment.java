package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetAssignment {
    private String assignmentCode;
    private String duration;
    private String instructions;
    private String assignmentVideo;
    private String assignmentVideoTitle;
    private String downloadableResource;
    private String downloadableResourceTitle;
    private String externalLink;
    private String question;
    private String questionSheet;
    private String questionSheetTitle;
    private String questionExternalLink;
    private String solutions;
    private String solutionVideo;
    private String solutionVideoTitle;
    private String solutionsSheet;
    private String solutionsSheetTitle;
    private String solutionsExternalLink;
}
