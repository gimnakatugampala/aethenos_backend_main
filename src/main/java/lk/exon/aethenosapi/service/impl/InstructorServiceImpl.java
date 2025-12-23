package lk.exon.aethenosapi.service.impl;

import lk.exon.aethenosapi.config.Config;
import lk.exon.aethenosapi.entity.*;
import lk.exon.aethenosapi.exception.ErrorException;
import lk.exon.aethenosapi.payload.request.InstructorProfileUpdateRequest;
import lk.exon.aethenosapi.payload.response.*;
import lk.exon.aethenosapi.repository.GeneralUserProfileRepository;
import lk.exon.aethenosapi.repository.GupTypeRepository;
import lk.exon.aethenosapi.repository.InstructorExternalDetailsRepository;
import lk.exon.aethenosapi.repository.InstructorProfileRepository;
import lk.exon.aethenosapi.service.InstructorService;
import lk.exon.aethenosapi.utils.FileUploadUtil;
import lk.exon.aethenosapi.utils.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class InstructorServiceImpl implements InstructorService {
    @Autowired
    private UserProfileServiceImpl userProfileServiceImpl;
    @Autowired
    private InstructorProfileRepository instructorProfileRepository;
    @Autowired
    private GupTypeRepository gupTypeRepository;
    @Autowired
    private GeneralUserProfileRepository generalUserProfileRepository;
    @Autowired
    private InstructorExternalDetailsRepository instructorExternalDetailsRepository;

    @Override
    public InstructorProfileResponse getInstructorProfileDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                InstructorProfile instructorProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(profile);
                return getInstructorProfileResponse(profile, instructorProfile);
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse updateInstructor(InstructorProfileUpdateRequest instructorProfileUpdateRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                InstructorProfile instructorProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(profile);
                if (instructorProfile != null) {
                    final String firstName = instructorProfileUpdateRequest.getFirstName();
                    final String lastName = instructorProfileUpdateRequest.getLastName();
                    final MultipartFile profileImage = instructorProfileUpdateRequest.getProfileImage();

                    final String HeadLine = instructorProfileUpdateRequest.getHeadline();
                    final String Website = instructorProfileUpdateRequest.getWebsite();
                    final String Biography = instructorProfileUpdateRequest.getBiography();
                    final String Twitter = instructorProfileUpdateRequest.getTwitter();
                    final String Facebook = instructorProfileUpdateRequest.getFacebook();
                    final String Linkedin = instructorProfileUpdateRequest.getLinkedin();
                    final String youtubeUrl = instructorProfileUpdateRequest.getYoutube();
                    final String linkToCourse = instructorProfileUpdateRequest.getLinkToCourse();
                    final Double externalRating = instructorProfileUpdateRequest.getExternalRating();
                    final Integer externalNumberOfStudent = instructorProfileUpdateRequest.getExternalNumberOfStudents();
                    final String anyComments = instructorProfileUpdateRequest.getAnyComments();
                    final String email = instructorProfileUpdateRequest.getEmail();


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
                                try {
                                    Files.delete(Paths.get(Config.UPLOAD_URL + profile.getProfileImg()));
                                } catch (Exception e) {
                                    throw new ErrorException(e.getMessage(), VarList.RSP_NO_DATA_FOUND);
                                }
                            }

                            profile.setProfileImg(profileImg.getFilename());
                            generalUserProfileRepository.save(profile);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    instructorProfile.setHeadline((HeadLine != null && !HeadLine.isEmpty()) ? HeadLine : "");
                    instructorProfile.setEmail((email != null && !email.isEmpty()) ? email : "");
                    instructorProfile.setWebsite((Website != null && !Website.isEmpty()) ? Website : "");
                    instructorProfile.setBiography((Biography != null && !Biography.isEmpty()) ? Biography : "");
                    instructorProfile.setTwitter((Twitter != null && !Twitter.isEmpty()) ? Twitter : "");
                    instructorProfile.setFacebook((Facebook != null && !Facebook.isEmpty()) ? Facebook : "");
                    instructorProfile.setLinkedin((Linkedin != null && !Linkedin.isEmpty()) ? Linkedin : "");
                    instructorProfile.setYoutube((youtubeUrl != null && !youtubeUrl.isEmpty()) ? youtubeUrl : "");
                    instructorProfileRepository.save(instructorProfile);

                    if (profile.getProfileImg() != null && !profile.getProfileImg().isEmpty() && instructorProfile.getBiography() != null && !instructorProfile.getBiography().isEmpty() && instructorProfile.getHeadline() != null && !instructorProfile.getHeadline().isEmpty()
                            && profile.getFirstName() != null && !profile.getFirstName().isEmpty() && profile.getLastName() != null && !profile.getLastName().isEmpty()) {
                        instructorProfile.setIsProfileCompleted((byte) 1);
                        instructorProfileRepository.save(instructorProfile);
                    }

                    if ((linkToCourse != null && !linkToCourse.isEmpty()) ||
                            (externalRating != null && !externalRating.toString().isEmpty()) ||
                            (externalNumberOfStudent != null && !externalNumberOfStudent.toString().isEmpty()) ||
                            (anyComments != null && !anyComments.isEmpty())) {

                        InstructorExternalDetails instructorExternalDetails = instructorExternalDetailsRepository.getInstructorExternalDetailsByInstructorProfile(instructorProfile);
                        if (instructorExternalDetails == null) {
                            instructorExternalDetails = new InstructorExternalDetails();
                            instructorExternalDetails.setInstructorProfile(instructorProfile);
                        }

                        instructorExternalDetails.setLinkToCourse(linkToCourse != null && !linkToCourse.isEmpty() ? linkToCourse : "");
                        instructorExternalDetails.setExternalRating(externalRating != null && !externalRating.toString().isEmpty() ? externalRating : 0.0);
                        instructorExternalDetails.setExternalNumberOfStudents(externalNumberOfStudent != null && !externalNumberOfStudent.toString().isEmpty() ? externalNumberOfStudent : 0);
                        instructorExternalDetails.setAnyComments(anyComments != null && !anyComments.isEmpty() ? anyComments : "");

                        instructorExternalDetailsRepository.save(instructorExternalDetails);
                    }


                    SuccessResponse successResponse = new SuccessResponse();
                    successResponse.setMessage("Instructor profile update successful");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;
                } else {
                    throw new ErrorException("No such profile found", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse switchToInstructor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                InstructorProfile instructorProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(profile);
                if (instructorProfile == null) {
                    InstructorProfile instructorProfile1 = new InstructorProfile();
                    instructorProfile1.setGeneralUserProfile(profile);
                    instructorProfile1.setIsProfileCompleted((byte) 0);
                    instructorProfile1.setCreated_date(new Date());
                    instructorProfile1.setIsVerified((byte) 0);

                    instructorProfileRepository.save(instructorProfile1);
                }
                GupType gupType = gupTypeRepository.getGupTypeById(2);
                if (gupType != null) {
                    profile.setGupType(gupType);
                    generalUserProfileRepository.save(profile);
                } else {
                    throw new ErrorException("Gup type not available", VarList.RSP_NO_DATA_FOUND);
                }

                SuccessResponse successResponse = new SuccessResponse();
                successResponse.setMessage("Success");
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
    public SuccessResponse verifyInstructorProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getGupType().getId() == 2) {
                if (profile.getIsActive() == 1) {
                    InstructorProfile instructorProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(profile);
                    if (instructorProfile != null) {
                        if (instructorProfile.getIsVerified() == 1) {
                            throw new ErrorException("You have already verified", VarList.RSP_NO_DATA_FOUND);
                        } else {
                            instructorProfile.setIsVerified((byte) 1);
                            instructorProfileRepository.save(instructorProfile);
                            SuccessResponse successResponse = new SuccessResponse();
                            successResponse.setMessage("Instructor profile verified");
                            successResponse.setVariable(VarList.RSP_SUCCESS);
                            return successResponse;
                        }
                    } else {
                        throw new ErrorException("Instructor profile not found", VarList.RSP_NO_DATA_FOUND);
                    }
                } else {
                    throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("You are not a instructor to do this operation", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public GetInstructorVerifyResponse getInstructorVerify() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getGupType().getId() == 2) {
                if (profile.getIsActive() == 1) {
                    InstructorProfile instructorProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(profile);
                    if (instructorProfile != null) {
                        GetInstructorVerifyResponse getInstructorVerifyResponse = new GetInstructorVerifyResponse();
                        if (instructorProfile.getIsVerified() == 1) {
                            getInstructorVerifyResponse.setMessage("Instructor Profile Verified");
                            getInstructorVerifyResponse.setIsVerify(instructorProfile.getIsVerified());
                        } else if (instructorProfile.getIsVerified() == 0) {
                            getInstructorVerifyResponse.setMessage("Instructor Profile not Verified");
                            getInstructorVerifyResponse.setIsVerify(instructorProfile.getIsVerified());
                        }
                        return getInstructorVerifyResponse;

                    } else {
                        throw new ErrorException("Instructor profile not found", VarList.RSP_NO_DATA_FOUND);
                    }
                } else {
                    throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("You are not a instructor to do this operation", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    private InstructorProfileResponse getInstructorProfileResponse(GeneralUserProfile profile, InstructorProfile instructorProfile) {
        InstructorProfileResponse instructorProfileResponse = new InstructorProfileResponse();
        List<InstructorExternalDetailsResponse> instructorExternalDetailsResponses = new ArrayList<>();
        if (instructorProfile != null) {
            instructorProfileResponse.setId(instructorProfile.getId());
            instructorProfileResponse.setHeadline(instructorProfile.getHeadline());
            instructorProfileResponse.setInstructorExternalEmail((instructorProfile.getEmail() == null) ? "" : instructorProfile.getEmail());
            instructorProfileResponse.setWebsite(instructorProfile.getWebsite());
            instructorProfileResponse.setBiography(instructorProfile.getBiography());
            instructorProfileResponse.setTwitter(instructorProfile.getTwitter());
            instructorProfileResponse.setFacebook(instructorProfile.getFacebook());
            instructorProfileResponse.setLinkedin(instructorProfile.getLinkedin());
            instructorProfileResponse.setYoutube(instructorProfile.getYoutube());
            instructorProfileResponse.setEmail(profile.getEmail());
            instructorProfileResponse.setUser_code(profile.getUserCode());
            instructorProfileResponse.setRegistered_date(profile.getRegisteredDate().toString());
            instructorProfileResponse.setFirst_name(profile.getFirstName());
            instructorProfileResponse.setLast_name(profile.getLastName());
            instructorProfileResponse.setProfile_img(profile.getProfileImg());
            List<InstructorExternalDetails> instructorExternalDetailsList = instructorExternalDetailsRepository.getInstructorExternalDetailssByInstructorProfile(instructorProfile);
            for (InstructorExternalDetails instructorExternalDetails : instructorExternalDetailsList) {
                InstructorExternalDetailsResponse instructorExternalDetailsResponse = new InstructorExternalDetailsResponse();
                instructorExternalDetailsResponse.setLinkToCourse(instructorExternalDetails.getLinkToCourse());
                instructorExternalDetailsResponse.setExternalRating(instructorExternalDetails.getExternalRating());
                instructorExternalDetailsResponse.setExternalNumberOfStudents(instructorExternalDetails.getExternalNumberOfStudents());
                instructorExternalDetailsResponse.setAnyComments(instructorExternalDetails.getAnyComments());
                instructorExternalDetailsResponses.add(instructorExternalDetailsResponse);
            }
        } else {
            instructorProfileResponse.setHeadline("");
            instructorProfileResponse.setInstructorExternalEmail("");
            instructorProfileResponse.setWebsite("");
            instructorProfileResponse.setBiography("");
            instructorProfileResponse.setTwitter("");
            instructorProfileResponse.setFacebook("");
            instructorProfileResponse.setLinkedin("");
            instructorProfileResponse.setYoutube("");
            instructorProfileResponse.setEmail(profile.getEmail());
            instructorProfileResponse.setUser_code(profile.getUserCode());
            instructorProfileResponse.setRegistered_date(profile.getRegisteredDate().toString());
            instructorProfileResponse.setFirst_name(profile.getFirstName());
            instructorProfileResponse.setLast_name(profile.getLastName());
            instructorProfileResponse.setProfile_img(profile.getProfileImg() != null ? profile.getProfileImg() : "");
        }
        instructorProfileResponse.setInstructorExternalDetailsResponse(instructorExternalDetailsResponses);
        return instructorProfileResponse;
    }
}
