package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetInstructorDetailsResponse {
    private String name;
    private String profileImage;
    private String headline;
    private Integer totalStudents;
    private Integer reviews;
    private String about;
    private String email;
    private String secondaryEmail;
    private String website;
    private String twitter;
    private String facebook;
    private String linkedin;
    private String youtube;
    private double rating;
}
