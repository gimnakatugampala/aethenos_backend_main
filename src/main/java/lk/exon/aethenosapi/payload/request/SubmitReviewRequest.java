package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SubmitReviewRequest {
    private String item_code;
    private String comment;
    private double rating;
}
