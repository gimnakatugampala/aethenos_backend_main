package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.GeneralUserProfile;
import lk.exon.aethenosapi.entity.Order;
import lk.exon.aethenosapi.entity.OrderHasCourse;
import lk.exon.aethenosapi.entity.Refunds;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RefundsRepository extends JpaRepository<Refunds, Integer> {

    Refunds getRefundsByRefundCode(String refundCode);

    List<Refunds> getRefundsByGeneralUserProfile(GeneralUserProfile profile);

    Refunds getRefundsByOrderHasCourse(OrderHasCourse orderHasCourse);

    Refunds getRefundsByOrder(Order order);
}
