package lk.exon.aethenosapi.payload.response;

import lk.exon.aethenosapi.entity.CourseCategory;
import lk.exon.aethenosapi.entity.CourseLevel;
import lk.exon.aethenosapi.entity.CourseSubCategory;
import lk.exon.aethenosapi.entity.Language;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CourseLandingPageResponse {
    private String courseTitle;
    private String courseSubTitle;
    private String description;
    private String languageId;
    private String language;
    private String levelId;
    private String level;
    private String categoryId;
    private String category;
    private String subCategoryId;
    private String subCategory;
    private String[] keywords;
    private String courseImage;
    private String PromotionalVideo;
    private double courseLength;
    private String topicId;

}
