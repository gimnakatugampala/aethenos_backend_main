package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class GetCourseContentResponse {
    private int section_id;
    private String section_name;
    private Integer arrangedNo;
    private int no_of_lectures;
    private int no_of_qize;
    private int on_of_assignment;
    private int on_of_codingExercise;
    private int on_of_practiceTest;
    private List<GetSectionCurriculumItemResponse> section_curriculum_item;
}
