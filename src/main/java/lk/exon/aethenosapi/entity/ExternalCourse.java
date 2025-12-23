package lk.exon.aethenosapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "external_course")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExternalCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "link_to_course", columnDefinition = "LONGTEXT")
    private String linkToCourse;
    @Column(name = "external_rating")
    private double externalRating;
    @Column(name = "external_number_of_students")
    private long externalNumberOfStudents;
    @Column(name = "any_comments", columnDefinition = "LONGTEXT")
    private String anyComment;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
}
