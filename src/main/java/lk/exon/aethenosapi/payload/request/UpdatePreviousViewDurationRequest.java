package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UpdatePreviousViewDurationRequest {
    private Integer curriculumItemId;
    private Double duration;
}
