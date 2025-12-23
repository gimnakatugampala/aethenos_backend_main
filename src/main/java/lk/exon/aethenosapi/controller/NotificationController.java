package lk.exon.aethenosapi.controller;

import lk.exon.aethenosapi.payload.response.GetOwnNotificationResponse;
import lk.exon.aethenosapi.payload.response.SuccessResponse;
import lk.exon.aethenosapi.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "notification")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/getOwnNotifications")
    public List<GetOwnNotificationResponse> getOwnNotifications() {
        return notificationService.getOwnNotifications();
    }
    @PutMapping("/readNotification/{notificationCode}")
    public SuccessResponse readNotification(@PathVariable String notificationCode) {
        return notificationService.readNotification(notificationCode);
    }


}
