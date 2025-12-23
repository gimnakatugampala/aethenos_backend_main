package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetAnswer {
    private String id;
    private Boolean CorrectAnswer;
    private String explanation;
    private String name;

}
