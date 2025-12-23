package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.CourseSection;
import lk.exon.aethenosapi.entity.CurriculumItemType;
import lk.exon.aethenosapi.entity.SectionCurriculumItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionCurriculumItemRepository extends JpaRepository<SectionCurriculumItem, Integer> {

    List<SectionCurriculumItem> getSectionCurriculumItemByCourseSection(CourseSection courseSection);

    SectionCurriculumItem getSectionCurriculumItemByCourseSectionAndTitle(CourseSection getcourseSection, String title);

    List<SectionCurriculumItem> getSectionCurriculumItemsByCourseSection(CourseSection getcourseSection);

    SectionCurriculumItem getSectionCurriculumItemByCourseSectionAndTitleAndCurriculumItemType(CourseSection courseSection, String title, CurriculumItemType curriculumItemType);

    List<SectionCurriculumItem> getSectionCurriculumItemsByCourseSectionAndCurriculumItemType(CourseSection courseSection, CurriculumItemType curriculumItemType);

    SectionCurriculumItem getSectionCurriculumItemById(int lectureId);

    SectionCurriculumItem getSectionCurriculumItemsByCourseSectionAndCurriculumItemTypeAndId(CourseSection courseSection, CurriculumItemType curriculumItemType, Integer sectionCurriculumItemId);

    List<SectionCurriculumItem> findByCourseSectionOrderByArrangedNoAsc(CourseSection courseSection);
}