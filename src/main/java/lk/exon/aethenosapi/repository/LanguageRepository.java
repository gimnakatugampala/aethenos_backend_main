package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LanguageRepository extends JpaRepository<Language, Integer> {
    Language getLanguageById(int language);

    List<Language> findAllByOrderByNameAsc();
}