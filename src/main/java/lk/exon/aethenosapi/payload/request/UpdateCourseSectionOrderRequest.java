package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UpdateCourseSectionOrderRequest {
    private String courseCode;
    private Integer[] courseSectionOrder;
}
