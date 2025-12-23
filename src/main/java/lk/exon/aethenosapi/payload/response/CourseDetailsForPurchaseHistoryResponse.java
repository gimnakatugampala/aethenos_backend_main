package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CourseDetailsForPurchaseHistoryResponse {
    private String itemCode;
    private String courseCode;
    private String courseTitle;
}
