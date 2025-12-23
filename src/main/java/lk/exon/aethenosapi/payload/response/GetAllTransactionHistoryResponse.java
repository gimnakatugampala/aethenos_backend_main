package lk.exon.aethenosapi.payload.response;

import lk.exon.aethenosapi.entity.Order;
import lk.exon.aethenosapi.entity.Vat;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@ToString
public class GetAllTransactionHistoryResponse {
    private String transactionCode;
    private String studentName;
    private String purchaseCountry;
    private String totalAmount;
    private String purchasedDate;
    private String paymentMethod;
    private List<GetExpandedTransactionResponse> courses;

}
