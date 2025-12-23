package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.VerificationApproval;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationApprovalRepository extends JpaRepository<VerificationApproval, Integer> {
}