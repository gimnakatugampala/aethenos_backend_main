package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class VerifyVerificationCodeRequest {
    private String verificationCode;
    private String email;
}
