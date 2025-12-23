package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.EmailVerifyGupAccount;
import lk.exon.aethenosapi.entity.GeneralUserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerifyGupAccountRepository extends JpaRepository<EmailVerifyGupAccount, Integer> {
    EmailVerifyGupAccount getEmailVerifyGupAccountByGeneralUserProfile(GeneralUserProfile generalUserProfile);
}
