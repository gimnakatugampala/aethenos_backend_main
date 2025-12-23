package lk.exon.aethenosapi.controller;

import lk.exon.aethenosapi.payload.request.InstructorProfileUpdateRequest;
import lk.exon.aethenosapi.payload.response.GetInstructorVerifyResponse;
import lk.exon.aethenosapi.payload.response.InstructorProfileResponse;
import lk.exon.aethenosapi.payload.response.SuccessResponse;
import lk.exon.aethenosapi.security.JwtTokenUtil;
import lk.exon.aethenosapi.service.InstructorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "instructor")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class InstructorController {

    @Autowired
    private InstructorService instructorService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @GetMapping("/getInstructorProfileDetails")
    public InstructorProfileResponse getInstructorProfileDetails(){
        return instructorService.getInstructorProfileDetails();
    }

    @PutMapping("/updateInstructorProfile")
    public SuccessResponse updateInstructorProfile(InstructorProfileUpdateRequest instructorProfileUpdateRequest) {
        return instructorService.updateInstructor(instructorProfileUpdateRequest);
    }
    @GetMapping("/switchToInstructor")
    public SuccessResponse switchToInstructor(){
        return instructorService.switchToInstructor();
    }

    @GetMapping("/verifyInstructorProfile")
    public SuccessResponse verifyInstructorProfile(){
        return instructorService.verifyInstructorProfile();
    }
    @GetMapping("/getInstructorVerify")
    public GetInstructorVerifyResponse getInstructorVerify(){
        return instructorService.getInstructorVerify();
    }

}
