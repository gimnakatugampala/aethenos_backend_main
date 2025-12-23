package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetAllInstructorsUkBankDetailsForManagePaymentsResponse {
    private String instructorName;
    private String sortCode;
    private String accountNo;
    private String bankAccountName;
    private String amount;
    private String monthOfSale;
}
