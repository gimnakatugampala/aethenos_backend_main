package lk.exon.aethenosapi.controller;

import lk.exon.aethenosapi.payload.request.AddInstructorPaymentDetailsRequest;
import lk.exon.aethenosapi.payload.request.UpdateInstructorTermsAgreeRequest;
import lk.exon.aethenosapi.payload.response.CheckAllInstructorPaymentDetailsResponse;
import lk.exon.aethenosapi.payload.response.GetInstructorPaymentDetailsResponse;
import lk.exon.aethenosapi.payload.response.SuccessResponse;
import lk.exon.aethenosapi.service.InstructorPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "instructorPayment")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class InstructorPaymentsController {
    @Autowired
    private InstructorPaymentService instructorPaymentService;

    @PostMapping("/addInstructorPaymentDetails")
    public SuccessResponse addInstructorPaymentDetails(AddInstructorPaymentDetailsRequest addInstructorPaymentDetailsRequest) {
        return instructorPaymentService.addInstructorPaymentDetails(addInstructorPaymentDetailsRequest);
    }

    @GetMapping("/getInstructorPaymentDetails")
    public GetInstructorPaymentDetailsResponse getInstructorPaymentDetails() {
        return instructorPaymentService.getInstructorPaymentDetails();
    }

    @PutMapping("/updateInstructorTermsAgree")
    public SuccessResponse updateInstructorTermsAgree(UpdateInstructorTermsAgreeRequest updateInstructorTermsAgreeRequest) {
        return instructorPaymentService.updateInstructorTermsAgree(updateInstructorTermsAgreeRequest);
    }

    @GetMapping("/checkAllInstructorPaymentDetailsComplete/{courseCode}")
    public CheckAllInstructorPaymentDetailsResponse checkAllInstructorPaymentDetailsComplete(@PathVariable String courseCode) {
        return instructorPaymentService.checkAllInstructorPaymentDetailsComplete(courseCode);
    }
    @GetMapping("/checkInstructorTermsForFreeCourse/{courseCode}")
    public boolean checkInstructorTermsForFreeCourse(@PathVariable String courseCode) {
        return instructorPaymentService.checkInstructorTermsForFreeCourse(courseCode);
    }

    @GetMapping("/checkInstructorTerms/{courseCode}")
    public boolean checkInstructorPaymentDetails(@PathVariable String courseCode) {
        return instructorPaymentService.checkInstructorPaymentDetails(courseCode);
    }
}
