package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.GeneralUserProfile;
import lk.exon.aethenosapi.entity.InstructorProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InstructorProfileRepository extends JpaRepository<InstructorProfile, Integer> {
    InstructorProfile getInstructorProfileByGeneralUserProfile(GeneralUserProfile profile);

    InstructorProfile getInstructorProfileByGeneralUserProfileId(Integer id);

    List<InstructorProfile> findInstructorProfilesByGeneralUserProfile_FirstNameContainingIgnoreCaseOrGeneralUserProfile_LastNameContainingIgnoreCase(String keyword, String keyword1);
    List<InstructorProfile> getInstructorProfileByIsVerifiedAndIsProfileCompleted(Byte isVerified, Byte isProfileCompleted);

}