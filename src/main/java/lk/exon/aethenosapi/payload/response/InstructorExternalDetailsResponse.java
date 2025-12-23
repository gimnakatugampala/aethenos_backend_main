package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class InstructorExternalDetailsResponse {
    private String linkToCourse;
    private double externalRating;
    private int externalNumberOfStudents;
    private String anyComments;
}
