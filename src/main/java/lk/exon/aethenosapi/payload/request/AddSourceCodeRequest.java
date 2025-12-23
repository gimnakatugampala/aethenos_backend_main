package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Data
@ToString
public class AddSourceCodeRequest {
    private String CurriculumItemId;
    private String sourceCodeGeneratedName;
    private String sourceCodeOriginalName;
}
