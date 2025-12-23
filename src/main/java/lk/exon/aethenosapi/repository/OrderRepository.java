package lk.exon.aethenosapi.repository;


import lk.exon.aethenosapi.entity.Course;
import lk.exon.aethenosapi.entity.GeneralUserProfile;
import lk.exon.aethenosapi.entity.Order;
import lk.exon.aethenosapi.entity.OrderHasCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> getOrdersByGeneralUserProfile(GeneralUserProfile profile);
    Order getOrderById(int id);
}
