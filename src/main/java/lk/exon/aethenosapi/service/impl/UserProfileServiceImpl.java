package lk.exon.aethenosapi.service.impl;

import lk.exon.aethenosapi.entity.GeneralUserProfile;
import lk.exon.aethenosapi.repository.GeneralUserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserProfileServiceImpl {
    @Autowired
    private GeneralUserProfileRepository generalUserProfileRepository;

    public GeneralUserProfile getProfile(String username) {
        return generalUserProfileRepository.getGeneralUserProfileByEmail(username);
    }

}
