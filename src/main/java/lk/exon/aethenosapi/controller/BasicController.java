package lk.exon.aethenosapi.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by IntelliJ IDEA Ultimate.
 * User: Pasindu Raveen
 * Date: 12/9/2021
 * Time: 5:57 PM
 * arc-seller-backend
 */
@RestController
@CrossOrigin(allowedHeaders = "*" , origins = "*")
public class BasicController {
    @RequestMapping("/")
    public String landingView(){
      return  "Aethonos API Backend Test";
    }
}

