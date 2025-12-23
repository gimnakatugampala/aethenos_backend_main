package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.List;
@Data
@ToString
public class GetOwnRefundsResponse {
    private String refundCode;
    private String refundAmount;
    private String reason;
    private List<CourseDetailsResponse> courseDetailsResponses;
    private String purchasedAmount;
    private String currency;
    private String purchasedDate;
    private String adminAction;
    private String adminComment;
    private String requestDate;

}
