package lk.exon.aethenosapi.controller;

import lk.exon.aethenosapi.exception.ErrorException;
import lk.exon.aethenosapi.payload.request.AddAdminRequest;
import lk.exon.aethenosapi.payload.response.AdminProfileResponse;
import lk.exon.aethenosapi.entity.GeneralUserProfile;
import lk.exon.aethenosapi.payload.response.SuccessResponse;
import lk.exon.aethenosapi.security.JwtTokenUtil;
import lk.exon.aethenosapi.service.ViewAdminProfileService;
import lk.exon.aethenosapi.service.impl.UserProfileServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/adminProfile")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j

public class ManageAdminProfileController {

    private final ViewAdminProfileService viewAdminProfileService;

    public Integer userId;
    @Autowired
    private JwtTokenUtil jwtUtil;
    @Autowired
    private UserProfileServiceImpl userProfileServiceImpl;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @Autowired
    public ManageAdminProfileController(ViewAdminProfileService viewAdminProfileService) {
        this.viewAdminProfileService = viewAdminProfileService;
    }

    @GetMapping("/profiledetails")

    public AdminProfileResponse adminProfileResponse(@RequestHeader("Authorization") String authorizationHeader ) {
        try {
            String jwtToken = authorizationHeader.substring(7);
            String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
            if (!username.isEmpty()){
                if (profile.getIsActive() == 1) {
                    userId = profile.getId();
                    return viewAdminProfileService.adminProfileResponse(userId);
                } else {
                    throw new ErrorException("Error! ", "User is not an active User.");
                }
            }else{
                throw new ErrorException("Error! ", "User Profile not found.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @PutMapping("/profiledetailsUpdate")

    public SuccessResponse adminProfileResponse(@RequestHeader("Authorization") String authorizationHeader,
                                                AddAdminRequest addAdminRequest ) {
        try {
            String jwtToken = authorizationHeader.substring(7);
            String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
            if (profile.getIsActive() == 1) {
                return viewAdminProfileService.updateAdminUserProfile(authorizationHeader, addAdminRequest);
            } else {
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }





}
