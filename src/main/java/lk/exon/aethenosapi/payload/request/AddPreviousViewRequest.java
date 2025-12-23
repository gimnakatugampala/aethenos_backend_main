package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AddPreviousViewRequest {
    private String itemCode;
    private Integer sectionCurriculumItemId;

}
