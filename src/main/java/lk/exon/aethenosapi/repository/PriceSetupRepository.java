package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.PriceSetup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceSetupRepository extends JpaRepository<PriceSetup, Integer> {
List<PriceSetup> findAll();
    PriceSetup getPriceSetupByCountryId(Integer id);
    PriceSetup getPriceSetupByCountryIdAndCurrencyId(Integer country_id,Integer currency_id);
}