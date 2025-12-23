package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.Course;
import lk.exon.aethenosapi.entity.CourseKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseKeywordRepository extends JpaRepository<CourseKeyword, Integer> {
    List<CourseKeyword> findByCourse(Course course);

    CourseKeyword getCourseKeywordByCourseAndName(Course course, String excel);
}