package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class GetAllRefundsResponse {
    private String refundCode;
    private String refundAmount;
    private String reason;
    private UserDetails userDetails;
    private CourseDetailsResponse courseDetailsResponse;
    private String purchasedAmount;
    private String currency;
    private String purchasedDate;
    private List<GetOwnRefundsResponse> getOwnRefundsResponse;
    private String status;
}
