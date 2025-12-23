package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetCurriculumItemFilesResponse {
    private String title;
    private String url;
    private String curriculum_item_file_type;
    private Double videoLength;
    private boolean PreviewVideo;
}
