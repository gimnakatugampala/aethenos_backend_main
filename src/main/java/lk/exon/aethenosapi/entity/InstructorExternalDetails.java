package lk.exon.aethenosapi.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "instructor_external_details")
public class InstructorExternalDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "link_to_course", columnDefinition = "LONGTEXT")
    private String linkToCourse;
    @Column(name = "external_rating")
    private double externalRating;
    @Column(name = "external_number_of_students")
    private int externalNumberOfStudents;
    @Column(name = "any_comments", columnDefinition = "LONGTEXT")
    private String anyComments;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "instructor_profile_id", nullable = false)
    private InstructorProfile instructorProfile;
}
