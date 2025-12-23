package lk.exon.aethenosapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "revenue")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Revenue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "order_has_course_id", nullable = false)
    private OrderHasCourse orderHasCourse;
    @Column(name = "tax")
    private double tax;
    @Column(name = "processing_fee")
    private double processingFee;
    @Column(name = "net_sale")
    private double netSale;
    @Column(nullable = false, columnDefinition = "TINYINT(0)")
    private boolean isRefunded;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;
    @Column(name = "created_date")
    private Date createdDate;
}
