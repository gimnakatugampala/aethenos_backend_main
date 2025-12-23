package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.CourseCategory;
import lk.exon.aethenosapi.entity.CourseSubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseSubCategoryRepository extends JpaRepository<CourseSubCategory, Integer> {
    CourseSubCategory getCourseSubCategoryById(int subcategory);

    CourseSubCategory getCourseSubCategoryBySubLinkName(String subLinkName);

    List<CourseSubCategory> getCourseSubCategoriesByCourseCategory(CourseCategory category);
}