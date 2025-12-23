package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AddExternalCourseLinkAndRatingsRequest {
    private String courseCode;
    private String linkToCourse;
    private Double externalRating;
    private Long externalNumberOfStudents;
    private String anyComments;
}
