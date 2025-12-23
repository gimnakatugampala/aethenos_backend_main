package lk.exon.aethenosapi.service;

import lk.exon.aethenosapi.entity.Course;
import org.springframework.stereotype.Service;

import java.util.List;


public interface RecentCoursesService {
    List<Course> findRecentCourses();
}
