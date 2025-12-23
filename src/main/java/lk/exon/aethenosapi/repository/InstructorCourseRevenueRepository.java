package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.InstructorCourseRevenue;
import lk.exon.aethenosapi.entity.InstructorProfile;
import lk.exon.aethenosapi.entity.Revenue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InstructorCourseRevenueRepository extends JpaRepository<InstructorCourseRevenue, Integer> {
    List<InstructorCourseRevenue> getInstructorCourseRevenueByInstructorProfile(InstructorProfile instructorProfile);

    InstructorCourseRevenue getInstructorCourseRevenueByRevenue(Revenue revenue);

    InstructorCourseRevenue getInstructorCourseRevenueByInstructorProfileAndRevenue(InstructorProfile instructorProfile, Revenue revenue);
}
