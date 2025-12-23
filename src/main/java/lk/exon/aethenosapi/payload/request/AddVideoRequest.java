package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Data
@ToString
public class AddVideoRequest {
    private String CurriculumItemId;
    private String Video;
    private double videoLength;
    private String originalFileName;
}
