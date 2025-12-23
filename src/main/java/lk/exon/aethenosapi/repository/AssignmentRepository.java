package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.Assignment;
import lk.exon.aethenosapi.entity.PracticeTest;
import lk.exon.aethenosapi.entity.SectionCurriculumItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository   extends JpaRepository<Assignment, Integer> {
Assignment getAssignmentById(int id);

    Assignment getAssignmentByAssignmentCode(String assignmentCode);

    Assignment getAssignmentBySectionCurriculumItem(SectionCurriculumItem sectionCurriculumItem);
}
