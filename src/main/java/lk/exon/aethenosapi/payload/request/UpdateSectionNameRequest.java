package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UpdateSectionNameRequest {
    private String courseCode;
    private Integer sectionId;
    private String sectionName;
}
