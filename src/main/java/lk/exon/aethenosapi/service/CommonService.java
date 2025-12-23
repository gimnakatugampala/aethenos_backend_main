package lk.exon.aethenosapi.service;

import lk.exon.aethenosapi.payload.request.AddVatRequest;
import lk.exon.aethenosapi.payload.request.Exceltobase64ByexcelFileNameRequest;
import lk.exon.aethenosapi.payload.request.SendEmailRequest;
import lk.exon.aethenosapi.payload.response.GetAdminDashboardCardsResponse;
import lk.exon.aethenosapi.payload.response.GetCourseAndInstructorDetailsResponse;
import lk.exon.aethenosapi.payload.response.GetVatResponse;
import lk.exon.aethenosapi.payload.response.SuccessResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CommonService {
    String exceltobase64ByexcelFileName(Exceltobase64ByexcelFileNameRequest exceltobase64ByexcelFileNameRequest);

    List<GetVatResponse> getVat();

    SuccessResponse updateVat(List<AddVatRequest> addVatRequests);

    GetAdminDashboardCardsResponse getAdminDashboardCards();

    List<GetCourseAndInstructorDetailsResponse> searchCourseAndInstructorDetails(String keyword);

    SuccessResponse sendEmail(SendEmailRequest sendEmailRequest);

    ResponseEntity<Resource> downloadFile(String filePath);
}
