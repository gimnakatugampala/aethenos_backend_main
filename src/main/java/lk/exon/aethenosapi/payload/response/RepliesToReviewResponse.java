package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class RepliesToReviewResponse {
    private String userCode;
    private String profileImg;
    private String comment;
    private String name;
    private Integer userType;
    private Date createdDate;
}
