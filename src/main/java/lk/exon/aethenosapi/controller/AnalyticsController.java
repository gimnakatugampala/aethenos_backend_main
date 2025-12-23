package lk.exon.aethenosapi.controller;

import lk.exon.aethenosapi.payload.response.GetCoursesByInstructorResponse;
import lk.exon.aethenosapi.payload.response.StudentsEnrollResponse;
import lk.exon.aethenosapi.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "analytics")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AnalyticsController {

    @Autowired
    AnalyticsService analyticsService;

    @GetMapping("/getCoursesByInstructor")
    public List<GetCoursesByInstructorResponse> getCoursesByInstructor(){
        return analyticsService.getCoursesByInstructor();
    }

    @GetMapping("/getStudentsEnrollByCourse/{courseCode}")
    public List<StudentsEnrollResponse> getStudentsEnrollByCourse(@PathVariable("courseCode") String courseCode){
        return analyticsService.getStudentsEnrollByCourse(courseCode);
    }

}
