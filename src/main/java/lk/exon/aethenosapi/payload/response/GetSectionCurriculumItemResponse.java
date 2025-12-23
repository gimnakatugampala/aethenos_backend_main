package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class GetSectionCurriculumItemResponse {
    private String title;
    private String article;
    private String description;
    private String curriculum_item_type;
    private Integer curriculumItemId;
    private String arrangeNo;
    private boolean read;
    private List<GetCurriculumItemFilesResponse> get_CurriculumItem_File;
    private List<GetAssignment>getAssignments;
    private List<GetQuiz> getQuizs;
    private  List<GetCodingExercise>getCodingExercises;
    private List<GetPracticeTest>getPracticeTests;

}
