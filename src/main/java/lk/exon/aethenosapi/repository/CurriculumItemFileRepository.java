package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.CurriculumItemFile;
import lk.exon.aethenosapi.entity.CurriculumItemFileType;
import lk.exon.aethenosapi.entity.SectionCurriculumItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurriculumItemFileRepository extends JpaRepository<CurriculumItemFile, Integer> {
    List<CurriculumItemFile> getCurriculumItemFileBySectionCurriculumItem(SectionCurriculumItem sectionCurriculumItem);

    CurriculumItemFile getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(SectionCurriculumItem sectionCurriculumItem, CurriculumItemFileType curriculumItemFileType);

    CurriculumItemFile getCurriculumItemFileByUrl(String url);

    CurriculumItemFile getCurriculumItemFileById(Integer curriculumItemFileId);
}