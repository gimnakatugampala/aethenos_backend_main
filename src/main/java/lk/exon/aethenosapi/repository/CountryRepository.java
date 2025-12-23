package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Integer> {
    Country getCountryByName(String s);

    Country getCountryById(int i);
}