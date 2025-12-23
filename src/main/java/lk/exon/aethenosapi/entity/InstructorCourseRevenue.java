package lk.exon.aethenosapi.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.YearMonth;

@Entity
@Table(name = "instructor_course_revenue")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class InstructorCourseRevenue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "revenue_id", nullable = false)
    private Revenue revenue;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "instructor_profile_id", nullable = false)
    private InstructorProfile instructorProfile;
    @Column(name = "instructor_share")
    private double instructorShare;
}
