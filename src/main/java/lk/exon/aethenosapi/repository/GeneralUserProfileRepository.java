package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.GeneralUserProfile;
import lk.exon.aethenosapi.entity.GupType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GeneralUserProfileRepository extends JpaRepository<GeneralUserProfile, Integer> {

    GeneralUserProfile getGeneralUserProfileByEmail(String email);

    List<GeneralUserProfile> findByGupType_Id(int i);

    GeneralUserProfile getGeneralUserProfileById(int adminId);

    List<GeneralUserProfile> getGeneralUserProfileByGupType(GupType gupType);

    GeneralUserProfile getGeneralUserProfileByUserCode(String userCode);

}

