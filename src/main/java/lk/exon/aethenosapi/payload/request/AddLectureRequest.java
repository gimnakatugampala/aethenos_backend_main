package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AddLectureRequest {
    private String title;
    private int courseSectionId;
}
