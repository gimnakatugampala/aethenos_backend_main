package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class IntendedLearnersResponse {
    private String[] studentsLearn;
    private String[] requirements;
    private String[] who;
}