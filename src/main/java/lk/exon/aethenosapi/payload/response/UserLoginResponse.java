package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserLoginResponse {
    private String token;

    private String fname;

    private String lname;

    private String email;

    private String gup_type;
    private String loginToken;
    private String country;

}
