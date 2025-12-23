package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.Quiz;
import lk.exon.aethenosapi.entity.SectionCurriculumItem;
import lk.exon.aethenosapi.payload.response.GetQuiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository  extends JpaRepository<Quiz, Integer> {

    Quiz getQuizById(int quizId);

    List<Quiz> getQuizBySectionCurriculumItem(SectionCurriculumItem sectionCurriculumItem);
}
