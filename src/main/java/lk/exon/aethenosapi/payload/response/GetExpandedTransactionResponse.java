package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetExpandedTransactionResponse {
    private String courseName;
    private String listPrice;
    private String itemPrice;
    private String channel;
}
