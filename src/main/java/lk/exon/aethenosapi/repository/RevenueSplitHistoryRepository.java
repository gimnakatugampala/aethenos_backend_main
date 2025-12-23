package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.RevenueSplitHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevenueSplitHistoryRepository extends JpaRepository<RevenueSplitHistory, Integer> {


    RevenueSplitHistory findTopByOrderByChangedDateDesc();
}
