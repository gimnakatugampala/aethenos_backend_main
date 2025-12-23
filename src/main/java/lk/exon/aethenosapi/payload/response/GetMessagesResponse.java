package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class GetMessagesResponse {
    private String to;
    private String toType;
    private String from;
    private String fromType;
    private String message;
    private Date time;
    private boolean isRead;
}
