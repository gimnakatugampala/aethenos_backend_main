package lk.exon.aethenosapi.entity;

import lk.exon.aethenosapi.repository.CourseCategoryRepository;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@Table(name="course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String code;
    @Column(name = "course_title")
    private String courseTitle;
    private String comment;
    private String img;
    private String test_video;
    @Column(name = "course_length", columnDefinition = "double default 0")
    private double courseLength;
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "is_paid")
    private int isPaid;
    @Column(name = "instructor_terms", columnDefinition = "tinyint(1) default 0")
    private Byte instructorTerms;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "instructor_id")
    private InstructorProfile instructorId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "approval_type_id")
    private ApprovalType approvalType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_category_id")
    private CourseCategory courseCategory;

    @Column(name = "is_owned")
    private Byte isOwned;
    @Column(name = "buy_count")
    private Integer buyCount;
    @Column(name = "referral_code")
    private String referralCode;

}
