package lk.exon.aethenosapi.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.YearMonth;

@Entity
@Table(name = "instructor_revenue")
@Getter
@Setter
public class InstructorRevenue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "gross_revenue")
    private double grossRevenue;
    @Column(name = "net_revenue")
    private double netRevenue;
    @Column(name = "revenue_calculate_date")
    private LocalDate revenueCalculateDate;
    @Column(name = "revenue_for_month", nullable = false)
    private int revenueForMonth;

    @Column(name = "revenue_for_year", nullable = false)
    private int revenueForYear;
    @Column(name = "is_paid", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Byte isPaid;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "instructor_profile_id", nullable = false)
    private InstructorProfile instructorProfile;

    public void setRevenueForYearMonth(YearMonth yearMonth) {
        this.revenueForYear = yearMonth.getYear();
        this.revenueForMonth = yearMonth.getMonthValue();
    }

    public YearMonth getRevenueForYearMonth() {
        return YearMonth.of(revenueForYear, revenueForMonth);
    }
}
