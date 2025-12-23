package lk.exon.aethenosapi.service.impl;

import lk.exon.aethenosapi.entity.GeneralUserProfile;
import lk.exon.aethenosapi.entity.Notification;
import lk.exon.aethenosapi.exception.ErrorException;
import lk.exon.aethenosapi.payload.response.GetOwnNotificationResponse;
import lk.exon.aethenosapi.payload.response.SuccessResponse;
import lk.exon.aethenosapi.repository.NotificationRepository;
import lk.exon.aethenosapi.service.NotificationService;
import lk.exon.aethenosapi.utils.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class notificationServiceImpl implements NotificationService {

    @Autowired
    private UserProfileServiceImpl userProfileServiceImpl;
    @Autowired
    private NotificationRepository notificationRepository;

    SuccessResponse successResponse;

    @Override
    public List<GetOwnNotificationResponse> getOwnNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);

        if (profile != null) {
            if (profile.getIsActive() == 1) {

                List<GetOwnNotificationResponse> getOwnNotificationResponses = new ArrayList<>();
                List<Notification> notifications = notificationRepository.getNotificationByGeneralUserProfile(profile);

                for (Notification notification : notifications) {
                    GetOwnNotificationResponse getOwnNotificationResponse = new GetOwnNotificationResponse();
                    getOwnNotificationResponse.setNotificationCode(notification.getNotificationCode());
                    getOwnNotificationResponse.setNotification(notification.getNotification());
                    getOwnNotificationResponse.setNotificationTime(notification.getNotificationTime());
                    getOwnNotificationResponse.setIsRead(notification.isRead());
                    getOwnNotificationResponses.add(getOwnNotificationResponse);
                }

                return getOwnNotificationResponses;

            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse readNotification(String notificationCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);

        if (profile != null) {
            if (profile.getIsActive() == 1) {

                List<Notification> notifications = notificationRepository.getNotificationByNotificationCode(notificationCode);
                for (Notification notification : notifications) {
                    notification.setRead(true);
                    notificationRepository.save(notification);
                }

                successResponse = new SuccessResponse();
                successResponse.setMessage("Read notification");
                successResponse.setVariable(VarList.RSP_SUCCESS);
                return successResponse;

            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }
}
