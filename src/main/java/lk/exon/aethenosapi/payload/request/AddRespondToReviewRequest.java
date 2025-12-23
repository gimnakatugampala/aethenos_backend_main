package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AddRespondToReviewRequest {
    private String reviewCode;
    private String comment;
}
