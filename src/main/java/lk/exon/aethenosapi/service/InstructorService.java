package lk.exon.aethenosapi.service;

import lk.exon.aethenosapi.payload.request.InstructorProfileUpdateRequest;
import lk.exon.aethenosapi.payload.response.GetInstructorVerifyResponse;
import lk.exon.aethenosapi.payload.response.InstructorProfileResponse;
import lk.exon.aethenosapi.payload.response.SuccessResponse;

public interface InstructorService {

    InstructorProfileResponse getInstructorProfileDetails();

    SuccessResponse updateInstructor(InstructorProfileUpdateRequest instructorProfileUpdateRequest);

    SuccessResponse switchToInstructor();

    SuccessResponse verifyInstructorProfile();

    GetInstructorVerifyResponse getInstructorVerify();
}

