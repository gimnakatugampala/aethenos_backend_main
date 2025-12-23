package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Getcurriculumitemfile {
    private String id;
    private String title;
    private String url;
    private String Filetype;
    private boolean isPreviewVideo;
}
