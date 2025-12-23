package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.Course;
import lk.exon.aethenosapi.entity.CourseIntentedLearner;
import lk.exon.aethenosapi.entity.IntendedLearnerType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseIntentedLearnerRepository extends JpaRepository<CourseIntentedLearner, Integer> {

    List<CourseIntentedLearner> getCourseIntentedLearnerByCourseId(int id);

    List<CourseIntentedLearner> getCourseIntentedLearnerByCourse(Course course);

    List<CourseIntentedLearner> getCourseIntentedLearnerByCourseAndNameAndIntendedLearnerType(Course course, String intentedLearner, IntendedLearnerType intendedLearnerType);
}