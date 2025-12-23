package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@ToString
public class GetCoursesDataResponse {
    private String course_code;
    private Double progressValue;
    private boolean IsReview;
    private String sub_title;
    private String created_date;
    private int id;
    private String img;
    private String duration;
    private String course_outline;
    private String level;
    private String title;
    private List<GetRatingResponse> reviews;
    private List<GetRatingResponse> ownReview;
    private int rating_count;
    private double rating;
    private StarsCountResponse ratingDetails;
    private GetCoursePricesResponse course_prices;
    private int lesson;
    private int student;
    private String category;
    private String category_link_name;
    private String sub_category;
    private String sub_category_link_name;
    private String topic;
    private String topic_link_name;
    private String short_desc;
    private String Instructor;
    private String Instructor_code;
    private String instructor_img;
    private String instructor_title;
    private String instructor_desc;
    private String[] features;
    private List<GetSocialLinksResponse> social_links;
    private String language;
    private String certificate;
    private String videoId;
    private String course_main_desc;
    private String course_desc_2;
    private String[] learnList;
    private String course_desc_3;
    private String curriculum_desc;
    private List<GetCourseContentResponse> course_content;
    private int articles_count;
    private int no_of_videos;
    private int downloadable_resources_count;
    private int enrolled_count;
    private List<GetIntendedLearnerResponse> intended_learners;
    private String item_code;
    private boolean IsPaid;
    private String approvalTypeId;
    private Integer allItemsCount;
    private Integer completedItemCount;
    private Date purchasedDate;
    private double totalVideoLength;
    private ExternalCourseResponse externalCourseDetails;

}
