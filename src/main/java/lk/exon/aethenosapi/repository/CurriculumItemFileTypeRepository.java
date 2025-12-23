package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.CurriculumItemFileType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurriculumItemFileTypeRepository extends JpaRepository<CurriculumItemFileType, Integer> {
    CurriculumItemFileType getCurriculumItemFileTypeByName(String fileType);

    CurriculumItemFileType getCurriculumItemFileTypeById(int i);
}