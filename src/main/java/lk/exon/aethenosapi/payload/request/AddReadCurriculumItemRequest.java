package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AddReadCurriculumItemRequest {
    private String itemCode;
    private Integer curriculumItemId;

}
