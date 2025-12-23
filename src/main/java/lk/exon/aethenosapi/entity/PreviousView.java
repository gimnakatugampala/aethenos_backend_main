package lk.exon.aethenosapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "previous_view")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PreviousView {
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gup_id")
    private GeneralUserProfile generalUserProfile;
    @Column(name = "duration", nullable = false)
    private double duration = 0;
}
