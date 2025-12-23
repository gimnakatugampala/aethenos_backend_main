package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class GetSectionItem {
    private String id;
    private String article;
    private String title;
    private String description;
    private String type;
    private String arrangeNo;
    private List<Getcurriculumitemfile> curriculumItemFiles;
    private List<GetQuiz> getQuizs;
    private List<GetAssignment> getAssignment;
    private List<GetCodingExercise> getCodingExercises;
    private List<GetPracticeTest> getPracticeTests;
}
