package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetPopularInstructorsResponse {
    private String name;
    private String userCode;
    private String profile_img;
    private String about;
    private double rating;
    private Integer studentsCount;
    private Integer coursesCount;

}
