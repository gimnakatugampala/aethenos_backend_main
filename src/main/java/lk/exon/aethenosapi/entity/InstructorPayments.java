package lk.exon.aethenosapi.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@Table(name = "instructor_payments")
public class InstructorPayments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "paypal_username", length = 45)
    private String PaypalUserName;

    @Column(name = "paypal_email", length = 45)
    private String paypalEmail;
    @Column(name = "payoneer_username", length = 45)
    private String payoneerUserName;

    @Column(name = "payoneer_email", length = 45)
    private String payoneerEmail;
    @Column(name = "accountNumber")
    private String accountNumber;
    @Column(name = "sort1")
    private String sort1;
    @Column(name = "sort2")
    private String sort2;
    @Column(name = "sort3")
    private String sort3;
    @Column(name = "bank_account_name")
    private String bankAccountName;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "Instructor_profile_id", nullable = false)
    private InstructorProfile instructorProfile;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "payment_method_id", nullable = false)
    private PaymentMethod paymentMethod;
}
