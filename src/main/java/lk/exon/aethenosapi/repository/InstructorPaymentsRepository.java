package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.InstructorPayments;
import lk.exon.aethenosapi.entity.InstructorProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstructorPaymentsRepository extends JpaRepository<InstructorPayments, Integer> {
    InstructorPayments getInstructorPaymentsByInstructorProfile(InstructorProfile instructorProfile);
}
