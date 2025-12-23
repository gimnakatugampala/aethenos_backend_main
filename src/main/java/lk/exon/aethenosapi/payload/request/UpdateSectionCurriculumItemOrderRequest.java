package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UpdateSectionCurriculumItemOrderRequest {
    private String courseCode;
    private Integer sectionId;
    private Integer curriculumItemId;
    private Integer arrangedNo;
}
