package lk.exon.aethenosapi.service;

import lk.exon.aethenosapi.payload.request.AddInstructorPaymentDetailsRequest;
import lk.exon.aethenosapi.payload.request.UpdateInstructorTermsAgreeRequest;
import lk.exon.aethenosapi.payload.response.CheckAllInstructorPaymentDetailsResponse;
import lk.exon.aethenosapi.payload.response.GetInstructorPaymentDetailsResponse;
import lk.exon.aethenosapi.payload.response.SuccessResponse;

public interface InstructorPaymentService {
    SuccessResponse addInstructorPaymentDetails(AddInstructorPaymentDetailsRequest addInstructorPaymentDetailsRequest);

    GetInstructorPaymentDetailsResponse getInstructorPaymentDetails();

    SuccessResponse updateInstructorTermsAgree(UpdateInstructorTermsAgreeRequest updateInstructorTermsAgreeRequest);

    CheckAllInstructorPaymentDetailsResponse checkAllInstructorPaymentDetailsComplete(String courseCode);

    boolean checkInstructorPaymentDetails(String courseCode);

    boolean checkInstructorTermsForFreeCourse(String courseCode);
}
