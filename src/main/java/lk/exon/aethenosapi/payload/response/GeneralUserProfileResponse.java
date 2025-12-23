package lk.exon.aethenosapi.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.Date;

@Data
@ToString
public class GeneralUserProfileResponse {
    private String code;
    private String message;
    private String token;
    private String loginToken;
    private String variable;
}
