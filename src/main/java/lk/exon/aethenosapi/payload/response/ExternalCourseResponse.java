package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ExternalCourseResponse {
    private long externalNumberOfStudent;
    private double externalRating;
    private String anyComments;
    private String linkToCourse;
}
