package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetTransactionDetailsResponse {
    private String amount;
    private String TransActionCode;
    private String transactionDate;
    private String userName;
    private String vat;
    private String vatPercentage;
    private OrderDetailsResponse orderDetails;

}
