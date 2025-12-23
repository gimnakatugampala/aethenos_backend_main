package lk.exon.aethenosapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "notification")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "notification")
    private String notification;
    @Column(name = "notification_code")
    private String notificationCode;
    @Column(name = "notification_time")
    private Date notificationTime;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "general_user_profile_id")
    private GeneralUserProfile generalUserProfile;
    @Column(nullable = false, columnDefinition = "TINYINT(0)")
    private boolean isRead;
}
