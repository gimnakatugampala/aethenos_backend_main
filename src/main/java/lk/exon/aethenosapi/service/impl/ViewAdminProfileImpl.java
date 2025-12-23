package lk.exon.aethenosapi.service.impl;

import lk.exon.aethenosapi.payload.request.AddAdminRequest;
import lk.exon.aethenosapi.payload.response.AdminProfileResponse;
import lk.exon.aethenosapi.entity.GeneralUserProfile;
import lk.exon.aethenosapi.exception.ErrorException;
import lk.exon.aethenosapi.payload.response.SuccessResponse;
import lk.exon.aethenosapi.repository.GeneralUserProfileRepository;
import lk.exon.aethenosapi.repository.GupTypeRepository;
import lk.exon.aethenosapi.security.JwtTokenUtil;
import lk.exon.aethenosapi.service.ViewAdminProfileService;
import lk.exon.aethenosapi.utils.VarList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@Slf4j
public class ViewAdminProfileImpl implements ViewAdminProfileService {

    private final GeneralUserProfileRepository generalUserProfileRepository;


    public Integer userId;
    @Autowired
    private JwtTokenUtil jwtUtil;
    @Autowired
    private UserProfileServiceImpl userProfileServiceImpl;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private GupTypeRepository gupTypeRepository;

    @Autowired
    public ViewAdminProfileImpl(GeneralUserProfileRepository userProfileRepository) {
        this.generalUserProfileRepository = userProfileRepository;
    }

    @Override
    public AdminProfileResponse adminProfileResponse(Integer userId) throws Exception {
        GeneralUserProfile existingProfile = generalUserProfileRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));
        if (existingProfile != null) {
            AdminProfileResponse userProfileDTO = convertToDTO(existingProfile);
            log.warn("User profile found success");
            return userProfileDTO;
        } else {
            throw new ErrorException("User profile not found.", VarList.RSP_NO_DATA_FOUND);
        }
    }


    @Override
    public SuccessResponse updateAdminUserProfile(String authorizationHeader, AddAdminRequest addAdminRequest) {

        try {
            String jwtToken = authorizationHeader.substring(7);
            String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
            if (profile.getIsActive() == 1) {
                userId = profile.getId();

                GeneralUserProfile exisingUser = generalUserProfileRepository.getGeneralUserProfileByEmail(addAdminRequest.getEmail());

                if (exisingUser != null) {
                    exisingUser.setFirstName(addAdminRequest.getFirstName());
                    exisingUser.setLastName(addAdminRequest.getLastName());
                    exisingUser.setEmail(addAdminRequest.getEmail());
                    exisingUser.setPassword(addAdminRequest.getPassword());
                    exisingUser.setGupType(gupTypeRepository.getGupTypeById(addAdminRequest.getGup_type_id()));

                    generalUserProfileRepository.save(exisingUser);

                    log.warn("User updated successfully");

                    SuccessResponse successResponse = new SuccessResponse();
                    successResponse.setMessage("Admin user profile updated successfully");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;

                } else {
                    throw new ErrorException("User profile not found.", VarList.RSP_NO_DATA_FOUND);
                }

            } else {
                throw new ErrorException("User profile not found.", VarList.RSP_NO_DATA_FOUND);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }


    }

    private AdminProfileResponse convertToDTO(GeneralUserProfile userProfile) {
        AdminProfileResponse dto = new AdminProfileResponse();
        dto.setFirstName(userProfile.getFirstName());
        dto.setLastName(userProfile.getLastName());
        dto.setEmail(userProfile.getEmail());
        dto.setPassword(userProfile.getPassword());

        return dto;
    }


}
