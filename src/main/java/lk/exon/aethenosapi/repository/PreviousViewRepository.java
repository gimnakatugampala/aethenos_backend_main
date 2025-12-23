package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.GeneralUserProfile;
import lk.exon.aethenosapi.entity.OrderHasCourse;
import lk.exon.aethenosapi.entity.PreviousView;
import lk.exon.aethenosapi.entity.SectionCurriculumItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreviousViewRepository extends JpaRepository<PreviousView, Integer> {

    PreviousView getPreviousViewByOrderHasCourseAndGeneralUserProfile(OrderHasCourse orderHasCourse, GeneralUserProfile profile);

    PreviousView getPreviousViewBySectionCurriculumItemAndGeneralUserProfile(SectionCurriculumItem sectionCurriculumItem, GeneralUserProfile profile);

    List<PreviousView> getPreviousViewBySectionCurriculumItem(SectionCurriculumItem sectionCurriculumItem);
}
