package lk.exon.aethenosapi.payload.response;

import lk.exon.aethenosapi.entity.ApprovalType;
import lk.exon.aethenosapi.entity.CourseCategory;
import lk.exon.aethenosapi.entity.InstructorProfile;
import lombok.Data;
import lombok.ToString;
import java.util.Date;
import java.util.List;

@Data
@ToString
public class RequestedCourseResponse {
    private int id;
    private String code;
    private String courseTitle;
    private String comment;
    private String img;
    private String test_video;
    private double courseLength;
    private Date createdDate;
    private int isPaid;
    private InstructorProfile instructorId;
    private ApprovalType approvalType;
    private CourseCategory courseCategory;
    private Byte isOwned;
    private Integer buyCount;
    private String referralCode;
    private List<GetCourseContentResponse> course_content;
    private List<CouponResponse> promotions;
    private String anyComments;
    private long externalNumberOfStudents;
    private double externalRating;
    private String linkToCourse;
}
