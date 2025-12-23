package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.CoursePurchaseType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoursePurchaseTypeRepository extends JpaRepository<CoursePurchaseType, Integer> {
    CoursePurchaseType getCoursePurchaseTypeById(int i);
}
