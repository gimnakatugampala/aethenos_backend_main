package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class GetCourseWithReviewsResponse {
    private String courseTitle;
    private String courseImg;
    private double rating;
    private List<ReviewResponse> reviewResponses;

}
