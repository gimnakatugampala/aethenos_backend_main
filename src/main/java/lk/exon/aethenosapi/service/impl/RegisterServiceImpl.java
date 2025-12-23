package lk.exon.aethenosapi.service.impl;


import lk.exon.aethenosapi.config.EmailConfig;
import lk.exon.aethenosapi.config.PasswordEncoderConfig;
import lk.exon.aethenosapi.entity.*;
import lk.exon.aethenosapi.exception.ErrorException;
import lk.exon.aethenosapi.payload.request.CheckUserEmailVerificationCodeRequest;
import lk.exon.aethenosapi.payload.request.GeneralUserProfileRequest;
import lk.exon.aethenosapi.payload.request.StudentRegistrationRequest;
import lk.exon.aethenosapi.payload.response.CheckUserEmailVerificationCodeResponse;
import lk.exon.aethenosapi.payload.response.GeneralUserProfileResponse;
import lk.exon.aethenosapi.payload.response.SuccessResponse;
import lk.exon.aethenosapi.repository.*;
import lk.exon.aethenosapi.security.JwtTokenUtil;
import lk.exon.aethenosapi.security.JwtUserDetailsServicePassword;

import lk.exon.aethenosapi.utils.EmailSender;
import lk.exon.aethenosapi.service.RegisterService;
import lk.exon.aethenosapi.utils.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;

@Service
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private GeneralUserProfileRepository generalUserProfileRepository;

    @Autowired
    private JwtUserDetailsServicePassword userDetailsServicePassword;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private InstructorProfileRepository instructorProfileRepository;

    @Autowired
    private GupTypeRepository gupTypeRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private UserLoginTokenRepository userLoginTokenRepository;
    @Autowired
    private EmailVerifyGupAccountRepository emailVerifyGupAccountRepository;

    @Override
    public GeneralUserProfileResponse saveUser(GeneralUserProfileRequest generalUserProfileRequest) {
        String email = generalUserProfileRequest.getEmail();
        String firstName = generalUserProfileRequest.getFirstName();
        String lastName = generalUserProfileRequest.getLastName();
        String password = generalUserProfileRequest.getPassword();
        Integer gupType = generalUserProfileRequest.getGup_type();
        String countryName = generalUserProfileRequest.getCountryName();


        if (email == null || email.isEmpty() || firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty() || password == null || password.isEmpty() || gupType == null || gupType.toString().isEmpty() || countryName == null || countryName.isEmpty()) {
            throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
        }

        GeneralUserProfile generalUserProfile = generalUserProfileRepository.getGeneralUserProfileByEmail(generalUserProfileRequest.getEmail());

        if (generalUserProfile == null) {

            generalUserProfile = new GeneralUserProfile();
            generalUserProfile.setEmail(email);
            generalUserProfile.setUserCode(UUID.randomUUID().toString());
            generalUserProfile.setFirstName(firstName);
            generalUserProfile.setLastName(lastName);
            generalUserProfile.setCountry(countryName);
            PasswordEncoderConfig by = new PasswordEncoderConfig();
            String encryptedPwd = by.passwordEncoder().encode(generalUserProfileRequest.getPassword());
            generalUserProfile.setPassword(encryptedPwd);
            generalUserProfile.setRegisteredDate(new Date());
            generalUserProfile.setIsActive((byte) 1);
            GupType gupTypeObj = gupTypeRepository.getGupTypeById(generalUserProfileRequest.getGup_type());
            if (gupTypeObj == null) {
                throw new ErrorException("Invalid gup type id", VarList.RSP_NO_DATA_FOUND);
            }
            generalUserProfile.setGupType(gupTypeObj);


            generalUserProfileRepository.save(generalUserProfile);

            if (generalUserProfileRequest.getGup_type() == 2) {
                InstructorProfile instructorProfile1 = new InstructorProfile();
                instructorProfile1.setGeneralUserProfile(generalUserProfile);
                instructorProfile1.setIsProfileCompleted((byte) 0);
                instructorProfile1.setCreated_date(new Date());
                instructorProfile1.setIsVerified((byte) 0);
                instructorProfileRepository.save(instructorProfile1);
            }

            UserDetails userDetails = userDetailsServicePassword.loadUserByUsername(generalUserProfile.getEmail());
            String token;
            try {
                token = jwtTokenUtil.generateToken(userDetails);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            GeneralUserProfileResponse generalUserProfileResponse = new GeneralUserProfileResponse();

            Properties properties = EmailConfig.getEmailProperties(generalUserProfile.getFirstName() + " " + generalUserProfile.getLastName(), "User Registration");
            try {
                EmailSender emailSender = new EmailSender();
                emailSender.sendEmail("UserRegistrationCompletedMessage", generalUserProfile.getEmail(), (String) properties.getProperty("from"), (String) properties.get("subject"), properties);

                properties = EmailConfig.getEmailProperties(generalUserProfile.getFirstName() + " " + generalUserProfile.getLastName(), "Aethenos Email Verification");
                List<Integer> verficationCodeList = generateVerificationCode();
                String verificationCode = convertListToString(verficationCodeList);

                EmailVerifyGupAccount emailVerifyGupAccount = new EmailVerifyGupAccount();
                emailVerifyGupAccount.setGeneralUserProfile(generalUserProfile);
                emailVerifyGupAccount.setEmailVerificationCode(verificationCode);
                emailVerifyGupAccount.setIsVerify((byte) 0);
                emailVerifyGupAccountRepository.save(emailVerifyGupAccount);

                properties.put("verificationCode", verificationCode);
                emailSender.sendEmail("EmailVerificationMessage", generalUserProfile.getEmail(), (String) properties.getProperty("from"), (String) properties.get("subject"), properties);
                generalUserProfileResponse.setCode(generalUserProfile.getUserCode());
                generalUserProfileResponse.setToken(token);

                UserLoginToken userLoginToken = new UserLoginToken();

                String genUserLoginToken = UUID.randomUUID().toString().replace("-", "");
                String encodedToken = Base64.getUrlEncoder().withoutPadding().encodeToString(genUserLoginToken.getBytes(StandardCharsets.UTF_8));


                generalUserProfileResponse.setLoginToken(encodedToken);

                userLoginToken.setLoginToken(encodedToken);
                userLoginToken.setGeneralUserProfile(generalUserProfile);
                userLoginTokenRepository.save(userLoginToken);

            } catch (MessagingException e) {
                e.printStackTrace();
            }
            generalUserProfileResponse.setMessage("User profile added successfully");
            generalUserProfileResponse.setVariable(VarList.RSP_SUCCESS);
            return generalUserProfileResponse;


        } else {
            throw new ErrorException("User already exists", VarList.RSP_NO_DATA_FOUND);
        }
    }

    private List<Integer> generateVerificationCode() {
        final int NUMBER_OF_INTEGERS = 5;
        final int BOUND = 10;
        final SecureRandom random = new SecureRandom();
        List<Integer> integers = new ArrayList<>(NUMBER_OF_INTEGERS);

        for (int i = 0; i < NUMBER_OF_INTEGERS; i++) {
            integers.add(random.nextInt(BOUND));
        }

        return integers;
    }

    private String convertListToString(List<Integer> integers) {
        StringBuilder sb = new StringBuilder();
        for (int number : integers) {
            sb.append(number);
        }
        return sb.toString().trim();
    }

    @Override
    public List<GeneralUserProfile> getAllUsers() {
        return generalUserProfileRepository.findAll();
    }

    @Override
    public GeneralUserProfileResponse studentRegistration(StudentRegistrationRequest studentRegistrationRequest) {
        String email = studentRegistrationRequest.getEmail();
        String firstName = studentRegistrationRequest.getFirstName();
        String lastName = studentRegistrationRequest.getLastName();
        String password = studentRegistrationRequest.getPassword();
        String countryName = studentRegistrationRequest.getCountryName();


        if (email == null || email.isEmpty() || firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty() || password == null || password.isEmpty() || countryName == null || countryName.isEmpty()) {
            throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
        }

        GeneralUserProfile generalUserProfile = generalUserProfileRepository.getGeneralUserProfileByEmail(email);

        if (generalUserProfile == null) {

            generalUserProfile = new GeneralUserProfile();
            generalUserProfile.setEmail(email);
            generalUserProfile.setUserCode(UUID.randomUUID().toString());
            generalUserProfile.setFirstName(firstName);
            generalUserProfile.setLastName(lastName);
            generalUserProfile.setCountry(countryName);
            PasswordEncoderConfig by = new PasswordEncoderConfig();
            String encryptedPwd = by.passwordEncoder().encode(password);
            generalUserProfile.setPassword(encryptedPwd);
            generalUserProfile.setRegisteredDate(new Date());
            generalUserProfile.setIsActive((byte) 1);
            GupType gupTypeObj = gupTypeRepository.getGupTypeById(1);
            if (gupTypeObj == null) {
                throw new ErrorException("Invalid gup type id", VarList.RSP_NO_DATA_FOUND);
            }
            generalUserProfile.setGupType(gupTypeObj);


            generalUserProfileRepository.save(generalUserProfile);
//
//            if (generalUserProfileRequest.getGup_type() == 2) {
//                InstructorProfile instructorProfile1 = new InstructorProfile();
//                instructorProfile1.setGeneralUserProfile(generalUserProfile);
//                instructorProfile1.setIsProfileCompleted((byte) 0);
//                instructorProfile1.setCreated_date(new Date());
//                instructorProfile1.setIsVerified((byte) 0);
//                instructorProfileRepository.save(instructorProfile1);
//            }

            UserDetails userDetails = userDetailsServicePassword.loadUserByUsername(generalUserProfile.getEmail());
            String token;
            try {
                token = jwtTokenUtil.generateToken(userDetails);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            GeneralUserProfileResponse generalUserProfileResponse = new GeneralUserProfileResponse();

            Properties properties = EmailConfig.getEmailProperties(generalUserProfile.getFirstName() + " " + generalUserProfile.getLastName(), "User Registration");
            try {
                EmailSender emailSender = new EmailSender();
                emailSender.sendEmail("UserRegistrationCompletedMessage", generalUserProfile.getEmail(), (String) properties.getProperty("from"), (String) properties.get("subject"), properties);
                generalUserProfileResponse.setCode(generalUserProfile.getUserCode());
                generalUserProfileResponse.setToken(token);

                UserLoginToken userLoginToken = new UserLoginToken();

                String genUserLoginToken = UUID.randomUUID().toString().replace("-", "");
                String encodedToken = Base64.getUrlEncoder().withoutPadding().encodeToString(genUserLoginToken.getBytes(StandardCharsets.UTF_8));


                generalUserProfileResponse.setLoginToken(encodedToken);

                userLoginToken.setLoginToken(encodedToken);
                userLoginToken.setGeneralUserProfile(generalUserProfile);
                userLoginTokenRepository.save(userLoginToken);

            } catch (MessagingException e) {
                e.printStackTrace();
            }
            generalUserProfileResponse.setMessage("Student profile added successfully");
            generalUserProfileResponse.setVariable(VarList.RSP_SUCCESS);
            return generalUserProfileResponse;


        } else {
            throw new ErrorException("User already exists", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public GeneralUserProfileResponse instructorRegistration(StudentRegistrationRequest studentRegistrationRequest) {
        String email = studentRegistrationRequest.getEmail();
        String firstName = studentRegistrationRequest.getFirstName();
        String lastName = studentRegistrationRequest.getLastName();
        String password = studentRegistrationRequest.getPassword();
        String countryName = studentRegistrationRequest.getCountryName();


        if (email == null || email.isEmpty() || firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty() || password == null || password.isEmpty() || countryName == null || countryName.isEmpty()) {
            throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
        }

        GeneralUserProfile generalUserProfile = generalUserProfileRepository.getGeneralUserProfileByEmail(email);

        if (generalUserProfile == null) {

            generalUserProfile = new GeneralUserProfile();
            generalUserProfile.setEmail(email);
            generalUserProfile.setUserCode(UUID.randomUUID().toString());
            generalUserProfile.setFirstName(firstName);
            generalUserProfile.setLastName(lastName);
            generalUserProfile.setCountry(countryName);
            PasswordEncoderConfig by = new PasswordEncoderConfig();
            String encryptedPwd = by.passwordEncoder().encode(password);
            generalUserProfile.setPassword(encryptedPwd);
            generalUserProfile.setRegisteredDate(new Date());
            generalUserProfile.setIsActive((byte) 1);
            GupType gupTypeObj = gupTypeRepository.getGupTypeById(2);
            if (gupTypeObj == null) {
                throw new ErrorException("Invalid gup type id", VarList.RSP_NO_DATA_FOUND);
            }
            generalUserProfile.setGupType(gupTypeObj);


            generalUserProfileRepository.save(generalUserProfile);

            InstructorProfile instructorProfile1 = new InstructorProfile();
            instructorProfile1.setGeneralUserProfile(generalUserProfile);
            instructorProfile1.setIsProfileCompleted((byte) 0);
            instructorProfile1.setCreated_date(new Date());
            instructorProfile1.setIsVerified((byte) 0);
            instructorProfileRepository.save(instructorProfile1);

            UserDetails userDetails = userDetailsServicePassword.loadUserByUsername(generalUserProfile.getEmail());
            String token;
            try {
                token = jwtTokenUtil.generateToken(userDetails);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            GeneralUserProfileResponse generalUserProfileResponse = new GeneralUserProfileResponse();

            Properties properties = EmailConfig.getEmailProperties(generalUserProfile.getFirstName() + " " + generalUserProfile.getLastName(), "Instructor Registration");
            try {
                EmailSender emailSender = new EmailSender();
                emailSender.sendEmail("UserRegistrationCompletedMessage", generalUserProfile.getEmail(), (String) properties.getProperty("from"), (String) properties.get("subject"), properties);
                generalUserProfileResponse.setCode(generalUserProfile.getUserCode());
                generalUserProfileResponse.setToken(token);

                UserLoginToken userLoginToken = new UserLoginToken();

                String genUserLoginToken = UUID.randomUUID().toString().replace("-", "");
                String encodedToken = Base64.getUrlEncoder().withoutPadding().encodeToString(genUserLoginToken.getBytes(StandardCharsets.UTF_8));


                generalUserProfileResponse.setLoginToken(encodedToken);

                userLoginToken.setLoginToken(encodedToken);
                userLoginToken.setGeneralUserProfile(generalUserProfile);
                userLoginTokenRepository.save(userLoginToken);

            } catch (MessagingException e) {
                e.printStackTrace();
            }
            generalUserProfileResponse.setMessage("Instructor profile added successfully");
            generalUserProfileResponse.setVariable(VarList.RSP_SUCCESS);
            return generalUserProfileResponse;


        } else {
            throw new ErrorException("User already exists", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public CheckUserEmailVerificationCodeResponse checkUserEmailVerificationCode(CheckUserEmailVerificationCodeRequest checkUserEmailVerificationCodeRequest) {
        final String email = checkUserEmailVerificationCodeRequest.getEmail();
        final String verificationCode = checkUserEmailVerificationCodeRequest.getVerificationCode();

        if (email == null || email.isEmpty())
            throw new ErrorException("Please add an email", VarList.RSP_NO_DATA_FOUND);
        if (verificationCode == null || verificationCode.isEmpty())
            throw new ErrorException("Please add the verification code", VarList.RSP_NO_DATA_FOUND);

        GeneralUserProfile generalUserProfile = generalUserProfileRepository.getGeneralUserProfileByEmail(email);
        if (generalUserProfile == null)
            throw new ErrorException("Not a registered email", VarList.RSP_NO_DATA_FOUND);

        EmailVerifyGupAccount emailVerifyGupAccount = emailVerifyGupAccountRepository.getEmailVerifyGupAccountByGeneralUserProfile(generalUserProfile);

        if (emailVerifyGupAccount == null)
            throw new ErrorException("Not a registered email", VarList.RSP_NO_DATA_FOUND);
        CheckUserEmailVerificationCodeResponse checkUserEmailVerificationCodeResponse = new CheckUserEmailVerificationCodeResponse();
        if (emailVerifyGupAccount.getEmailVerificationCode().equals(verificationCode)) {
            emailVerifyGupAccount.setIsVerify((byte) 1);
            emailVerifyGupAccountRepository.save(emailVerifyGupAccount);
            checkUserEmailVerificationCodeResponse.setMessage("Email verified successfully");
        } else {
            checkUserEmailVerificationCodeResponse.setMessage("The email was not verified because the verification code is invalid");
        }

        return checkUserEmailVerificationCodeResponse;

    }

    @Override
    public SuccessResponse resendUserEmailVerificationCode(String email) {
        GeneralUserProfile generalUserProfile = generalUserProfileRepository.getGeneralUserProfileByEmail(email);
        if (generalUserProfile == null)
            throw new ErrorException("Invalid email", VarList.RSP_NO_DATA_FOUND);

        EmailVerifyGupAccount emailVerifyGupAccount = emailVerifyGupAccountRepository.getEmailVerifyGupAccountByGeneralUserProfile(generalUserProfile);

        if (emailVerifyGupAccount == null) {
            emailVerifyGupAccount = new EmailVerifyGupAccount();
            emailVerifyGupAccount.setGeneralUserProfile(generalUserProfile);
            emailVerifyGupAccount.setIsVerify((byte) 0);
        }

        if (emailVerifyGupAccount != null && emailVerifyGupAccount.getIsVerify() == 1)
            throw new ErrorException("Email already verified", VarList.RSP_NO_DATA_FOUND);

        List<Integer> verficationCodeList = generateVerificationCode();
        String verificationCode = convertListToString(verficationCodeList);
        emailVerifyGupAccount.setEmailVerificationCode(verificationCode);

        emailVerifyGupAccountRepository.save(emailVerifyGupAccount);
        Properties properties = EmailConfig.getEmailProperties(generalUserProfile.getFirstName() + " " + generalUserProfile.getLastName(), "Aethenos Email Verification");

        properties.put("verificationCode", verificationCode);
        EmailSender emailSender = new EmailSender();
        try {
            emailSender.sendEmail("EmailVerificationMessage", generalUserProfile.getEmail(), (String) properties.getProperty("from"), (String) properties.get("subject"), properties);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setMessage("Verification code sent successfully");
        successResponse.setVariable(VarList.RSP_SUCCESS);
        return successResponse;

    }
}
