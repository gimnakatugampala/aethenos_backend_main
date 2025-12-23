package lk.exon.aethenosapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "transacton")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "transaction_code")
    private String transactionCode;
    @Column(name = "created_date")
    private Date createdDate;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vat_id", nullable = true)
    private Vat vat;
    @Column(name = "amount")
    private Double amount;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "revenue_split_history_id", nullable = false)
    private RevenueSplitHistory revenueSplitHistory;
    @Column(name = "vat_percentage", nullable = false)
    private Double vatPercentage = 0.0;
    @Column(name = "vat_amount", nullable = false)
    private Double vatAmount = 0.0;
    @Column(name = "usd_rate", nullable = false)
    private Double usdRate = 0.0;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;
    @Column(name = "payment_processing_fee", nullable = false)
    private Double paymentProcessingFee = 0.0;
    @Column(name = "stripe_pf_currency", nullable = true)
    private String stripe_pf_currency;
    @Column(name = "stripe_pf_exchange_rate", nullable = false, columnDefinition = "DOUBLE DEFAULT 1.0")
    private Double stripe_pf_exchange_rate;
}
