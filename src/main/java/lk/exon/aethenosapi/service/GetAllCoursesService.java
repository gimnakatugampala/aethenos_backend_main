package lk.exon.aethenosapi.service;

import lk.exon.aethenosapi.entity.Course;
import lk.exon.aethenosapi.entity.CourseCategory;
import lk.exon.aethenosapi.payload.request.AddCourseCompleteDetailsRequest;
import lk.exon.aethenosapi.payload.request.DisApproveCourseRequest;
import lk.exon.aethenosapi.payload.response.*;

import java.util.List;

public interface GetAllCoursesService {

    List<CourseWithProgressResponse> findByInstructorId();

    List<CourseCategory> getAllCourseCategory();

    List<Course> getAllDraftedCourses();

    SuccessResponse setRequestedCourse(String courseCode);

    GetAllRequestedCourseResponse getAllRequestedCode();

    SuccessResponse setApproveCourse(String courseCode);

    SuccessResponse setDisapproveCourse(DisApproveCourseRequest disApproveCourseRequest);

    GetCourseTitleAndApproveTypeResponse getCourseTitleAndApproveType(String courseCode);

    SuccessResponse submitForReview(String courseCode);

    SuccessResponse disapproveRequestedCourse(DisApproveCourseRequest courseCode);

    String getCurrentApprovalType(String courseCode);

    SuccessResponse approveRequestedCourse(String courseCode);

    String getCourseIsOwned(String courseCode);

    double courseCurriculumProgress(String courseCode);


    String getCourseComment(String courseCode);

    GetCoursesDataResponse getCourseByStudent(String courseCode);

    String getCategorynameBylinkName(String linkName);

    List<String> getTopicBylinkName(String linkName);

    List<GetCoursesDataResponse> getCoursesData();

    List<GetCoursesDataResponse> getNewCourses(String linkName);

    List<GetCoursesDataResponse> getMostPopularCourses(String linkName);

    GetCategoryAndSubCategorynameResponse getCategoryAndSubCategorynameBylinkName(String linkSubName);

    List<GetSubCategoryDetailsResponse> getSubCategoryByCourseLinkName(String linkName);

    List<GetCoursesDataResponse> getTrendingByCourseLinkName(String linkName);

    GetInstructorDetailsResponse getInstructorDetails(String userCode);

    List<GetPopularInstructorsResponse> getPopularInstructors(String linkName);

    List<GetAllcoursesViewResponse> getAllcoursesViewByLinkName(String linkName);

    List<GetTopicsResponse> getPopularTopicByLinkName(String linkName);

    List<GetAllcoursesViewResponse> getAllcoursesViewByInstructor(String userCode);

    List<GetCoursesDataResponse> getNewCoursesBySubCategory(String sublinkName);

    List<GetCoursesDataResponse> getMostPopularCoursesBySubCategory(String sublinkName);

    List<GetCoursesDataResponse> getTrendingBySubCategory(String sublinkName);

    List<GetPopularInstructorsResponse> getPopularInstructorsBySubCategory(String sublinkName);

    List<GetAllcoursesViewResponse> getAllcoursesViewBySubLinkName(String sublinkName);

    List<GetTopicsResponse> getPopularTopicBySubLinkName(String sublinkName);

    List<GetCoursesDataResponse> getMostPopularCoursesByTopic(String linkName);

    List<GetCoursesDataResponse> getNewCoursesByTopic(String topicLinkName);

    List<GetCoursesDataResponse> getTrendingCoursesByTopic(String topicLinkName);
    List<GetCoursesDataResponse> getAllCoursesByInstructorCode(String userCode);

    CheckCourseCompleteDetailsResponse checkCourseCompleteDetails(String courseCode);

    CheckPaidCourseValidationResponse checkPaidCourseValidation(String courseCode);

    ReferralCodeValidationResponse checkReferralCodeValidation(String referralCode);

    List<GetCoursesDataResponse> getAllCoursesByAdmin();

    int getAvailableCouponCountForThisMonth(String courseCode);

    GetExternalCourseLinkAndRatingsResponse getExternalCourseLinkAndRatings(String courseCode);
}
