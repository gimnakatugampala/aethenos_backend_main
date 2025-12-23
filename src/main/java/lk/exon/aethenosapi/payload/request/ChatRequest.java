package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ChatRequest {

    private String message;
    private String courseCode;
    private String toUserCode;
    private Integer toUserTypeId;
}
