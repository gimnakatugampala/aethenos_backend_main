package lk.exon.aethenosapi.controller;

import lk.exon.aethenosapi.entity.GeneralUserProfile;
import lk.exon.aethenosapi.payload.request.*;
import lk.exon.aethenosapi.payload.response.ProfileResponse;
import lk.exon.aethenosapi.payload.response.SuccessResponse;
import lk.exon.aethenosapi.payload.response.TopicResponse;
import lk.exon.aethenosapi.service.ManageCourseService;
import lk.exon.aethenosapi.service.ManageStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = "/studentProfile")
public class StudentController {
    @Autowired
    private ManageStudentService manageStudentService;

    @PostMapping("/addStudentInterest")
    public SuccessResponse addStudentInterest(AddInterestRequest addInterestRequest) {
        return manageStudentService.addStudentInterest(addInterestRequest);
    }

    @GetMapping("/getTopics")
    public List<TopicResponse> getTopic() {
        return manageStudentService.getTopics();
    }

    @PostMapping("/setTopics")
    public SuccessResponse setTopic(SetTopicRequest setTopicRequest) {
        return manageStudentService.setTopics(setTopicRequest);
    }

    @PostMapping("/updateStudentProfile")
    public SuccessResponse updateStudentProfile(StudentProfileUpdateRequest studentProfileUpdateRequest) {
        return manageStudentService.updateStudentProfile(studentProfileUpdateRequest);
    }

    @GetMapping("/getProfileDetails")
    public GeneralUserProfile getProfileDetails() {
        return manageStudentService.getProfileDetails();
    }

    @PutMapping("/resetPassword")
    public SuccessResponse resetPassword(ResetPasswordRequest resetPasswordRequest) {
        return manageStudentService.resetPassword(resetPasswordRequest);
    }

    @PutMapping("/forgotPasswords/{email}")
    public SuccessResponse forgotPassword(@PathVariable String email) {
        return manageStudentService.forgotPassword(email);
    }

    @PutMapping("/updatePassword")
    public SuccessResponse updatePassword(ForgotPasswordRequest forgotPasswordRequest) {
        return manageStudentService.updatePassword(forgotPasswordRequest);
    }

    @PostMapping("/verifyVerificationCode")
    public Boolean verifyVerificationCode(VerifyVerificationCodeRequest verifyVerificationCodeRequest) {
        return manageStudentService.verifyVerificationCode(verifyVerificationCodeRequest);
    }
    @GetMapping("/getOwnCountry")
    public String getOwnCountry() {
        return manageStudentService.getOwnCountry();
    }
}
