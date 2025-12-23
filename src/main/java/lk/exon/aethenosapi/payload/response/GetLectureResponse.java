package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetLectureResponse {
   private int id;
    private String title;
    private String courseSection;

}
