package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class GetInstructorRevenueReportResponse {
    private List<InstructorRevenueReportResponse> instructorRevenueReportResponses;
    private String totalLifeTimeEarning;
    private String date;
}
