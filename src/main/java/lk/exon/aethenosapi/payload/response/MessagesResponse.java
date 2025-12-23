package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MessagesResponse {
    private String congratulations_msg;
    private String welcome_msg;
}
