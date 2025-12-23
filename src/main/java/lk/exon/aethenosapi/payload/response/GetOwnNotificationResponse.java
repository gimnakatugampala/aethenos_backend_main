package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class GetOwnNotificationResponse {
    private String notificationCode;
    private String notification;
    private Date notificationTime;
    private Boolean isRead;
}
