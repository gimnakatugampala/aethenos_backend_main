package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetCodingExercise {
    private String codingExerciseCode;
    private String instructions;
    private String codingVideo;
    private String downloadableResource;
    private String downloadableResourceTitle;
    private String externalLink;
    private String codingExerciseSheet;
    private String codingExerciseSheetTitle;
    private String CodingExternalLink;
    private String codingExerciseVideo;
    private String codingExerciseVideoTitle;
    private String codingSolutionsSheet ;
    private String codingSolutionsSheetTitle;
    private String solutionsExternalLink;
    private String codingSolutionsVideo;
    private String  codingSolutionsVideoTitle;
}
