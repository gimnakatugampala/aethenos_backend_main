package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SuccessResponse {
    private String message;
    private String variable;
}
