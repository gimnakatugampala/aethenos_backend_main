package lk.exon.aethenosapi.payload.request;

import lk.exon.aethenosapi.entity.Country;
import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@ToString
public class GeneralUserProfileRequest {
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private Integer gup_type;
    private String countryName;
}
