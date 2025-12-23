package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AdminProfileResponse {

    private String firstName;
    private String lastName;
    private String email;
    private String password;

}
