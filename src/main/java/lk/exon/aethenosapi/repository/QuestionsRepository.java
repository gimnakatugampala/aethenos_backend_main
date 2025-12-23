package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.Course;
import lk.exon.aethenosapi.entity.GeneralUserProfile;
import lk.exon.aethenosapi.entity.Questions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionsRepository extends JpaRepository<Questions, Integer> {
    List<Questions> getQuestionsByCourse(Course course);

    Questions getQuestionByCode(String questionCode);

    List<Questions> getQuestionsByCourseAndGeneralUserProfile(Course course, GeneralUserProfile generalUserProfile);
}
