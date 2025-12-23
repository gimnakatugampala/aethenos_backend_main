package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ProfileResponse {
    private int id;
    private String code;
    private String email;
    private String fname;
    private String lname;
}
