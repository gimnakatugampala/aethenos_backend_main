package lk.exon.aethenosapi.controller;


import lk.exon.aethenosapi.payload.request.UserLoginRequset;
import lk.exon.aethenosapi.payload.response.SuccessResponse;
import lk.exon.aethenosapi.payload.response.UserLoginResponse;
import lk.exon.aethenosapi.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/authentication")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/student")
    public UserLoginResponse userLoginResponse(UserLoginRequset request, HttpServletRequest servletRequest) throws Exception {
        UserLoginResponse loginSessionResponse = loginService.userLoginWithPassword(request, servletRequest.getRemoteAddr());
             return loginSessionResponse;
    }

    @PostMapping("/studentLoginWithloginToken/{loginToken}")
    public UserLoginResponse studentLoginWithloginToken(@PathVariable("loginToken") String loginToken) throws Exception {
        UserLoginResponse loginSessionResponse = loginService.studentLoginWithloginToken(loginToken);
        return loginSessionResponse;
    }

    @PostMapping("/admin")
    public UserLoginResponse adminLoginResponse(UserLoginRequset request, HttpServletRequest servletRequest) throws Exception {
        log.info("-----------------Log In with Password-----------------");
        UserLoginResponse loginSessionResponse = loginService.adminLoginWithPassword(request, servletRequest.getRemoteAddr());
        log.info("Login with password " + loginSessionResponse.toString());
        return loginSessionResponse;
    }

    @PostMapping("/instructor")
    public UserLoginResponse instructorLoginResponse(UserLoginRequset request, HttpServletRequest servletRequest) throws Exception {
        log.info("-----------------Log In with Password-----------------");
        UserLoginResponse loginSessionResponse = loginService.instructorLoginWithPassword(request, servletRequest.getRemoteAddr());
        log.info("Login with password " + loginSessionResponse.toString());
        return loginSessionResponse;
    }

    @GetMapping("/getAccountValidation")
    public SuccessResponse getAccountValidation(){
        return loginService.getAccountValidation();
    }

}
