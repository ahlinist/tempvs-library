package club.tempvs.library.dao;

import static club.tempvs.library.domain.Source.Period;
import static club.tempvs.library.domain.Source.Type;
import static club.tempvs.library.domain.Source.Classification;

import club.tempvs.library.domain.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SourceRepository extends JpaRepository<Source, Long> {

    @Query("SELECT s FROM Source s " +
            "WHERE s.period = :period " +
            "AND s.type IN :types " +
            "AND s.classification IN :classifications " +
            "AND (s.name LIKE %:query% OR s.description LIKE %:query%)")
    List<Source> find(Period period, List<Type> types, List<Classification> classifications, String query);
}
