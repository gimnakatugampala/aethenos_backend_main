package lk.exon.aethenosapi.entity;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.Instant;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@Table(name = "promotion_coupons")
public class PromotionCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    private String code;

    private String description;

    private Double amount;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date expire_date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "promotion_type_id")
    private PromotionType promotionType;

    private Byte is_active;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    private Course course;
}