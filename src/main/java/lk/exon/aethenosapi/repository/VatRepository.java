package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.Vat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VatRepository extends JpaRepository<Vat,Integer> {
    Vat getVatBycountry(String country);
}
