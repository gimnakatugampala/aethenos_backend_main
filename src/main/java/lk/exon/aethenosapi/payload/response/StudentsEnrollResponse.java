package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class StudentsEnrollResponse {
    private String courseTitle;
    private String studentName;
    private String profileImg;
    private String email;
    private String country;
    private String EnrolledDate;
    private String lastVisited;
    private double progress;
    private Integer questionAskedCount;
    private Integer questionsAnsweredCount;
    private String studentUserCode;

}
