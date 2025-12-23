package lk.exon.aethenosapi.controller;


import lk.exon.aethenosapi.payload.response.SearchCourseByCodeResponse;
import lk.exon.aethenosapi.payload.response.CourseSearchPageResponse;
import lk.exon.aethenosapi.service.ManageStudentCoursesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/manageStudentCourse")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j

public class ManageStudentCoursesController {
    private final ManageStudentCoursesService ManageStudentCoursesService;

    @Autowired
    public ManageStudentCoursesController(ManageStudentCoursesService manageStudentCoursesService) {
        this.ManageStudentCoursesService = manageStudentCoursesService;
    }

    @GetMapping("/{courseCode}")
    public SearchCourseByCodeResponse CourseLandingPageResponse(@PathVariable String courseCode) {
        try {
         return ManageStudentCoursesService.selectCourseByCode(courseCode);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


    @GetMapping("/search")
    public List<CourseSearchPageResponse> searchCourses(@RequestParam(name = "searchTerm") String searchTerm) {
        try {
             return ManageStudentCoursesService.searchCourses(searchTerm);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

}
