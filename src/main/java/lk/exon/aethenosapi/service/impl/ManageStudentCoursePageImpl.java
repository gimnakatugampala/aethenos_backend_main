package lk.exon.aethenosapi.service.impl;

import lk.exon.aethenosapi.payload.response.SearchCourseByCodeResponse;
import lk.exon.aethenosapi.payload.response.CourseSearchPageResponse;
import lk.exon.aethenosapi.entity.Course;
import lk.exon.aethenosapi.entity.CourseLandingPage;
import lk.exon.aethenosapi.exception.ErrorException;
import lk.exon.aethenosapi.repository.CourseLandingPageRepository;
import lk.exon.aethenosapi.repository.CourseRepository;
import lk.exon.aethenosapi.service.ManageStudentCoursesService;
import lk.exon.aethenosapi.utils.VarList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class ManageStudentCoursePageImpl implements ManageStudentCoursesService {
    @Autowired
    CourseLandingPageRepository courseLandingPageRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    public void setCourseLandingPageRepository(CourseLandingPageRepository courseLandingPageRepository) {
        this.courseLandingPageRepository = courseLandingPageRepository;
    }

//    <<Course View>>
    @Override
    public SearchCourseByCodeResponse selectCourseByCode(String courseCode) {
        Course course = courseRepository.findCourseByCode(courseCode);

        if (course != null) {
            SearchCourseByCodeResponse searchCourseByCodeResponse = convertToDTO(course);
            log.warn("Course found success!");

            return searchCourseByCodeResponse;
        } else {
            throw new ErrorException("No such data found", VarList.RSP_NO_DATA_FOUND);
        }
    }


    private SearchCourseByCodeResponse convertToDTO(Course course) {

        SearchCourseByCodeResponse dto = new SearchCourseByCodeResponse();

        CourseLandingPage courseLandingPage = courseLandingPageRepository.getCourseLandingPagesByCourse(course);

        dto.setCourseTitle(course.getCourseTitle());
        dto.setCourseSubtitle(courseLandingPage.getSubTitle());
        dto.setCourseDescription(courseLandingPage.getDescription());
        dto.setLanguage(courseLandingPage.getLanguage().getName());
        dto.setCourseLevel(courseLandingPage.getCourseLevel().getName());
        dto.setCourseCode(course.getCode());
        dto.setImageURL(course.getImg());
        dto.setApprovalType(course.getApprovalType().getName());
        dto.setCategory(course.getCourseCategory().getName());
        dto.setInstructor(course.getInstructorId().getGeneralUserProfile().getFirstName());
        dto.setPromotional_video_url(course.getTest_video());

        return dto;
    }

//    <<Course Search>>

    @Override
    public List<CourseSearchPageResponse> searchCourses(String searchTerm) {
        try {
            if (!searchTerm.isEmpty()){
                List<Course> courses = courseRepository.searchCoursesByTerm(searchTerm);
                List<CourseSearchPageResponse> courseDTOs = courses.stream()
                        .map(this::mapCourseToDTO)
                        .collect(Collectors.toList());
                return courseDTOs;
            }else{
                throw new ErrorException("Empty search term! ", VarList.RSP_NO_DATA_FOUND);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();

        }
    }


    private CourseSearchPageResponse mapCourseToDTO(Course course) {
        CourseSearchPageResponse dto = new CourseSearchPageResponse();

        CourseLandingPage courseLandingPage = courseLandingPageRepository.getCourseLandingPagesByCourse(course);

        dto.setId(course.getId());
        dto.setCode(course.getCode());
        dto.setCourseTitle(course.getCourseTitle());
        dto.setSubTitle(courseLandingPage.getSubTitle());
        dto.setCourseLevel(courseLandingPage.getCourseLevel().getName());
        dto.setLanguage(courseLandingPage.getLanguage().getName());
        dto.setImg(course.getImg());
        dto.setCategory(course.getCourseCategory().getName());

        return dto;
    }


}
