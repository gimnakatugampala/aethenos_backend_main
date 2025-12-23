package lk.exon.aethenosapi.payload.response;

import lk.exon.aethenosapi.entity.Course;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CourseWithProgressResponse {
    private Course course;
    private double progress;
}
