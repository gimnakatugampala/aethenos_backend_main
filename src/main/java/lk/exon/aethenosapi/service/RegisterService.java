package lk.exon.aethenosapi.service;


import lk.exon.aethenosapi.entity.GeneralUserProfile;
import lk.exon.aethenosapi.payload.request.CheckUserEmailVerificationCodeRequest;
import lk.exon.aethenosapi.payload.request.GeneralUserProfileRequest;
import lk.exon.aethenosapi.payload.request.StudentRegistrationRequest;
import lk.exon.aethenosapi.payload.response.CheckUserEmailVerificationCodeResponse;
import lk.exon.aethenosapi.payload.response.GeneralUserProfileResponse;
import lk.exon.aethenosapi.payload.response.SuccessResponse;

import java.util.List;

public interface RegisterService {
    GeneralUserProfileResponse saveUser(GeneralUserProfileRequest generalUserProfileRequest);
    List<GeneralUserProfile> getAllUsers();

    GeneralUserProfileResponse studentRegistration(StudentRegistrationRequest studentRegistrationRequest);

    GeneralUserProfileResponse instructorRegistration(StudentRegistrationRequest studentRegistrationRequest);

    CheckUserEmailVerificationCodeResponse checkUserEmailVerificationCode(CheckUserEmailVerificationCodeRequest checkUserEmailVerificationCodeRequest);

    SuccessResponse resendUserEmailVerificationCode(String email);
}
