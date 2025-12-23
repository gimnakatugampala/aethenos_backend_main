package lk.exon.aethenosapi.payload.request;


import lk.exon.aethenosapi.controller.LoginController;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserLoginRequset {
        public String email;
        public String password;

}
