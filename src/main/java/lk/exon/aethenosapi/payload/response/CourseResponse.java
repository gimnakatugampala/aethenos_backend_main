package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class CourseResponse {
    private String code;
    private String course_title;
    private String img;
    private String test_video;
    private int approval_type_id;
    private int course_category_id;
    private String[] keywordArray;
}
