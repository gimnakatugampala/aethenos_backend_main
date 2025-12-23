package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AddAnnouncementRequest {
    private String courseCode;
    private String title;
    private String content;
}
