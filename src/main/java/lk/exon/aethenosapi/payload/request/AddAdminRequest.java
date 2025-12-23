package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AddAdminRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private int gup_type_id;
}
