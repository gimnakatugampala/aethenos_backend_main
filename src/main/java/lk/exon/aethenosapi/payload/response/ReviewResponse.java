package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@ToString
public class ReviewResponse {
    private String reviewCode;
    private String userCode;
    private String fullName;
    private String userProfile;
    private String comment;
    private double rating;
    private Date date;
    private List<RepliesToReviewResponse>replies;
    
}
