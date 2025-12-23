package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AddInstructorPaymentDetailsRequest {
    private String paypalUserName;
    private String paypalEmail;
    private String payoneerUserName;
    private String payoneerEmail;
    private String accountNumber;
    private String sort1;
    private String sort2;
    private String sort3;
    private String bankAccountName;
    private Integer paymentMethodId;
}
