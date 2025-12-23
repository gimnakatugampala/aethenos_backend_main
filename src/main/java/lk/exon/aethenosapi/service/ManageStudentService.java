package lk.exon.aethenosapi.service;

import lk.exon.aethenosapi.entity.*;
import lk.exon.aethenosapi.payload.request.*;
import lk.exon.aethenosapi.payload.response.*;

import java.util.List;

public interface ManageStudentService {
    SuccessResponse addStudentInterest(AddInterestRequest addInterestRequest);

    List<TopicResponse> getTopics();

    SuccessResponse setTopics(SetTopicRequest setTopicRequest);

    SuccessResponse updateStudentProfile(StudentProfileUpdateRequest studentProfileUpdateRequest);

  GeneralUserProfile getProfileDetails();

    SuccessResponse resetPassword(ResetPasswordRequest resetPasswordRequest);

    SuccessResponse forgotPassword(String email);

    SuccessResponse updatePassword(ForgotPasswordRequest forgotPasswordRequest);

    Boolean verifyVerificationCode(VerifyVerificationCodeRequest verifyVerificationCodeRequest);

    String getOwnCountry();
}
