package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.OrderHasCourse;
import lk.exon.aethenosapi.entity.Revenue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface RevenueRepository extends JpaRepository<Revenue, Integer> {
    Revenue getRevenueByOrderHasCourse(OrderHasCourse orderHasCourse);


    List<Revenue> getRevenueByCreatedDateBetween(Date from, Date from1);

    Revenue getRevenueById(int id);
}
