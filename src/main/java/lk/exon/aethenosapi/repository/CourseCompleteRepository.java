package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.Course;
import lk.exon.aethenosapi.entity.CourseComplete;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseCompleteRepository extends JpaRepository<CourseComplete, Integer> {
    CourseComplete getCourseCompleteByCourse(Course course);
}
