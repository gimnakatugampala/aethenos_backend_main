package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetAllInstructorsPaypalOrPayoneerDetailsForManagePaymentsResponse {
    private String InstructorName;
    private String accountType;
    private String userName;
    private String email;
    private String amount;
    private String monthOfSale;
}
