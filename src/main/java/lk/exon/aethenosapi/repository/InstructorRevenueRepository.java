package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.InstructorProfile;
import lk.exon.aethenosapi.entity.InstructorRevenue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InstructorRevenueRepository extends JpaRepository<InstructorRevenue, Integer> {
    InstructorRevenue getInstructorRevenueByInstructorProfileAndRevenueForMonthAndRevenueForYear(InstructorProfile instructorProfile, int month, int year);

    List<InstructorRevenue> getInstructorRevenueByInstructorProfile(InstructorProfile instructorProfile);

    InstructorRevenue getInstructorRevenueById(int id);

    List<InstructorRevenue> getInstructorRevenueByInstructorProfileAndRevenueForYear(InstructorProfile instructorProfile, int year);

    List<InstructorRevenue> getInstructorRevenueByInstructorProfileAndRevenueForYearAndRevenueForMonth(InstructorProfile instructorProfile, int year, int value);
}
