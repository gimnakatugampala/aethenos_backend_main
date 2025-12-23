package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AddQuizRequest {
    private String title;
    private String description;
    private int courseSectionId;
}
