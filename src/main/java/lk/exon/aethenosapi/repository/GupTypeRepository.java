package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.GeneralUserProfile;
import lk.exon.aethenosapi.entity.GupType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GupTypeRepository extends JpaRepository<GupType, Integer> {
    GupType getGupTypeById(int i);

    GupType getGupTypeById (Integer Id);
}