package lk.exon.aethenosapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "student_topic")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StudentTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "general_user_profile_id", nullable = false)
    private GeneralUserProfile generalUserProfile;
}
