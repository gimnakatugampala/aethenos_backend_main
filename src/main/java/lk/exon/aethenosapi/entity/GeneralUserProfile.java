package lk.exon.aethenosapi.entity;

import lk.exon.aethenosapi.payload.request.GeneralUserProfileRequest;
import lombok.*;

import javax.persistence.*;
import java.util.Date;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@Table(name = "general_user_profile")
public class GeneralUserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "email", length = 45)
    private String email;

    @Column(name = "user_code", length = 45)
    private String userCode;

    @Column(name = "registered_date")
    private Date registeredDate;

    @Column(name = "first_name", length = 45)
    private String firstName;

    @Column(name = "last_name", length = 45)
    private String lastName;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "is_active")
    private Byte isActive;

    @Column(name = "profile_img", length = 1000)
    private String profileImg;
    @Column(name = "verification_code")
    private String verificationCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gup_type_id")
    private GupType gupType;
    @Column(name = "country")
    private String country;

    @Column(name = "is_synthetic")
    private Byte isSynthetic;

}