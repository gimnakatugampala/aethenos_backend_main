package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MessagesRequset {
    private String course_code;
    private String congratulations_msg;
    private String welcome_msg;

}
