package lk.exon.aethenosapi.service.impl;

import lk.exon.aethenosapi.entity.Course;
import lk.exon.aethenosapi.repository.CourseRepository;
import lk.exon.aethenosapi.service.RecentCoursesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecentCoursesServiceImpl implements RecentCoursesService {
    @Autowired
    private CourseRepository courseRepository;

    @Override
    public List<Course> findRecentCourses() {
        return courseRepository.findTop4ByOrderByIdDesc();
    }
}
