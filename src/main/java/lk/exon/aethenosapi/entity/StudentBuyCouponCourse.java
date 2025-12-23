package lk.exon.aethenosapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "student_buy_coupon_course")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StudentBuyCouponCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "order_has_course_id", nullable = false)
    private OrderHasCourse orderHasCourse;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;
}
