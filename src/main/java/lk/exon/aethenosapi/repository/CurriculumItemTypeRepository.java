package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.CurriculumItemType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurriculumItemTypeRepository extends JpaRepository<CurriculumItemType, Integer> {

    CurriculumItemType getCurriculumItemTypeById(int i);
}
