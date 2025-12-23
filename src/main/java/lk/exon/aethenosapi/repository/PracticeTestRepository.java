package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.PracticeTest;
import lk.exon.aethenosapi.entity.SectionCurriculumItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PracticeTestRepository extends JpaRepository<PracticeTest, Integer> {
    PracticeTest getPracticeTestById(int id);

    PracticeTest getPracticeTestByPracticeTestCode(String practiceTestCode);

    PracticeTest getPracticeTestBySectionCurriculumItem(SectionCurriculumItem sectionCurriculumItem);
}
