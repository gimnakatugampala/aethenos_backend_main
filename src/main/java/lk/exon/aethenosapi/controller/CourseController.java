package lk.exon.aethenosapi.controller;


import lk.exon.aethenosapi.entity.Course;
import lk.exon.aethenosapi.entity.CourseCategory;
import lk.exon.aethenosapi.payload.request.*;
import lk.exon.aethenosapi.payload.response.*;
import lk.exon.aethenosapi.service.CourseService;
import lk.exon.aethenosapi.service.GetAllCoursesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "course")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CourseController {

    private final GetAllCoursesService getAllCoursesService;

    @Autowired
    private CourseService courseService;

    @Autowired
    public CourseController(GetAllCoursesService getAllCoursesService) {
        this.getAllCoursesService = getAllCoursesService;
    }

    @GetMapping(value = "/getCourseByInstructor")
    public List<CourseWithProgressResponse> getCourseByInstructor() {
        return getAllCoursesService.findByInstructorId();
    }

    @PostMapping("/addCourse")
    public SuccessResponse addCourse(CourseRequest courseRequest) {
        return courseService.addCourse(courseRequest);
    }

    @GetMapping("/getCourseCategory")
    public List<CourseCategory> getAllCourseCategory() {
        return getAllCoursesService.getAllCourseCategory();
    }


    @GetMapping("/getDraftcourses")
    public List<Course> getAllDraftCourses() {
        return getAllCoursesService.getAllDraftedCourses();
    }


    @PutMapping("/setRequestedCourse/{courseCode}")
    public SuccessResponse setRequestedCourse(@PathVariable String courseCode) {
        return getAllCoursesService.setRequestedCourse(courseCode);
    }


    @GetMapping("/getAllRequestedCourse")
    public GetAllRequestedCourseResponse setRequestedCourse() {
        return getAllCoursesService.getAllRequestedCode();
    }


    @PutMapping("/setApproveCourse/{courseCode}")
    public SuccessResponse setApproveCourse(@PathVariable String courseCode) {
        return getAllCoursesService.setApproveCourse(courseCode);
    }


    @PutMapping("/setDisapproveCourse")
    public SuccessResponse setDisapproveCourse(DisApproveCourseRequest disApproveCourseRequest) {
        return getAllCoursesService.setDisapproveCourse(disApproveCourseRequest);
    }

    @GetMapping("/getCourseTitleAndApproveType/{courseCode}")
    public GetCourseTitleAndApproveTypeResponse getCourseTitleAndApproveType(@PathVariable("courseCode") String courseCode) {
        return getAllCoursesService.getCourseTitleAndApproveType(courseCode);
    }

    @PutMapping("/submitForReview/{courseCode}")
    public SuccessResponse submitForReview(@PathVariable String courseCode) {
        return getAllCoursesService.submitForReview(courseCode);
    }

    @PutMapping("/disapproveRequestedCourse")
    public SuccessResponse disapproveRequestedCourse(DisApproveCourseRequest disApproveCourseRequest) {
        return getAllCoursesService.disapproveRequestedCourse(disApproveCourseRequest);
    }

    @GetMapping("/getCurrentApprovalType/{courseCode}")
    public String getCurrentApprovalType(@PathVariable String courseCode) {
        return getAllCoursesService.getCurrentApprovalType(courseCode);
    }

    @PutMapping("/approveRequestedCourse/{courseCode}")
    public SuccessResponse approveRequestedCourse(@PathVariable String courseCode) {
        return getAllCoursesService.approveRequestedCourse(courseCode);
    }

    @GetMapping("/getCourseIsOwned/{courseCode}")
    public String getCourseIsOwned(@PathVariable String courseCode) {
        return getAllCoursesService.getCourseIsOwned(courseCode);
    }

    @GetMapping("/courseCurriculumProgress/{courseCode}")
    public double courseCurriculumProgress(@PathVariable String courseCode) {
        return getAllCoursesService.courseCurriculumProgress(courseCode);
    }

    @GetMapping("/getCourseComment/{courseCode}")
    public String getCourseComment(@PathVariable String courseCode) {
        return getAllCoursesService.getCourseComment(courseCode);
    }

    @GetMapping("/getCourseByStudent/{CourseCode}")
    public GetCoursesDataResponse getCourseByStudent(@PathVariable("CourseCode") String CourseCode) {
        return getAllCoursesService.getCourseByStudent(CourseCode);
    }

    @GetMapping("/getCategorynameBylinkName/{linkName}")
    public String getCategorynameBylinkName(@PathVariable("linkName") String linkName) {
        return getAllCoursesService.getCategorynameBylinkName(linkName);
    }

    @GetMapping("/getCategoryAndSubCategorynameBylinkName/{linkSubName}")
    public GetCategoryAndSubCategorynameResponse getCategoryAndSubCategorynameBylinkName(@PathVariable("linkSubName") String linkSubName) {
        return getAllCoursesService.getCategoryAndSubCategorynameBylinkName(linkSubName);
    }

    @GetMapping("/getTopicBylinkName/{linkName}")
    public List<String> getTopicBylinkName(@PathVariable("linkName") String linkName) {
        return getAllCoursesService.getTopicBylinkName(linkName);
    }

    @GetMapping("/getCoursesData")
    public List<GetCoursesDataResponse> getCoursesData() {
        return getAllCoursesService.getCoursesData();
    }

    @GetMapping("/getAllCategorySubCategoryTopics")
    public List<GetCategoryResponse> getAllCategorySubCategoryTopics() {
        return courseService.getAllCategorySubCategoryTopics();
    }

    @GetMapping("/getNewCourses/{linkName}")
    public List<GetCoursesDataResponse> getNewCourses(@PathVariable("linkName") String linkName) {
        return getAllCoursesService.getNewCourses(linkName);
    }

    @GetMapping("/getMostPopularCourses/{linkName}")
    public List<GetCoursesDataResponse> getMostPopularCourses(@PathVariable("linkName") String linkName) {
        return getAllCoursesService.getMostPopularCourses(linkName);
    }

    @GetMapping("/getSubCategoryByCourseLinkName/{linkName}")
    public List<GetSubCategoryDetailsResponse> getSubCategoryByCourseLinkName(@PathVariable("linkName") String linkName) {
        return getAllCoursesService.getSubCategoryByCourseLinkName(linkName);
    }

    @GetMapping("/getTrendingByCourseLinkName/{linkName}")
    public List<GetCoursesDataResponse> getTrendingByCourseLinkName(@PathVariable("linkName") String linkName) {
        return getAllCoursesService.getTrendingByCourseLinkName(linkName);
    }

    @GetMapping("/getInstructorDetails/{userCode}")
    public GetInstructorDetailsResponse getInstructorDetails(@PathVariable("userCode") String userCode) {
        return getAllCoursesService.getInstructorDetails(userCode);
    }

    @GetMapping("/getPopularInstructors/{linkName}")
    public List<GetPopularInstructorsResponse> getPopularInstructors(@PathVariable("linkName") String linkName) {
        return getAllCoursesService.getPopularInstructors(linkName);
    }

    @GetMapping("/getReferralCodeByCourseCode/{courseCode}")
    public SuccessResponse getReferralCodeByCourseCode(@PathVariable("courseCode") String courseCode) {
        return courseService.getReferralCodeByCourseCode(courseCode);
    }

    @GetMapping("/getCourseType")
    public List<CouponTypeResponse> getCourseType() {
        return courseService.getCourseType();
    }

    @PostMapping("/addFreeCoupon")
    public SuccessResponse addFreeCoupon(AddFreeCouponRequest addFreeCouponRequest) {
        return courseService.addFreeCoupon(addFreeCouponRequest);
    }

    @GetMapping("/getCouponsFromCourseCode/{courseCode}")
    public List<CouponResponse> getCouponsFromCourseCode(@PathVariable("courseCode") String courseCode) {
        return courseService.getCouponsFromCourseCode(courseCode);
    }

    @GetMapping("/activeDeactiveCoupon/{couponCode}")
    public SuccessResponse activeDeactiveCoupon(@PathVariable("couponCode") String couponCode) {
        return courseService.activeDeactiveCoupon(couponCode);
    }

    @GetMapping("/getAllcoursesViewByLinkName/{linkName}")
    public List<GetAllcoursesViewResponse> getAllcoursesViewByLinkName(@PathVariable("linkName") String linkName) {
        return getAllCoursesService.getAllcoursesViewByLinkName(linkName);
    }

    @PostMapping("/addDiscountCoupon")
    public SuccessResponse addDiscountCoupon(@RequestBody AddDiscountCouponRequest addDiscountCouponRequest) {
        return courseService.addDiscountCoupon(addDiscountCouponRequest);
    }

    @GetMapping("/getPopularTopicByLinkName/{linkName}")
    public List<GetTopicsResponse> getPopularTopicByLinkName(@PathVariable("linkName") String linkName) {
        return getAllCoursesService.getPopularTopicByLinkName(linkName);
    }

    @GetMapping("/getAllcoursesViewByInstructor/{userCode}")
    public List<GetAllcoursesViewResponse> getAllcoursesViewByInstructor(@PathVariable("userCode") String userCode) {
        return getAllCoursesService.getAllcoursesViewByInstructor(userCode);
    }

    @GetMapping("/getNewCoursesBySubCategory/{sublinkName}")
    public List<GetCoursesDataResponse> getNewCoursesBySubCategory(@PathVariable("sublinkName") String sublinkName) {
        return getAllCoursesService.getNewCoursesBySubCategory(sublinkName);
    }

    @GetMapping("/getMostPopularCoursesBySubCategory/{sublinkName}")
    public List<GetCoursesDataResponse> getMostPopularCoursesBySubCategory(@PathVariable("sublinkName") String sublinkName) {
        return getAllCoursesService.getMostPopularCoursesBySubCategory(sublinkName);
    }

    @GetMapping("/getTrendingBySubCategory/{sublinkName}")
    public List<GetCoursesDataResponse> getTrendingBySubCategory(@PathVariable("sublinkName") String sublinkName) {
        return getAllCoursesService.getTrendingBySubCategory(sublinkName);
    }

    @GetMapping("/getPopularInstructorsBySubCategory/{sublinkName}")
    public List<GetPopularInstructorsResponse> getPopularInstructorsBySubCategory(@PathVariable("sublinkName") String sublinkName) {
        return getAllCoursesService.getPopularInstructorsBySubCategory(sublinkName);
    }

    @GetMapping("/getAllcoursesViewBySubLinkName/{sublinkName}")
    public List<GetAllcoursesViewResponse> getAllcoursesViewBySubLinkName(@PathVariable("sublinkName") String sublinkName) {
        return getAllCoursesService.getAllcoursesViewBySubLinkName(sublinkName);
    }

    @GetMapping("/getPopularTopicBySubLinkName/{sublinkName}")
    public List<GetTopicsResponse> getPopularTopicBySubLinkName(@PathVariable("sublinkName") String sublinkName) {
        return getAllCoursesService.getPopularTopicBySubLinkName(sublinkName);
    }

    @GetMapping("/getTopicsBySubCategory/{subCategoryId}")
    public List<GetTopicsWithIdResponse> getTopicsBySubCategory(@PathVariable("subCategoryId") int subCategoryId) {
        return courseService.getTopicsBySubCategory(subCategoryId);
    }

    @GetMapping("/CheckOwnCourse/{courseCode}")
    public int CheckOwnCourse(@PathVariable("courseCode") String courseCode) {
        return courseService.CheckOwnCourse(courseCode);
    }

    @GetMapping("/getMostPopularCoursesByTopic/{linkName}")
    public List<GetCoursesDataResponse> getMostPopularCoursesByTopic(@PathVariable("linkName") String linkName) {
        return getAllCoursesService.getMostPopularCoursesByTopic(linkName);
    }

    @GetMapping("/getNewCoursesByTopic/{topicLinkName}")
    public List<GetCoursesDataResponse> getNewCoursesByTopic(@PathVariable("topicLinkName") String topicLinkName) {
        return getAllCoursesService.getNewCoursesByTopic(topicLinkName);
    }

    @GetMapping("/getTrendingCoursesByTopic/{topicLinkName}")
    public List<GetCoursesDataResponse> getTrendingCoursesByTopic(@PathVariable("topicLinkName") String topicLinkName) {
        return getAllCoursesService.getTrendingCoursesByTopic(topicLinkName);
    }

    @GetMapping("/getAllCoursesByInstructorCode/{userCode}")
    public List<GetCoursesDataResponse> getAllCoursesByInstructorCode(@PathVariable("userCode") String userCode) {
        return getAllCoursesService.getAllCoursesByInstructorCode(userCode);
    }

    @GetMapping("/checkCourseCompleteDetails/{courseCode}")
    public CheckCourseCompleteDetailsResponse checkCourseCompleteDetails(@PathVariable("courseCode") String courseCode) {
        return getAllCoursesService.checkCourseCompleteDetails(courseCode);
    }

    @GetMapping("/checkPaidCourseValidation/{courseCode}")
    public CheckPaidCourseValidationResponse checkPaidCourseValidation(@PathVariable("courseCode") String courseCode) {
        return getAllCoursesService.checkPaidCourseValidation(courseCode);
    }

    @GetMapping("/checkReferralCodeValidation/{referralCode}")
    public ReferralCodeValidationResponse checkReferralCodeValidation(@PathVariable("referralCode") String referralCode) {
        return getAllCoursesService.checkReferralCodeValidation(referralCode);
    }

    @GetMapping("/getAllCoursesByAdmin")
    public List<GetCoursesDataResponse> getAllCoursesByAdmin() {
        return getAllCoursesService.getAllCoursesByAdmin();
    }

    @GetMapping("/getAvailableCouponCountForThisMonth/{courseCode}")
    public int getAvailableCouponCountForThisMonth(@PathVariable("courseCode") String courseCode) {
        return getAllCoursesService.getAvailableCouponCountForThisMonth(courseCode);
    }
    @PostMapping("/addExternalCourseLinkAndRatings")
    public SuccessResponse addExternalCourseLinkAndRatings(AddExternalCourseLinkAndRatingsRequest addExternalCourseLinkAndRatingsRequest) {
        return courseService.addExternalCourseLinkAndRatings(addExternalCourseLinkAndRatingsRequest);
    }
    @GetMapping("/getExternalCourseLinkAndRatings/{courseCode}")
    public GetExternalCourseLinkAndRatingsResponse getExternalCourseLinkAndRatings(@PathVariable("courseCode") String courseCode) {
        return getAllCoursesService.getExternalCourseLinkAndRatings(courseCode);
    }
}
