package lk.exon.aethenosapi.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@Table(name = "assignment")
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "assignment_code")
    private String assignmentCode;
    @Column(name = "duration")
    private String duration;
    @Lob
    @Column(name = "instructions", columnDefinition = "LONGTEXT")
    private String instructions;
    @Lob
    @Column(name = "external_link", columnDefinition = "LONGTEXT")
    private String externalLink;
    @Column(name = "questions", columnDefinition = "LONGTEXT")
    private String questions;
    @Column(name = "questions_external_link", columnDefinition = "LONGTEXT")
    private String questionsExternalLink;
    @Column(name = "solutions", columnDefinition = "LONGTEXT")
    private String solutions;
    @Column(name = "solutions_external_link", columnDefinition = "LONGTEXT")
    private String solutionsExternalLink;
    @Column(name = "is_delete", columnDefinition = "tinyint(1) default 0")
    private Byte isDelete;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "section_curriculum_item_id")
    private SectionCurriculumItem sectionCurriculumItem;
}
