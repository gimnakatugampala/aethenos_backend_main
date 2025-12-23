package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CheckRefundStatusResponse {
    private String refundCode;
    private String refundAmount;
    private String reason;
    private String status;
    private String comment;
}
