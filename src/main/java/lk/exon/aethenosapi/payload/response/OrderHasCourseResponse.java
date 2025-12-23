package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderHasCourseResponse {
    private String currency;
    private String itemCode;
    private String listPrice;
    private String itemPrice;
    private String courseCode;
    private String courseTitle;
}
