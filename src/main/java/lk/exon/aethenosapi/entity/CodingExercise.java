package lk.exon.aethenosapi.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@Table(name = "coding_exercise")
public class CodingExercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "coding_exercise_code")
    private String codingExerciseCode;
    @Lob
    @Column(name = "instructions", columnDefinition = "LONGTEXT")
    private String instructions;
    @Lob
    @Column(name = "external_link", columnDefinition = "LONGTEXT")
    private String externalLink;
    @Column(name = "coding_link", columnDefinition = "LONGTEXT")
    private String codingLink;
    @Column(name = "solution_link", columnDefinition = "LONGTEXT")
    private String solutionLink;
    @Column(name = "is_delete", columnDefinition = "tinyint(1) default 0")
    private Byte isDelete;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "section_curriculum_item_id")
    private SectionCurriculumItem sectionCurriculumItem;
}
