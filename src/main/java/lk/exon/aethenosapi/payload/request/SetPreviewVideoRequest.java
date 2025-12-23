package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SetPreviewVideoRequest {
    private String videoFileName;
    private boolean PreviewVideo;
 }
