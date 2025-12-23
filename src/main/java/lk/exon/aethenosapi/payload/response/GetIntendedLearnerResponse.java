package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetIntendedLearnerResponse {
    private String intended_learner;
    private String intended_learner_type;
}
