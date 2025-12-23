package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.GeneralUserProfile;
import lk.exon.aethenosapi.entity.InstructorExternalDetails;
import lk.exon.aethenosapi.entity.InstructorProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InstructorExternalDetailsRepository extends JpaRepository<InstructorExternalDetails, Integer> {

    InstructorExternalDetails getInstructorExternalDetailsByInstructorProfile(InstructorProfile instructorProfile);

    List<InstructorExternalDetails> getInstructorExternalDetailssByInstructorProfile(InstructorProfile instructorProfile);
}
