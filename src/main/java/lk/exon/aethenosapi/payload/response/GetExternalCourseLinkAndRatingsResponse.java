package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetExternalCourseLinkAndRatingsResponse {
    private String linkToCourse;
    private double externalRating;
    private long externalNumberOfStudents;
    private String anyComments;
}
