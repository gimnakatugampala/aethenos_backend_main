package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.DiscountType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountTypeRepository extends JpaRepository<DiscountType, Integer> {
    DiscountType getDiscountTypeById(int i);
}