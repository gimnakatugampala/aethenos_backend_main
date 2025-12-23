package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Integer> {
    PaymentMethod getPaymentMethodById(int parseInt);
}
