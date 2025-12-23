package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class GetOwnAllRefundsResponse {
    private String courseTitle;
    private String date;
    private String currency;
    private String amount;
    private String refundedTo;
    private String status;
    private String transactionCode;
    private Integer orderId;
    private String itemCode;

}
