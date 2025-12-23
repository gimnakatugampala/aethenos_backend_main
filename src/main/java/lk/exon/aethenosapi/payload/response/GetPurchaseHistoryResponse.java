package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class GetPurchaseHistoryResponse {
    private String transActionCode;
    private String createdDate;
    private String amount;
    private String currency;
    private String paymentType;
    private List<CourseDetailsForPurchaseHistoryResponse> courseDetails;

}
