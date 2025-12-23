package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UpdateCurriculumItemNameRequest {
    private Integer courseSectionId;
    private Integer sectionCurriculumItemId;
    private Integer curriculumItemTypeId;
    private String name;
    private String description;
}
