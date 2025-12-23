package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.CodingExercise;
import lk.exon.aethenosapi.entity.SectionCurriculumItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodingExerciseRepository   extends JpaRepository<CodingExercise, Integer> {
    CodingExercise getCodingExerciseById(int id);

    CodingExercise getCodingExerciseByCodingExerciseCode(String codingExerciseCode);

    CodingExercise getCodingExerciseBySectionCurriculumItem(SectionCurriculumItem sectionCurriculumItem);
}
