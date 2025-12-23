package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.Course;
import lk.exon.aethenosapi.entity.ExternalCourse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExternalCourseRepository extends JpaRepository<ExternalCourse, Integer> {
    ExternalCourse getExternalCourseByCourse(Course course);
}
