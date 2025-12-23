package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ReferralCodeValidationResponse {
    private boolean validation;
    private String message;
    private String courseCode;
}
