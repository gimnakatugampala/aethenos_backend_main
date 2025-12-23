package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Exceltobase64ByexcelFileNameRequest {
    private String excelFileName;
}
