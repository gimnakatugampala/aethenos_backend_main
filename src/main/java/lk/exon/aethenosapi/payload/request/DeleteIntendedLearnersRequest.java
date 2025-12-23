package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DeleteIntendedLearnersRequest {
    private String courseCode;
    private String intendedLearner;
    private Integer intendedLearnerTypeId;
}
