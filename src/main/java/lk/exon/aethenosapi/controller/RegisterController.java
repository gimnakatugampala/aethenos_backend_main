package lk.exon.aethenosapi.controller;


import lk.exon.aethenosapi.entity.GeneralUserProfile;
import lk.exon.aethenosapi.payload.request.CheckUserEmailVerificationCodeRequest;
import lk.exon.aethenosapi.payload.request.GeneralUserProfileRequest;
import lk.exon.aethenosapi.payload.request.StudentRegistrationRequest;
import lk.exon.aethenosapi.payload.response.CheckUserEmailVerificationCodeResponse;
import lk.exon.aethenosapi.payload.response.GeneralUserProfileResponse;
import lk.exon.aethenosapi.payload.response.GetAllInstructorsUkBankDetailsForManagePaymentsResponse;
import lk.exon.aethenosapi.payload.response.SuccessResponse;
import lk.exon.aethenosapi.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/register")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RegisterController {
    @Autowired
    private RegisterService registerService;

    @PostMapping("/add")
    public GeneralUserProfileResponse add(GeneralUserProfileRequest generalUserProfileRequest) {
        return registerService.saveUser(generalUserProfileRequest);
    }

    @GetMapping("/getAll")
    public List<GeneralUserProfile> list() {
        return registerService.getAllUsers();
    }

    @PostMapping("/studentRegistration")
    public GeneralUserProfileResponse studentRegistration(StudentRegistrationRequest studentRegistrationRequest) {
        return registerService.studentRegistration(studentRegistrationRequest);
    }

    @PostMapping("/instructorRegistration")
    public GeneralUserProfileResponse instructorRegistration(StudentRegistrationRequest studentRegistrationRequest) {
        return registerService.instructorRegistration(studentRegistrationRequest);
    }
    @PostMapping("/checkUserEmailVerificationCode")
    public CheckUserEmailVerificationCodeResponse checkUserEmailVerificationCode(CheckUserEmailVerificationCodeRequest checkUserEmailVerificationCodeRequest) {
        return registerService.checkUserEmailVerificationCode(checkUserEmailVerificationCodeRequest);
    }
    @PostMapping("/resendUserEmailVerificationCode/{email}")
    public SuccessResponse resendUserEmailVerificationCode(@PathVariable("email") String email) {
        return registerService.resendUserEmailVerificationCode(email);
    }
}
