package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.RevenueSplit;
import lk.exon.aethenosapi.entity.RevenueSplitType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevenueSplitRepository extends JpaRepository<RevenueSplit, Integer> {
    RevenueSplit getRevenueSplitByRevenueSplitType(RevenueSplitType revenueSplitType);
}
