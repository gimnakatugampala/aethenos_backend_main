package lk.exon.aethenosapi.service;

import lk.exon.aethenosapi.payload.request.UserLoginRequset;
import lk.exon.aethenosapi.payload.response.SuccessResponse;
import lk.exon.aethenosapi.payload.response.UserLoginResponse;

public interface LoginService {
    UserLoginResponse userLoginWithPassword(UserLoginRequset request, String remoteAddr) throws Exception;

    UserLoginResponse adminLoginWithPassword(UserLoginRequset request, String remoteAddr) throws Exception;

    UserLoginResponse instructorLoginWithPassword(UserLoginRequset request, String remoteAddr) throws Exception;

    SuccessResponse getAccountValidation();

    UserLoginResponse studentLoginWithloginToken(String loginToken);
}
