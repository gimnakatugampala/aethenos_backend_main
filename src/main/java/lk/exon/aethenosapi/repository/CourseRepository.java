package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.ApprovalType;
import lk.exon.aethenosapi.entity.Course;
import lk.exon.aethenosapi.entity.CourseCategory;
import lk.exon.aethenosapi.entity.InstructorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    Course findByCode(String courseCode);

    List<Course> findTop4ByOrderByIdDesc();

    List<Course> getAllByInstructorId(InstructorProfile instructorProfile);

    @Query("SELECT c FROM Course c WHERE LOWER(c.courseTitle) LIKE %:searchTerm% OR LOWER(c.courseTitle) LIKE %:searchTerm%  OR LOWER(c.instructorId.generalUserProfile.firstName) LIKE %:searchTerm% OR LOWER(c.instructorId.generalUserProfile.lastName) LIKE %:searchTerm%")
    List<Course> searchCoursesByTerm(String searchTerm);

    Course findCourseByCode(String code);

    Course getCourseByCode(String courseID);

    Course getCourseByCourseTitle(String courseTitle);

    List<Course> getCourseByApprovalType(ApprovalType approvalTypeById);

    List<Course> getCourseByCourseCategory(CourseCategory courseCategoryByLinkName);
    List<Course> findTop8ByCourseCategory(CourseCategory courseCategoryByLinkName);
    List<Course> getCourseByCourseCategoryOrderByBuyCountDesc(CourseCategory courseCategory);

    List<Course> findByCreatedDateAfter(Date createdDate);

    List<Course> getCourseByCourseCategoryAndCreatedDateAfter(CourseCategory courseCategoryByLinkName, Date oneWeekAgoDate);

    List<Course> getCourseByCourseCategoryAndCreatedDateAfterOrderByBuyCountDesc(CourseCategory courseCategoryByLinkName, Date oneWeekAgoDate);

    List<Course> getCourseByInstructorId(InstructorProfile instructorProfile);

    List<Course> getCourseByCourseCategoryAndApprovalTypeIdOrderByBuyCountDesc(CourseCategory courseCategoryByLinkName, int i);

    List<Course> getCourseByCourseCategoryAndApprovalTypeIdAndCreatedDateAfterOrderByBuyCountDesc(CourseCategory courseCategoryByLinkName, int i, Date oneMonthAgoDate);

    List<Course> findTop8ByCourseCategoryOrderByCreatedDateDesc(CourseCategory courseCategoryByLinkName);

    List<Course> getCoursesByCourseCategoryOrderByBuyCountDesc(CourseCategory courseCategoryByLinkName);


    List<Course> findFirst12ByCourseCategoryOrderByBuyCountDesc(CourseCategory courseCategoryByLinkName);

    List<Course> getCoursesByIsPaidOrderByCreatedDateDesc(int i);

    List<Course> findCoursesByCourseTitleContainingIgnoreCase(String keyword);

    List<Course> findCoursesByInstructorId_GeneralUserProfile_FirstNameContainingIgnoreCaseOrInstructorId_GeneralUserProfile_LastNameContainingIgnoreCase(String keyword, String keyword1);


    Course getCourseByReferralCode(String referralCode);

    List<Course> findByCodeIn(List<String> courseCodes);
}


