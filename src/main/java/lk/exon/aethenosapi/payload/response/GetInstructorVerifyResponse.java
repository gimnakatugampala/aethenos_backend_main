package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetInstructorVerifyResponse {
    private String Message;
    private int IsVerify;
}
