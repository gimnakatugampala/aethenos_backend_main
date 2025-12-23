package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetCourseTitleAndApproveTypeResponse {
    private String title;
    private String approveType;
    private double courseLength;
}
