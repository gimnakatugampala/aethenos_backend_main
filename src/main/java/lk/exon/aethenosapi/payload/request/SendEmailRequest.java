package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Data
@ToString
public class SendEmailRequest {
    private String senderEmail;
    private String email;
    private String name;
    private String subject;
    private String message;
    private String whoAreYou;
    private MultipartFile attachment;
}
