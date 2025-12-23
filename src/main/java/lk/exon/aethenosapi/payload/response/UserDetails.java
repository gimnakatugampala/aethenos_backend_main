package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserDetails {
    private String userCode;
    private String userName;
    private String email;
    private String profileImg;
    private String registeredDate;
    private int noOfRefundRequest;
    private int noOfRefundRejections;
    private int noOfRefundGranted;
    private int totalNumberOfRefunds;
}
