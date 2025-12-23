package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Data
@ToString
public class AddSectionCurriculumItemRequest {
    private String CourseCode;
    private String CourseSection;
    private String article;
    private MultipartFile video;
    private String Description;
    private String title;

}
