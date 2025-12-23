package lk.exon.aethenosapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "approved_refunds")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ApprovedRefunds {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "purchased_date")
    private Date purchasedDate;
    @Column(name = "purchased_amount")
    private double purchasedAmount;
    @Column(name = "refund_amount")
    private double refundAmount;
    @Column(name = "transferred_date")
    private Date transferredDate;
    @Column(name = "currency")
    private String currency;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "refund_status_id")
    private RefundStatus refundStatus;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gup_id")
    private GeneralUserProfile generalUserProfile;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    private Course course;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "refunds_id")
    private Refunds refunds;
}
