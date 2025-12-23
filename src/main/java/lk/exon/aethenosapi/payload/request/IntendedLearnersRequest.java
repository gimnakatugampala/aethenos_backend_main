package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class IntendedLearnersRequest {
    private String course_code;
    private String[] studentsLearn;
    private String[] requirements;
    private String[] who;

}
