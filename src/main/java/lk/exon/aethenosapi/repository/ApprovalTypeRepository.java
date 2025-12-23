package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.ApprovalType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalTypeRepository extends JpaRepository<ApprovalType, Integer> {
    ApprovalType getApprovalTypeById(int approvalTypeId);
}