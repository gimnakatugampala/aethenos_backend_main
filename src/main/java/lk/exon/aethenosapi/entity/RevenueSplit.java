package lk.exon.aethenosapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "revenue_split")
public class RevenueSplit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "aethenos_revenue")
    private double aethenosRevenue;
    @Column(name = "instructor_revenue")
    private double InstructorRevenue;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "revenue_split_type_id")
    private RevenueSplitType revenueSplitType;

}
