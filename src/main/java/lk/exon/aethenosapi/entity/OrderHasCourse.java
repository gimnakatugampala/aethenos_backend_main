package lk.exon.aethenosapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "order_has_course")
public class OrderHasCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    private Course course;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private Order order;
    @Column(name = "item_price")
    private Double itemPrice;
    @Column(name = "list_price")
    private Double listPrice;
    @Column(name = "item_code")
    private String itemCode;
    @Column(name = "currrency")
    private String currrency;
    @Column(name = "progress")
    private double progress;
    @Column(name = "completed_sections", columnDefinition = "LONGTEXT")
    private String completedSections;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_purchase_type_id")
    private CoursePurchaseType coursePurchaseType;
    @Column(name = "certificate")
    private String certificate;
    @Column(name = "is_complete")
    private Byte isComplete;
    @Column(name = "is_delete", columnDefinition = "TINYINT DEFAULT 0")
    private Byte isDelete;

}
