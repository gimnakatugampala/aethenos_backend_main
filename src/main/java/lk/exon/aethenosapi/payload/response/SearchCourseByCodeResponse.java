package lk.exon.aethenosapi.payload.response;

public class SearchCourseByCodeResponse {

    private String courseSubtitle;

    private String courseDescription;

    private String language;

    private String courseLevel;

    private String promotional_video_url;

    private String courseCode;

    private String courseTitle;

    private String imageURL;

    private String ApprovalType;

    private String Category;

    private String instructor;

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getApprovalType() {
        return ApprovalType;
    }

    public void setApprovalType(String approvalType) {
        ApprovalType = approvalType;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getCourseSubtitle() {
        return courseSubtitle;
    }

    public void setCourseSubtitle(String courseSubtitle) {
        this.courseSubtitle = courseSubtitle;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCourseLevel() {
        return courseLevel;
    }

    public void setCourseLevel(String courseLevel) {
        this.courseLevel = courseLevel;
    }

    public String getPromotional_video_url() {
        return promotional_video_url;
    }

    public void setPromotional_video_url(String promotional_video_url) {
        this.promotional_video_url = promotional_video_url;
    }
}
