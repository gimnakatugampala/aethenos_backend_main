package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.Course;
import lk.exon.aethenosapi.entity.CourseSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseSectionRepository extends JpaRepository<CourseSection, Integer> {

    List<CourseSection> getCourseSectionByCourse(Course course);

    CourseSection getCourseSectionByCourseAndSectionName(Course course, String sectionName);


    CourseSection getCourseSectionByCourseAndId(Course course, int parseInt);

    CourseSection getCourseSectionById(int courseSectionId);

    List<CourseSection> findByCourseOrderByArrangedNoAsc(Course course);
}