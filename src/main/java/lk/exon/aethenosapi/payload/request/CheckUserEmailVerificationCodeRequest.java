package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CheckUserEmailVerificationCodeRequest {
    private String email;
    private String verificationCode;
}
