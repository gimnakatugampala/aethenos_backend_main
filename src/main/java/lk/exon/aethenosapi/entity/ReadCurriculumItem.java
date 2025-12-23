package lk.exon.aethenosapi.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@Table(name = "read_curriculum_item")
public class ReadCurriculumItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_has_course_id")
    private OrderHasCourse orderHasCourse;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "curriculum_item_id")
    private SectionCurriculumItem sectionCurriculumItem;
}
