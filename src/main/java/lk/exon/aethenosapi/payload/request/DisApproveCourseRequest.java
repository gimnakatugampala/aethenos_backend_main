package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DisApproveCourseRequest {
    private String code;
    private String comment;
}
