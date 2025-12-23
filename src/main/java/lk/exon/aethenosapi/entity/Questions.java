package lk.exon.aethenosapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "questions")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Questions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "date")
    private Date date;
    @Column(name = "question_code")
    private String code;
    @Column(name = "question", columnDefinition = "LONGTEXT")
    private String question;
    @Column(name = "answer", columnDefinition = "LONGTEXT")
    private String answer;
    @Column(name = "is_read")
    private int isRead;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gup_id", nullable = false)
    private GeneralUserProfile generalUserProfile;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
}
