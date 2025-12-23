package lk.exon.aethenosapi.service.impl;

import lk.exon.aethenosapi.config.EmailConfig;
import lk.exon.aethenosapi.config.PasswordEncoderConfig;
import lk.exon.aethenosapi.entity.*;
import lk.exon.aethenosapi.exception.ErrorException;
import lk.exon.aethenosapi.payload.request.*;
import lk.exon.aethenosapi.payload.response.*;
import lk.exon.aethenosapi.repository.*;
import lk.exon.aethenosapi.service.ManageStudentService;
import lk.exon.aethenosapi.utils.EmailSender;
import lk.exon.aethenosapi.utils.FileUploadUtil;
import lk.exon.aethenosapi.utils.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.ZoneId;
import java.util.*;

@Service
public class ManageStudentServiceimpl implements ManageStudentService {
    @Autowired
    private InterestRepository interestRepository;
    @Autowired
    private UserProfileServiceImpl userProfileServiceImpl;

    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private StudentTopicRepository studentTopicRepository;
    @Autowired
    private InstructorProfileRepository instructorProfileRepository;
    @Autowired
    private GeneralUserProfileRepository generalUserProfileRepository;
    private SuccessResponse successResponse = new SuccessResponse();

    @Override
    public SuccessResponse addStudentInterest(AddInterestRequest addInterestRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {

                final List<String> interestList = addInterestRequest.getInterest();
                if (interestList == null || interestList.size() != 5) {
                    throw new ErrorException("Please add exactly 5 items of interest", VarList.RSP_NO_DATA_FOUND);
                }

                List<Interest> interests = interestRepository.getInterestByGeneralUserProfile(profile);

                if (interests != null) {
                    for (Interest interest : interests) {
                        interestRepository.delete(interest);
                    }
                }

                for (int i = 0; i < interestList.size(); i++) {
                    Interest interest = new Interest();
                    interest.setInterest(interestList.get(i));
                    interest.setGeneralUserProfile(profile);
                    interestRepository.save(interest);
                }
                successResponse.setMessage("Student interests added successfully");
                successResponse.setVariable(VarList.RSP_SUCCESS);
                return successResponse;

            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public List<TopicResponse> getTopics() {
        List<TopicResponse> responseList = new ArrayList<>();
        Set<String> seenTopics = new HashSet<>();
        List<Topic> topicList = topicRepository.findAll();

        for (Topic topic : topicList) {
            if (!seenTopics.contains(topic.getTopic())) {
                TopicResponse topicResponse = new TopicResponse();
                topicResponse.setId(topic.getId());
                topicResponse.setLink_name(topic.getLinkName());
                topicResponse.setTopic(topic.getTopic());
                responseList.add(topicResponse);
                seenTopics.add(topic.getTopic());
            }
        }
        return responseList;
    }

    @Override
    public SuccessResponse setTopics(SetTopicRequest setTopicRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 1) {

                    final List<Integer> TopicList = setTopicRequest.getTopic();

                    if (TopicList == null) {
                        throw new ErrorException("Please add 5 items of topics", VarList.RSP_NO_DATA_FOUND);
                    }

                    if (TopicList.size() == 5) {
                        List<StudentTopic> stu_topics = studentTopicRepository.getStudentTopicByGeneralUserProfile(profile);

                        if (stu_topics != null) {
                            for (StudentTopic stu_topic : stu_topics) {
                                studentTopicRepository.delete(stu_topic);
                            }
                        }

                        for (int i = 0; i < TopicList.size(); i++) {
                            StudentTopic studentTopic = new StudentTopic();
                            Topic topic = topicRepository.getTopicById(setTopicRequest.getTopic().get(i));
                            if (topic == null) {
                                throw new ErrorException("Invalid topic id", VarList.RSP_NO_DATA_FOUND);
                            }
                            studentTopic.setTopic(topic);
                            studentTopic.setGeneralUserProfile(profile);
                            studentTopicRepository.save(studentTopic);
                        }
                        successResponse.setMessage("Student topics added successfully");
                        successResponse.setVariable(VarList.RSP_SUCCESS);
                        return successResponse;
                    } else {
                        throw new ErrorException("Please add 5 items of interest", VarList.RSP_NO_DATA_FOUND);
                    }

                } else {
                    throw new ErrorException("You are not a student", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse updateStudentProfile(StudentProfileUpdateRequest studentProfileUpdateRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {

                InstructorProfile studentProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(profile);
                if (studentProfile == null) {
                    studentProfile = new InstructorProfile();
                    studentProfile.setGeneralUserProfile(profile);
                    studentProfile.setIsProfileCompleted((byte) 0);
                    studentProfile.setCreated_date(new Date());
                    studentProfile.setIsVerified((byte) 0);

                }
                updateProfile(profile, studentProfile, studentProfileUpdateRequest);
                SuccessResponse successResponse = new SuccessResponse();
                successResponse.setMessage("Student profile update successful");
                successResponse.setVariable(VarList.RSP_SUCCESS);
                return successResponse;
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public GeneralUserProfile getProfileDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                return profile;
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse resetPassword(ResetPasswordRequest resetPasswordRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {

                final String oldPassword = resetPasswordRequest.getOldPassword();
                final String newPassword = resetPasswordRequest.getNewPassword();

                if (oldPassword == null || oldPassword.isEmpty()) {
                    throw new ErrorException("Please add the old password", VarList.RSP_NO_DATA_FOUND);
                }
                if (newPassword == null || newPassword.isEmpty()) {
                    throw new ErrorException("Please add the new password", VarList.RSP_NO_DATA_FOUND);
                }


                if (!new BCryptPasswordEncoder().matches(oldPassword, profile.getPassword())) {
                    throw new ErrorException("The old password does not match", VarList.RSP_NO_DATA_FOUND);
                }
                PasswordEncoderConfig by = new PasswordEncoderConfig();
                String encryptedPwd = by.passwordEncoder().encode(newPassword);
                profile.setPassword(encryptedPwd);
                generalUserProfileRepository.save(profile);
                SuccessResponse successResponse = new SuccessResponse();
                successResponse.setMessage("Password update in student profile successful");
                successResponse.setVariable(VarList.RSP_SUCCESS);
                return successResponse;

            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    private void updateProfile(GeneralUserProfile profile, InstructorProfile studentProfile, StudentProfileUpdateRequest studentProfileUpdateRequest) {
        final String firstName = studentProfileUpdateRequest.getFirstName();
        final String lastName = studentProfileUpdateRequest.getLastName();
        final MultipartFile profileImage = studentProfileUpdateRequest.getProfileImage();

        final String HeadLine = studentProfileUpdateRequest.getHeadline();
        final String Website = studentProfileUpdateRequest.getWebsite();
        final String Biography = studentProfileUpdateRequest.getBiography();
        final String Twitter = studentProfileUpdateRequest.getTwitter();
        final String Facebook = studentProfileUpdateRequest.getFacebook();
        final String Linkedin = studentProfileUpdateRequest.getLinkedin();
        final String youtubeUrl = studentProfileUpdateRequest.getYoutube();


        if (firstName != null && !firstName.isEmpty()) {
            profile.setFirstName(firstName);
            generalUserProfileRepository.save(profile);
        }
        if (lastName != null && !lastName.isEmpty()) {
            profile.setLastName(lastName);
            generalUserProfileRepository.save(profile);
        }
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                FileUploadResponse profileImg = FileUploadUtil.saveFile(profileImage, "profile-images");
                if (profile.getProfileImg() != null && !profile.getProfileImg().isEmpty()) {
                    FileUploadUtil.deleteFile(profile.getProfileImg());
                }

                profile.setProfileImg(profileImg.getFilename());
                generalUserProfileRepository.save(profile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (HeadLine != null && !HeadLine.isEmpty()) {
            studentProfile.setHeadline(HeadLine);
            instructorProfileRepository.save(studentProfile);
        }
        if (Website != null && !Website.isEmpty()) {
            studentProfile.setWebsite(Website);
            instructorProfileRepository.save(studentProfile);
        }

        if (Biography != null && !Biography.isEmpty()) {
            studentProfile.setBiography(Biography);
            instructorProfileRepository.save(studentProfile);
        }

        if (Twitter != null && !Twitter.isEmpty()) {
            studentProfile.setTwitter(Twitter);
            instructorProfileRepository.save(studentProfile);
        }

        if (Facebook != null && !Facebook.isEmpty()) {
            studentProfile.setFacebook(Facebook);
            instructorProfileRepository.save(studentProfile);
        }
        if (Linkedin != null && !Linkedin.isEmpty()) {
            studentProfile.setLinkedin(Linkedin);
            instructorProfileRepository.save(studentProfile);
        }
        if (youtubeUrl != null && !youtubeUrl.isEmpty()) {
            studentProfile.setYoutube(youtubeUrl);
            instructorProfileRepository.save(studentProfile);
        }

        if (studentProfile != null && studentProfile.getBiography() != null && studentProfile.getFacebook() != null && studentProfile.getHeadline() != null
                && studentProfile.getLinkedin() != null && studentProfile.getTwitter() != null && studentProfile.getWebsite() != null && studentProfile.getYoutube() != null) {
            studentProfile.setIsProfileCompleted((byte) 1);
            instructorProfileRepository.save(studentProfile);
        }
    }

    @Override
    public SuccessResponse forgotPassword(String email) {
        GeneralUserProfile generalUserProfile = generalUserProfileRepository.getGeneralUserProfileByEmail(email);
        if (generalUserProfile == null) {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
        List<Integer> verficationCodeList = generateVerificationCode();

        String verficationCode = convertListToString(verficationCodeList);


        generalUserProfile.setVerificationCode(verficationCode);
        generalUserProfileRepository.save(generalUserProfile);

        Properties properties = EmailConfig.getVerificationEmailProperties(generalUserProfile.getFirstName() + " " + generalUserProfile.getLastName()
                , "Forgot Password - Verification Code", verficationCode);
        try {
            EmailSender emailSender = new EmailSender();
            emailSender.sendEmail("ForgotPasswordVerificationCodeSendMessage", generalUserProfile.getEmail(), (String) properties.getProperty("from"), (String) properties.get("subject"), properties);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new ErrorException(e.getMessage(), VarList.RSP_NO_DATA_FOUND);
        }
        successResponse.setMessage("Verification code sent successfully. Please check your email");
        successResponse.setVariable(VarList.RSP_SUCCESS);
        return successResponse;
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
    public SuccessResponse updatePassword(ForgotPasswordRequest forgotPasswordRequest) {
        final String email = forgotPasswordRequest.getEmail();
        final String verificationCode = forgotPasswordRequest.getVerificationCode();
        final String newPassword = forgotPasswordRequest.getNewPassword();

        if (email == null || email.isEmpty() || verificationCode == null || verificationCode.isEmpty() || newPassword == null || newPassword.isEmpty())
            throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);

        GeneralUserProfile profile = generalUserProfileRepository.getGeneralUserProfileByEmail(email);
        if (profile == null)
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);

        if (!profile.getVerificationCode().equals(verificationCode))
            throw new ErrorException("The verification code is invalid", VarList.RSP_NO_DATA_FOUND);
        PasswordEncoderConfig by = new PasswordEncoderConfig();
        String encryptedPwd = by.passwordEncoder().encode(newPassword);
        profile.setPassword(encryptedPwd);
        generalUserProfileRepository.save(profile);
        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setMessage("Password update successful");
        successResponse.setVariable(VarList.RSP_SUCCESS);
        return successResponse;

    }

    @Override
    public Boolean verifyVerificationCode(VerifyVerificationCodeRequest verifyVerificationCodeRequest) {
        final String verificationCode = verifyVerificationCodeRequest.getVerificationCode();
        final String email = verifyVerificationCodeRequest.getEmail();
        if (verificationCode == null || verificationCode.isEmpty() || email == null || email.isEmpty())
            throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);

        GeneralUserProfile profile = generalUserProfileRepository.getGeneralUserProfileByEmail(email);
        if (profile == null)
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);

        if (profile.getVerificationCode().equals(verificationCode))
            return true;
        else
            return false;

    }

    @Override
    public String getOwnCountry() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
            if (profile != null && profile.getIsActive() == 1) {
                return profile.getCountry() == null ? "" : profile.getCountry();
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not authenticated", VarList.RSP_NO_DATA_FOUND);
        }
    }

}
