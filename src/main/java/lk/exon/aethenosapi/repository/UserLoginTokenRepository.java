package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.GeneralUserProfile;
import lk.exon.aethenosapi.entity.UserLoginToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLoginTokenRepository extends JpaRepository<UserLoginToken,Integer> {
    UserLoginToken getUserLoginTokenByLoginToken(String loginToken);

    UserLoginToken getUserLoginTokenByGeneralUserProfile(GeneralUserProfile gup);
}
