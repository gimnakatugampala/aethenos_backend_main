package lk.exon.aethenosapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "course_complete")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CourseComplete {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    private Course course;
    @Column(name = "intended_learners")
    private Byte intendedLearners;
    @Column(name = "curriculum")
    private Byte curriculum;
    @Column(name = "course_landing_page")
    private Byte courseLandingPage;
    @Column(name = "pricing")
    private Byte pricing;
    @Column(name = "course_messages")
    private Byte courseMessages;
    @Column(name = "promotions")
    private Byte promotions;
}
