package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.RefundStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundStatusRepository extends JpaRepository<RefundStatus, Integer> {
    RefundStatus getRefundStatusById(int i);
}
