package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AddCourseSectionResponse {
    private String Message;
    private String statusCode;
    private int SectionCode;

}
