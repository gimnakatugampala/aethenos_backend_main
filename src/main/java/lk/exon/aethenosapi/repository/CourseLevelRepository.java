package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.CourseLevel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseLevelRepository extends JpaRepository<CourseLevel, Integer> {
    CourseLevel getCourseLevelById(int level);
}