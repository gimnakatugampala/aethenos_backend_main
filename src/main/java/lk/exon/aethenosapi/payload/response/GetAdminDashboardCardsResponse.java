package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetAdminDashboardCardsResponse {
    private Integer instructorsCount;
    private Integer studentsCount;
    private Integer draftCoursesCount;
    private Integer CoursesSubmissionsCount;
}
