package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Data
@ToString
public class CourseRequest {
    private String code;
    private String course_title;
    private Double default_price;
    private MultipartFile img;
    private String test_video;
    private int approval_type_id;
    private int course_category_id;
    private int course_sub_category_id;
    private String[] keywords;

}
