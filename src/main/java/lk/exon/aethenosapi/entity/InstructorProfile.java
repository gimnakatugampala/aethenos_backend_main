package lk.exon.aethenosapi.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "instructor_profile")
@Getter
@Setter
public class InstructorProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "headline", columnDefinition = "LONGTEXT")
    private String headline;

    @Column(name = "email")
    private String email;
    @Column(name = "website", columnDefinition = "LONGTEXT")
    private String website;

    @Column(name = "biography", columnDefinition = "LONGTEXT")
    private String biography;

    @Column(name = "twitter", columnDefinition = "LONGTEXT")
    private String twitter;

    @Column(name = "facebook", columnDefinition = "LONGTEXT")
    private String facebook;

    @Column(name = "linkedin", columnDefinition = "LONGTEXT")
    private String linkedin;

    @Column(name = "youtube", columnDefinition = "LONGTEXT")
    private String youtube;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "general_user_profile_id", nullable = false)
    private GeneralUserProfile generalUserProfile;

    @Column(name = "created_date")
    private Date created_date;

    @Column(name = "is_profile_completed")
    private Byte isProfileCompleted;

    @Column(name = "is_verified")
    private Byte isVerified;
}