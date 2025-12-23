package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UpdateOrderHasCourseProgressRequest {
    private String itemCode;
    private String sectionName;
}
