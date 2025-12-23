package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UpdateRefundStatusRequest {
    private String refundCode;
    private Integer refundStatusId;
    private String comment;
}
