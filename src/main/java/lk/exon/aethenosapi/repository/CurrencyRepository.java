package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, Integer> {
    Currency getCurrencyById(Integer id);

    Currency getCurrencyByName(String currency);
}