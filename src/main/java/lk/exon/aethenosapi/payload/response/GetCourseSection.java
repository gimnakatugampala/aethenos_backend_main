package lk.exon.aethenosapi.payload.response;

import lk.exon.aethenosapi.entity.Quiz;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class GetCourseSection {
    private String sectionId;
    private String sectionName;
    private List<GetSectionItem> sectionCurriculumItem;

}
