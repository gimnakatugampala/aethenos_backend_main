package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface CourseLandingPageRepository extends JpaRepository<CourseLandingPage, Integer> {
    CourseLandingPage findByCourseId(int id);

    CourseLandingPage getCourseLandingPageByCourseCode(String courseCode);

    CourseLandingPage getCourseLandingPageByCourse(Course course);

    CourseLandingPage getCourseLandingPagesByCourse(Course course);

    List<CourseLandingPage> getCourseLandingPageBySubcategory(CourseSubCategory courseSubCategory);

    List<CourseLandingPage> findCourseLandingPagesByCourse_CourseTitleContainingIgnoreCaseOrSubTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String courseTitle, String subTitle, String description);


    List<CourseLandingPage> getCourseLandingPagesByTopic(Topic topic);

    List<CourseLandingPage> findCourseLandingPagesByCourse_CourseTitleContainingIgnoreCaseOrSubTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndCourse_CreatedDateAfter(String keyword, String keyword1, String keyword2, Date oneMonthAgoDate);


    List<CourseLandingPage> getCourseLandingPagesByCourseLevelAndTopic(CourseLevel courseLevel, Topic topic);

    List<CourseLandingPage> getCourseLandingPagesBySubcategory(CourseSubCategory courseSubCategory);

}