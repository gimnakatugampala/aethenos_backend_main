package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
public class CurriculumResponse {
    private GetCourseSection courseSection;
}
