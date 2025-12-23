package lk.exon.aethenosapi.service.impl;

import lk.exon.aethenosapi.entity.GeneralUserProfile;
import lk.exon.aethenosapi.entity.GupType;
import lk.exon.aethenosapi.entity.InstructorProfile;
import lk.exon.aethenosapi.entity.UserLoginToken;
import lk.exon.aethenosapi.exception.ErrorException;
import lk.exon.aethenosapi.payload.request.UserLoginRequset;
import lk.exon.aethenosapi.payload.response.SuccessResponse;
import lk.exon.aethenosapi.payload.response.UserLoginResponse;
import lk.exon.aethenosapi.repository.GeneralUserProfileRepository;
import lk.exon.aethenosapi.repository.GupTypeRepository;
import lk.exon.aethenosapi.repository.InstructorProfileRepository;
import lk.exon.aethenosapi.repository.UserLoginTokenRepository;
import lk.exon.aethenosapi.security.JwtTokenUtil;
import lk.exon.aethenosapi.security.JwtUserDetailsServicePassword;
import lk.exon.aethenosapi.service.LoginService;
import lk.exon.aethenosapi.utils.VarList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService {
    @Autowired
    private InstructorProfileRepository instructorProfileRepository;
    @Autowired
    private GupTypeRepository gupTypeRepository;

    @Autowired
    private JwtUserDetailsServicePassword userDetailsServicePassword;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    GeneralUserProfileRepository generalUserProfileRepostory;
    @Autowired
    private UserProfileServiceImpl userProfileServiceImpl;
    @Autowired
    private UserLoginTokenRepository userLoginTokenRepository;


    @Override
    public UserLoginResponse instructorLoginWithPassword(UserLoginRequset request, String remoteAddr) throws Exception {
        UserLoginResponse userLoginResponse = new UserLoginResponse();

        GeneralUserProfile gup = generalUserProfileRepostory.getGeneralUserProfileByEmail(request.getEmail());
        if (gup != null) {
            if (gup.getGupType().getId() == 2) {
                if (new BCryptPasswordEncoder().matches(request.getPassword(), gup.getPassword())) {
                    UserDetails userDetails = userDetailsServicePassword.loadUserByUsername(request.getEmail());
                    String token = jwtTokenUtil.generateToken(userDetails);
                    userLoginResponse.setToken(token);
                    userLoginResponse.setFname(gup.getFirstName());
                    userLoginResponse.setLname(gup.getLastName());
                    userLoginResponse.setEmail(gup.getEmail());
                    userLoginResponse.setGup_type(gup.getGupType().getName());
                    userLoginResponse.setCountry(gup.getCountry() == null ? "" : gup.getCountry());
                } else {
                    log.warn("password incorrect.");
                    throw new ErrorException("incorrect password.", "Wrong password.Try again or click Forgot password to reset it.");
                }
            } else {
                InstructorProfile instructorProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(gup);
                if (instructorProfile == null) {
                    InstructorProfile instructorProfile1 = new InstructorProfile();
                    instructorProfile1.setGeneralUserProfile(gup);
                    instructorProfile1.setIsProfileCompleted((byte) 0);
                    instructorProfile1.setCreated_date(new Date());
                    instructorProfile1.setIsVerified((byte) 0);
                    GupType gupType = gupTypeRepository.getGupTypeById(2);
                    if (gupType != null) {
                        gup.setGupType(gupType);
                    } else {
                        throw new ErrorException("Gup Type Not Available", VarList.RSP_NO_DATA_FOUND);
                    }
                    instructorProfileRepository.save(instructorProfile1);
                    if (new BCryptPasswordEncoder().matches(request.getPassword(), gup.getPassword())) {
                        UserDetails userDetails = userDetailsServicePassword.loadUserByUsername(request.getEmail());
                        String token = jwtTokenUtil.generateToken(userDetails);
                        userLoginResponse.setToken(token);
                        userLoginResponse.setFname(gup.getFirstName());
                        userLoginResponse.setLname(gup.getLastName());
                        userLoginResponse.setEmail(gup.getEmail());
                        userLoginResponse.setGup_type(gup.getGupType().getName());
                        userLoginResponse.setCountry(gup.getCountry() == null ? "" : gup.getCountry());
                    } else {
                        log.warn("password incorrect.");
                        throw new ErrorException("incorrect password.", "Wrong password.Try again or click Forgot password to reset it.");
                    }
                } else {
                    if (new BCryptPasswordEncoder().matches(request.getPassword(), gup.getPassword())) {
                        UserDetails userDetails = userDetailsServicePassword.loadUserByUsername(request.getEmail());
                        String token = jwtTokenUtil.generateToken(userDetails);
                        userLoginResponse.setToken(token);
                        userLoginResponse.setFname(gup.getFirstName());
                        userLoginResponse.setLname(gup.getLastName());
                        userLoginResponse.setEmail(gup.getEmail());
                        userLoginResponse.setGup_type(gup.getGupType().getName());
                        userLoginResponse.setCountry(gup.getCountry() == null ? "" : gup.getCountry());
                    } else {
                        log.warn("password incorrect.");
                        throw new ErrorException("incorrect password.", "Wrong password.Try again or click Forgot password to reset it.");
                    }
                }
            }
        } else {
            log.warn("user not found");
            throw new ErrorException("User not found", "This username is not registered as a user.");
        }
        return userLoginResponse;
    }

    @Override
    public SuccessResponse getAccountValidation() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                SuccessResponse successResponse = new SuccessResponse();
                successResponse.setMessage("Valid user");
                successResponse.setVariable(VarList.RSP_SUCCESS);
                return successResponse;
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("Invalid token", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public UserLoginResponse studentLoginWithloginToken(String loginToken) {
        UserLoginToken userLoginToken = userLoginTokenRepository.getUserLoginTokenByLoginToken(loginToken);
        if (userLoginToken == null)
            throw new ErrorException("Invalid login token", VarList.RSP_NO_DATA_FOUND);

        UserLoginResponse userLoginResponse = new UserLoginResponse();

        UserDetails userDetails = userDetailsServicePassword.loadUserByUsername(userLoginToken.getGeneralUserProfile().getEmail());

        //  authenticate(request.getEmail(), request.getPassword());
        String token = null;
        try {
            token = jwtTokenUtil.generateToken(userDetails);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        userLoginResponse.setToken(token);
        userLoginResponse.setFname(userLoginToken.getGeneralUserProfile().getFirstName());
        userLoginResponse.setLname(userLoginToken.getGeneralUserProfile().getLastName());
        userLoginResponse.setEmail(userLoginToken.getGeneralUserProfile().getEmail());
        userLoginResponse.setGup_type(userLoginToken.getGeneralUserProfile().getGupType().getName());
        userLoginResponse.setLoginToken(userLoginToken.getLoginToken());
        return userLoginResponse;

    }

    @Override
    public UserLoginResponse adminLoginWithPassword(UserLoginRequset request, String remoteAddr) throws Exception {
        UserLoginResponse userLoginResponse = new UserLoginResponse();

        GeneralUserProfile gup = generalUserProfileRepostory.getGeneralUserProfileByEmail(request.getEmail());
        if (gup != null) {
            if (gup.getGupType().getId() == 3) {
                if (gup.getIsActive() == 1) {
                    if (new BCryptPasswordEncoder().matches(request.getPassword(), gup.getPassword())) {
                        UserDetails userDetails = userDetailsServicePassword.loadUserByUsername(request.getEmail());
                        String token = jwtTokenUtil.generateToken(userDetails);
                        userLoginResponse.setToken(token);
                        userLoginResponse.setFname(gup.getFirstName());
                        userLoginResponse.setLname(gup.getLastName());
                        userLoginResponse.setEmail(gup.getEmail());
                        userLoginResponse.setGup_type(gup.getGupType().getName());
                    } else {
                        log.warn("password incorrect.");
                        throw new ErrorException("incorrect password.", "Wrong password.Try again or click Forgot password to reset it.");
                    }
                } else {
                    throw new ErrorException("Your admin account deactivated.", "You don't have access to admin panel");
                }

            } else {
                log.warn("not an admin.");
                throw new ErrorException("not an admin.", "You don't have access to become an Admin");
            }
        } else {
            log.warn("user not found");
            throw new ErrorException("User not found", "This username is not registered as a user.");
        }
        return userLoginResponse;
    }

    @Override
    public UserLoginResponse userLoginWithPassword(UserLoginRequset request, String remoteAddr) throws Exception {
        UserLoginResponse userLoginResponse = new UserLoginResponse();

        GeneralUserProfile gup = generalUserProfileRepostory.getGeneralUserProfileByEmail(request.getEmail());
        if (gup != null) {
            if (new BCryptPasswordEncoder().matches(request.getPassword(), gup.getPassword())) {

                UserDetails userDetails = userDetailsServicePassword.loadUserByUsername(request.getEmail());

                //  authenticate(request.getEmail(), request.getPassword());
                String token = jwtTokenUtil.generateToken(userDetails);
                userLoginResponse.setToken(token);
                userLoginResponse.setFname(gup.getFirstName());
                userLoginResponse.setLname(gup.getLastName());
                userLoginResponse.setEmail(gup.getEmail());
                userLoginResponse.setGup_type(gup.getGupType().getName());
                userLoginResponse.setCountry(gup.getCountry() == null ? "" : gup.getCountry());
                UserLoginToken userLoginToken = userLoginTokenRepository.getUserLoginTokenByGeneralUserProfile(gup);
                String loginToken;
                if (userLoginToken == null) {
                    String genUserLoginToken = UUID.randomUUID().toString().replace("-", "");
                    String encodedToken = Base64.getUrlEncoder().withoutPadding().encodeToString(genUserLoginToken.getBytes(StandardCharsets.UTF_8));
                    loginToken = encodedToken;
                    userLoginToken = new UserLoginToken();
                    userLoginToken.setGeneralUserProfile(gup);
                    userLoginToken.setLoginToken(loginToken);
                    userLoginTokenRepository.save(userLoginToken);
                } else {
                    loginToken = userLoginToken.getLoginToken();
                }
                userLoginResponse.setLoginToken(loginToken);
            } else {
                log.warn("password incorrect.");
                throw new ErrorException("incorrect password.", "Wrong password.Try again or click Forgot password to reset it.");
            }

        } else {
            log.warn("user not found");
            throw new ErrorException("User not found", "This username is not registered as a user.");
        }

        return userLoginResponse;


    }

    private void authenticate(String username, String password) throws Exception {
        try {
            System.out.println(username);
            System.out.println(password);
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}