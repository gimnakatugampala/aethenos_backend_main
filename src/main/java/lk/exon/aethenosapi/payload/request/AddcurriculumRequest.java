package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Data
@ToString
public class AddcurriculumRequest {
    private String courseCode;
    private String sectionName;
    private String article;
    private MultipartFile video;
    private String description;
    private MultipartFile downloadableFile;
    private String externalResourcesTitle;
    private String externalResourcesUrl;
    private MultipartFile sourceCode;
}
