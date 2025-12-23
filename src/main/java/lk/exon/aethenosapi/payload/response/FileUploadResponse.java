package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class FileUploadResponse {
    private String filename;
    private String Url;
}
