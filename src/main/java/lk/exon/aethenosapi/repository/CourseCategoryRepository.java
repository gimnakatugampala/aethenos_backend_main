package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.CourseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseCategoryRepository extends JpaRepository<CourseCategory, Integer> {
    CourseCategory getCourseCategoryById(int category);

    CourseCategory getCourseCategoryByLinkName(String linkName);

}