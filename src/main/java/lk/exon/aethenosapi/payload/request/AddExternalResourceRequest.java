package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AddExternalResourceRequest {
    private String CurriculumItemId;
    private String Title;
    private String Url;
}
