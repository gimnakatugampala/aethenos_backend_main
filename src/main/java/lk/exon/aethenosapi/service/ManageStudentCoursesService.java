package lk.exon.aethenosapi.service;


import lk.exon.aethenosapi.payload.response.SearchCourseByCodeResponse;
import lk.exon.aethenosapi.payload.response.CourseSearchPageResponse;

import java.util.List;

public interface ManageStudentCoursesService {

    SearchCourseByCodeResponse selectCourseByCode (String courseCode) throws Exception;

    List<CourseSearchPageResponse> searchCourses(String searchTerm) throws Exception;


}
