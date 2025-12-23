package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AddRefundRequest {
    private String reason;
    private String itemCode;
}
