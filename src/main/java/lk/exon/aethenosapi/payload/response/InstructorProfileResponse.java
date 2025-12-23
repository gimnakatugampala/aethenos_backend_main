package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class InstructorProfileResponse {
    private int id;
    private String headline;
    private String InstructorExternalEmail;
    private String website;
    private String biography;
    private String twitter;
    private String facebook;
    private String linkedin;
    private String youtube;
    private String email;
    private String user_code;
    private String registered_date;
    private String first_name;
    private String last_name;
    private String profile_img;
    private List<InstructorExternalDetailsResponse> instructorExternalDetailsResponse;
}
