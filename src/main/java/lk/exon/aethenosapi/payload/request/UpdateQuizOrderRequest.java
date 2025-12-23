package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UpdateQuizOrderRequest {
    private Integer sectionCurriculumItemId;
    private Integer[] quizOrder;
}
