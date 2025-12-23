package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class StudentRegistrationRequest {
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String countryName;
}
