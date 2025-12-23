package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.Country;
import lk.exon.aethenosapi.entity.Course;
import lk.exon.aethenosapi.entity.CoursePrice;
import lk.exon.aethenosapi.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoursePriceRepository extends JpaRepository<CoursePrice, Integer> {
    List<CoursePrice> getCoursePriceByCourseId(Integer id);

    CoursePrice getCoursePriceByCountryIdAndCourseId(Integer id, int id1);

    CoursePrice getCoursePriceByCourseCodeAndCountryIdAndCurrencyId(String code,int country_id,int currency_id);

    List<CoursePrice> getCoursePriceByCourseCode(String code);
    CoursePrice getCoursePriceByCourseAndCountry(Course course, Country country);

    List<CoursePrice> getCoursePriceByCourse(Course course);

    CoursePrice getCoursePriceByCourseAndCurrency(Course course, Currency currency);
}