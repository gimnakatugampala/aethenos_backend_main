package lk.exon.aethenosapi.security;

import lk.exon.aethenosapi.entity.GeneralUserProfile;
import lk.exon.aethenosapi.exception.ErrorException;
import lk.exon.aethenosapi.repository.GeneralUserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class JwtUserDetailsServicePassword implements UserDetailsService {

    @Autowired
    GeneralUserProfileRepository generalUserProfileRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        GeneralUserProfile gup = generalUserProfileRepository.getGeneralUserProfileByEmail(username);
        if (gup != null) {
            User user = new User(username, gup.getPassword(), new ArrayList<>());
            if (user != null) {
                return user;
            } else {
                throw new ErrorException("Your password is incorrect.",
                        "Your password is incorrect. Please enter correct password.");
            }
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }

}
