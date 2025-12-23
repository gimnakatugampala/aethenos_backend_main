package lk.exon.aethenosapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "revenue_split_history")
public class RevenueSplitHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "aethenos_revenue_referral_link_split")
    private double aethenosRevenueReferralLinkSplit;
    @Column(name = "instructor_revenue_referral_link_split")
    private double InstructorRevenueReferralLinkSplit;
    @Column(name = "aethenos_revenue_aethenos_split")
    private double aethenosRevenueAethenosSplit;
    @Column(name = "instructor_revenue_aethenos_split")
    private double InstructorRevenueAethenosSplit;
    @Column(name = "changed_date")
    private Date changedDate;
}
