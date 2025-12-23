package lk.exon.aethenosapi.service;

import lk.exon.aethenosapi.entity.GeneralUserProfile;
import lk.exon.aethenosapi.payload.request.AddAdminRequest;
import lk.exon.aethenosapi.payload.request.AddRevenuePricesSplitRequest;
import lk.exon.aethenosapi.payload.response.GetAllTransactionHistoryResponse;
import lk.exon.aethenosapi.payload.response.GetRevenuePricesSplitResponse;
import lk.exon.aethenosapi.payload.response.GetRevenueSplitHistoryResponse;
import lk.exon.aethenosapi.payload.response.SuccessResponse;

import java.util.List;

public interface AddAdminService {
    public SuccessResponse addAdmin(AddAdminRequest addAdminRequest);


    public List<GeneralUserProfile> getAllAdmins();

    SuccessResponse getAdminDetails(int adminId);

    SuccessResponse addRevenuePricesSplit(AddRevenuePricesSplitRequest addRevenuePricesSplitRequest);

    GetRevenuePricesSplitResponse getRevenuePricesSplit();

    List<GetRevenueSplitHistoryResponse> getRevenueSplitHistory();

    List<GetAllTransactionHistoryResponse> getAllTransactionHistory();
}
