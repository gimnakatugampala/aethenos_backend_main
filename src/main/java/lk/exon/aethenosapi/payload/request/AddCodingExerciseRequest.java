package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Data
@ToString
public class AddCodingExerciseRequest {
    private int courseSectionId;
    private String codingExerciseCode;
    private String title;
    private String description;
    private String instructions;
    private String videoGeneratedName;
    private String videoOriginalName;
    private String codingExerciseResourceGeneratedName;
    private String codingExerciseResourceOriginalName;
    private String externalLink;
    private String codingVideoGeneratedName;
    private String codingVideoOriginalName;
    private String codingFilesGeneratedName;
    private String codingFilesOriginalName;
    private String codingLink;
    private String solutionVideoGeneratedName;
    private String solutionVideoOriginalName;
    private String solutionSheetGeneratedName;
    private String solutionSheetOriginalName;
    private String solutionLink;
}
