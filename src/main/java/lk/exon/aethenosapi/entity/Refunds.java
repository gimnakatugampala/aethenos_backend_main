package lk.exon.aethenosapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "refunds")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Refunds {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "refund_amount")
    private Double refundAmount;
    @Column(name = "reason")
    private String reason;
    @Column(name = "comment")
    private String comment;
    @Column(name = "refund_code")
    private String refundCode;
    @Column(name = "request_date")
    private Date requestDate;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "order_has_course_id", nullable = false)
    private OrderHasCourse orderHasCourse;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "gup_id", nullable = false)
    private GeneralUserProfile generalUserProfile;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "refund_status_id", nullable = false)
    private RefundStatus refundStatus;

}
