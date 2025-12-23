package lk.exon.aethenosapi.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@Table(name = "practice_test")
public class PracticeTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "practice_test_code")
    private String practiceTestCode;
    @Column(name = "duration")
    private String duration;
    @Lob
    @Column(name = "instructions", columnDefinition = "LONGTEXT")
    private String instructions;
    @Column(name = "minimum_pass_mark")
    private double minimumPassMark;
    @Lob
    @Column(name = "external_link", columnDefinition = "LONGTEXT")
    private String externalLink;
    @Column(name = "question_link", columnDefinition = "LONGTEXT")
    private String questionLink;
     @Column(name = "solution_link", columnDefinition = "LONGTEXT")
    private String solutionLink;
    @Column(name = "is_delete", columnDefinition = "tinyint(1) default 0")
    private Byte isDelete;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "section_curriculum_item_id")
    private SectionCurriculumItem sectionCurriculumItem;
}
