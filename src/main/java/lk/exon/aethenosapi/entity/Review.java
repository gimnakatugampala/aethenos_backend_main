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
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "review_code")
    private String reviewCode;
    @Column(name = "date")
    private Date date;
    @Column(name = "rating")
    private double rating;
    @Column(name = "comment", columnDefinition = "LONGTEXT")
    private String comment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_has_course_id")
    private OrderHasCourse orderHasCourse;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gup_id")
    private GeneralUserProfile generalUserProfile;
}
