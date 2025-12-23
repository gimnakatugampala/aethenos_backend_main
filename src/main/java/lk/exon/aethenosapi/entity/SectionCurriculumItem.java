package lk.exon.aethenosapi.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@Table(name = "section_curriculum_item")
public class SectionCurriculumItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "title")
    private String title;

    @Lob
    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_section_id")
    private CourseSection courseSection;
    @Column(name = "is_delete", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Byte isDelete;
    @Column(name = "arranged_no")
    private Integer arrangedNo;

    @Lob
    @Column(name = "article")
    private String article;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "curriculum_item_type_id")
    private CurriculumItemType curriculumItemType;


}