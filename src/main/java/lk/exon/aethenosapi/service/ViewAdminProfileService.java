package lk.exon.aethenosapi.service;

import lk.exon.aethenosapi.payload.request.AddAdminRequest;
import lk.exon.aethenosapi.payload.response.AdminProfileResponse;
import lk.exon.aethenosapi.entity.GeneralUserProfile;
import lk.exon.aethenosapi.payload.response.SuccessResponse;
import org.springframework.stereotype.Service;

@Service
public interface ViewAdminProfileService {

    AdminProfileResponse adminProfileResponse(Integer userId) throws Exception;

    SuccessResponse updateAdminUserProfile (String authorizationHeader, AddAdminRequest addAdminRequest);


}
