package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class GetRatingResponse {
    private String email;
    private String name;
    private String date;
    private String img;
    private String comment;
    private double rating;

}
