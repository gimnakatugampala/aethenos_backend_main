package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.Order;
import lk.exon.aethenosapi.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction,Integer> {
    Transaction getTransactionByTransactionCode(String transActionCode);

    Transaction getTransactionByOrder(Order order);
}
