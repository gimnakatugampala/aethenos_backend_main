package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.Answer;
import lk.exon.aethenosapi.entity.Quiz;
import lk.exon.aethenosapi.payload.response.GetQuiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository  extends JpaRepository<Answer, Integer> {

    List<Answer> getAnswerByQuiz(Quiz quiz);
}
