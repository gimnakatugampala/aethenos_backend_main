package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.IntendedLearnerType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntendedLearnerTypeRepository extends JpaRepository<IntendedLearnerType, Integer> {
    IntendedLearnerType findById(int id);
}