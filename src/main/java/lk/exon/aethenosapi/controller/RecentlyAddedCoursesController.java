package lk.exon.aethenosapi.controller;

import lk.exon.aethenosapi.entity.Course;
import lk.exon.aethenosapi.repository.CourseRepository;
import lk.exon.aethenosapi.service.RecentCoursesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("RecentCourses")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RecentlyAddedCoursesController {

    @Autowired
    private RecentCoursesService recentCoursesService;
    @GetMapping("/view")
    public @ResponseBody Iterable<Course> loadCourses(){
        return recentCoursesService.findRecentCourses();
    }

}
