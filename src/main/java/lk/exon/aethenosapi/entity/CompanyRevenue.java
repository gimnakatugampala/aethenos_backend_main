package lk.exon.aethenosapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "company_revenue")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompanyRevenue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "revenue_id", nullable = false)
    private Revenue revenue;
    @Column(name = "company_share")
    private double CompanyShare;
}
