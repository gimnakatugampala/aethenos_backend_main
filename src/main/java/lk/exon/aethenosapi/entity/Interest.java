package lk.exon.aethenosapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "interest")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Interest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "interest", length = 45)
    private String interest;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "general_user_profile_id", nullable = false)
    private GeneralUserProfile generalUserProfile;
}
