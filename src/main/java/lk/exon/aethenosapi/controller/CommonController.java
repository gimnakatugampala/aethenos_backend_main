package lk.exon.aethenosapi.controller;

import lk.exon.aethenosapi.payload.request.AddVatRequest;
import lk.exon.aethenosapi.payload.request.Exceltobase64ByexcelFileNameRequest;
import lk.exon.aethenosapi.payload.request.SendEmailRequest;
import lk.exon.aethenosapi.payload.response.*;
import lk.exon.aethenosapi.service.CommonService;
import lk.exon.aethenosapi.service.CommunicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping(value = "common")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CommonController {
    @Autowired
    private CommonService commonService;

    @GetMapping("/exceltobase64ByexcelFileName")
    public String exceltobase64ByexcelFileName(Exceltobase64ByexcelFileNameRequest exceltobase64ByexcelFileNameRequest) {
        return commonService.exceltobase64ByexcelFileName(exceltobase64ByexcelFileNameRequest);
    }
    @GetMapping("/getVat")
    public List<GetVatResponse> getVat() {
        return commonService.getVat();
    }
    @PutMapping("/updateVat")
    public SuccessResponse updateVat(@RequestBody List<AddVatRequest> addVatRequests) {
        return commonService.updateVat(addVatRequests);
    }
    @GetMapping("/getAdminDashboardCards")
    public GetAdminDashboardCardsResponse getAdminDashboardCards() {
        return commonService.getAdminDashboardCards();
    }
    @GetMapping("/searchCourseAndInstructorDetails/{keyword}")
    public List<GetCourseAndInstructorDetailsResponse> searchCourseAndInstructorDetails(@PathVariable("keyword") String keyword) {
        return commonService.searchCourseAndInstructorDetails(keyword);
    }
    @PostMapping("/sendEmail")
    public SuccessResponse sendEmail(SendEmailRequest sendEmailRequest) {
        return commonService.sendEmail(sendEmailRequest);
    }
    @GetMapping("/downloadFile")
    public ResponseEntity<Resource> downloadFile(@RequestParam String filePath) {
        return commonService.downloadFile(filePath);
    }
}
