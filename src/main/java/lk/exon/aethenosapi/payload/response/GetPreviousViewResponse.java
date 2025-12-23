package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetPreviousViewResponse {
    private String PreviousSectionCurriculumItemId;
    private String curriculumItemType;
    private String sectionId;

}
