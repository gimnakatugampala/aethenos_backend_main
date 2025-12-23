package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Data
@ToString
public class StudentProfileUpdateRequest {
    private String firstName;
    private String lastName;
    private MultipartFile profileImage;
    private String headline;
    private String website;
    private String biography;
    private String twitter;
    private String facebook;
    private String linkedin;
    private String youtube;
}
