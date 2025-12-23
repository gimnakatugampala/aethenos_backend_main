package lk.exon.aethenosapi.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@Table(name = "course_section")
public class CourseSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "section_name", length = 80)
    private String sectionName;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course")
    private Course course;

    @Column(name = "is_delete")
    private Byte isDelete;
    @Column(name = "arranged_no")
    private Integer arrangedNo;

}