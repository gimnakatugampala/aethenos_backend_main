package lk.exon.aethenosapi.controller;

import lk.exon.aethenosapi.entity.GeneralUserProfile;
import lk.exon.aethenosapi.payload.request.AddAdminRequest;
import lk.exon.aethenosapi.payload.request.AddRevenuePricesSplitRequest;
import lk.exon.aethenosapi.payload.response.GetAllTransactionHistoryResponse;
import lk.exon.aethenosapi.payload.response.GetRevenuePricesSplitResponse;
import lk.exon.aethenosapi.payload.response.GetRevenueSplitHistoryResponse;
import lk.exon.aethenosapi.payload.response.SuccessResponse;
import lk.exon.aethenosapi.repository.GeneralUserProfileRepository;
import lk.exon.aethenosapi.service.AddAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manageAdmins")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ManageAdminsController {
    @Autowired
    private AddAdminService addAdminService;
    @Autowired
    private GeneralUserProfileRepository generalUserProfileRepository;
    @PostMapping("/add")
    public SuccessResponse addAdmin(AddAdminRequest addAdminRequest){
        return addAdminService.addAdmin(addAdminRequest);
    }

    @GetMapping("/view")
    public @ResponseBody Iterable<GeneralUserProfile> loadAllAdmins(){
//      return generalUserProfileRepository.findAll();
        return addAdminService.getAllAdmins();
    }

    @GetMapping("/activate/{adminId}")
    public SuccessResponse activateAdmin(@PathVariable("adminId") int adminId){
        return addAdminService.getAdminDetails(adminId);
    }
    @PostMapping("/addRevenuePricesSplit")
    public SuccessResponse addRevenuePricesSplit(AddRevenuePricesSplitRequest addRevenuePricesSplitRequest){
        return addAdminService.addRevenuePricesSplit(addRevenuePricesSplitRequest);
    }
    @GetMapping("/getRevenuePricesSplit")
    public GetRevenuePricesSplitResponse getRevenuePricesSplit(){
        return addAdminService.getRevenuePricesSplit();
    }
    @GetMapping("/getRevenueSplitHistory")
    public List<GetRevenueSplitHistoryResponse> getRevenueSplitHistory(){
        return addAdminService.getRevenueSplitHistory();
    }
    @GetMapping("/getAllTransactionHistory")
    public List<GetAllTransactionHistoryResponse> getAllTransactionHistory(){
        return addAdminService.getAllTransactionHistory();
    }

}
