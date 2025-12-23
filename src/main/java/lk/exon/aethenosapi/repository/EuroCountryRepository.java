package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.EuroCountry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EuroCountryRepository extends JpaRepository<EuroCountry, Integer> {
    EuroCountry getEuroCountryByName(String country);
}
