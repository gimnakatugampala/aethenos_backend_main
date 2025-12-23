package lk.exon.aethenosapi.controller;

import lk.exon.aethenosapi.payload.request.CourseRequest;
import lk.exon.aethenosapi.payload.response.CourseResponse;
import lk.exon.aethenosapi.payload.response.SuccessResponse;
import lk.exon.aethenosapi.service.EditCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "editcourse")
@CrossOrigin(origins = "*",allowedHeaders = "*")
public class EditCourseController {

    @Autowired
    private EditCourseService editCourseService;

    @GetMapping("/getCourseByCode/{courseID}")
    public CourseResponse getCourseByCourseID(@PathVariable String courseID) {
        return editCourseService.getCourseByID(courseID);
    }

    @PostMapping("/updateCourse")
    public SuccessResponse updateCourse(CourseRequest courseRequest){
       return  editCourseService.updateCourse(courseRequest);
    }

}
