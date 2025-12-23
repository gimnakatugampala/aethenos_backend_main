package lk.exon.aethenosapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "user_login_token")
public class UserLoginToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "login_token")
    private String loginToken;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "gup_id", nullable = false)
    private GeneralUserProfile generalUserProfile;
}

