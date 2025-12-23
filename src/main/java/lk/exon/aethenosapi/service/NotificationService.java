package lk.exon.aethenosapi.service;


import lk.exon.aethenosapi.payload.response.GetOwnNotificationResponse;
import lk.exon.aethenosapi.payload.response.SuccessResponse;

import java.util.List;

public interface NotificationService {

    List<GetOwnNotificationResponse> getOwnNotifications();

    SuccessResponse readNotification(String notificationCode);
}
