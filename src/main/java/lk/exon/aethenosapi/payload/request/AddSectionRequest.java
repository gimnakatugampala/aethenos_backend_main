package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AddSectionRequest {
    private String CourseCode;
    private String SectionName;
}
