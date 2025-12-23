package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ForgotPasswordRequest {
    private String verificationCode;
    private String email;
    private String newPassword;
}
