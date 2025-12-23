package lk.exon.aethenosapi.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "generalUserProfile_id")
    private GeneralUserProfile generalUserProfile;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "paymentMethod_id")
    private PaymentMethod paymentMethod;
    @Column(name = "buy_date")
    private Date buyDate;
    @Column(name = "total")
    private Double total;
    @Column(name = "discount")
    private Double discount;
    @Column(name = "currency")
    private String currency;
}
