package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class GetAllcoursesViewResponse {

    private String courseCode;
    private GetCoursePricesResponse course_prices;
    private String title;
    private double rating;
    private String description;
    private int lessonsCount;
    private int studentCount;
    private String courseImg;
}
