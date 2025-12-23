package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.RevenueSplitType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevenueSplitTypeRepository extends JpaRepository<RevenueSplitType, Integer> {

    RevenueSplitType getRevenueSplitTypeById(int i);
}
