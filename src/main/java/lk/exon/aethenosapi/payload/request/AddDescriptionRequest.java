package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AddDescriptionRequest {
    private String CurriculumItemId;
    private String Description;
}
