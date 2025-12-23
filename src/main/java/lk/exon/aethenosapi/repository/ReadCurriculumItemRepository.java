package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.OrderHasCourse;
import lk.exon.aethenosapi.entity.ReadCurriculumItem;
import lk.exon.aethenosapi.entity.SectionCurriculumItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReadCurriculumItemRepository extends JpaRepository<ReadCurriculumItem, Integer> {
    ReadCurriculumItem getReadCurriculumItemByOrderHasCourseAndSectionCurriculumItem(OrderHasCourse orderHasCourse, SectionCurriculumItem sectionCurriculumItem);

    List<ReadCurriculumItem> getReadCurriculumItemByOrderHasCourse(OrderHasCourse orderHasCourse);

    List<ReadCurriculumItem> getReadCurriculumItemBySectionCurriculumItem(SectionCurriculumItem sectionCurriculumItem);
}
