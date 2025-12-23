package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.PromotionType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionTypeRepository extends JpaRepository<PromotionType, Integer> {
    PromotionType getPromotionTypeById(int type);
}