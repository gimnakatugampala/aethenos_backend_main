package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Data
@ToString
public class AddCurriculumItemFileRequest {
    private String CourseCode;
    private String sectionCode;
    private int curriculumItemCode;
    private String title;
    private String url;
    private MultipartFile video;
    private MultipartFile sourceCode;
    private MultipartFile downloadableFile;

}
