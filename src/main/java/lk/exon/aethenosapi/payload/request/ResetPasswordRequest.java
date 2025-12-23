package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ResetPasswordRequest {
    private String newPassword;
    private String oldPassword;
}
