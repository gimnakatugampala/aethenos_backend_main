package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.VerificationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTypeRepository extends JpaRepository<VerificationType, Integer> {
}