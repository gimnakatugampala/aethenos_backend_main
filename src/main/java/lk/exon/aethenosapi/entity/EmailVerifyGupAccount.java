package lk.exon.aethenosapi.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "email_verifify_gup_account")
public class EmailVerifyGupAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "generalUserProfile_id")
    private GeneralUserProfile generalUserProfile;
    @Column(name = "email_verification_code")
    private String emailVerificationCode;
    @Column(name = "is_verify")
    private Byte isVerify;
}
