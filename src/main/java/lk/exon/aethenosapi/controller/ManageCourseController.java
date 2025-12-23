package lk.exon.aethenosapi.controller;

import lk.exon.aethenosapi.entity.*;
import lk.exon.aethenosapi.payload.request.*;
import lk.exon.aethenosapi.payload.response.*;
import lk.exon.aethenosapi.service.ManageCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = "/managecourse")
public class ManageCourseController {
    @Autowired
    private ManageCourseService manageCourseService;

    @PostMapping("/savecourselandingpage")
    public SuccessResponse courseLandingPage(CourseLandingPageRequest courseLandingPageRequest) {
        return manageCourseService.saveCourseLandingPage(courseLandingPageRequest);
    }

    @GetMapping("/getcourselandingpage/{courseCode}")
    public CourseLandingPageResponse GetCourseLandingPage(@PathVariable("courseCode") String courseCode) {
        return manageCourseService.getCourseLandingPage(courseCode);
    }

    @GetMapping("/getIntendedLearners/{courseCode}")
    public IntendedLearnersResponse GetIntendedLearners(@PathVariable("courseCode") String courseCode) {
        return manageCourseService.getIntendedLearners(courseCode);
    }

    @PostMapping("/saveIntendedLearners")
    public SuccessResponse IntendedLearners(@RequestBody IntendedLearnersRequest intendedLearnersRequest) {
        return manageCourseService.saveIntendedLearners(intendedLearnersRequest);
    }

    @PostMapping("/savemessages")
    public SuccessResponse SaveMessages(MessagesRequset messagesRequset) {
        return manageCourseService.saveMessage(messagesRequset);
    }

    @GetMapping("/getMessages/{courseCode}")
    public MessagesResponse GetMessages(@PathVariable("courseCode") String courseCode) {
        return manageCourseService.getMessages(courseCode);
    }

    @PostMapping("/addCoupons")
    public SuccessResponse AddCoupons(AddCouponsRequest addCouponsRequest) {
        return manageCourseService.addCoupons(addCouponsRequest);
    }

    @GetMapping("/getCoupons/{courseCode}")
    public List<CouponsResponse> GetCoupons(@PathVariable("courseCode") String courseCode) {
        return manageCourseService.getCoupons(courseCode);
    }

    @PostMapping("/setCoursePricing")
    public SuccessResponse SaveCoursePriceRang(@RequestBody List<CoursePricingRequest> coursePricingRequest) {
        return manageCourseService.saveCoursePricing(coursePricingRequest);
    }

    @PostMapping("/addSingleCoursePricing")
    public SuccessResponse SaveCoursePricing(@RequestBody AddDefaultPrice addDefaultPrice) {
        return manageCourseService.saveSingleCoursePricing(addDefaultPrice);
    }

    @GetMapping("/getpricing/{courseCode}")
    public List<PricingResponse> GetPricing(@PathVariable("courseCode") String courseCode) {
        return manageCourseService.getPricing(courseCode);
    }

    @GetMapping("/getpricingrange")
    public List<PricingRangeResponse> GetPricingRange() {
        return manageCourseService.getPricingRange();
    }

    @GetMapping("/getCurriculum/{courseCode}")
    public List<CurriculumResponse> GetCurriculum(@PathVariable("courseCode") String courseCode) {
        return manageCourseService.getCurriculum(courseCode);
    }

    @GetMapping("/setUnpublish/{courseCode}")
    public SuccessResponse SetUnpublish(@PathVariable("courseCode") String courseCode) {
        return manageCourseService.setUnpublish(courseCode);
    }

    @GetMapping("/getCourseLandingPageDetails/{courseCode}")
    public CourseLandingPage GetCourseLandingPageDetails(@PathVariable("courseCode") String courseCode) {
        return manageCourseService.getCourseLandingPageDetails(courseCode);
    }

    @GetMapping("/getAllLanguage")
    public List<Language> GetAllLanguage() {
        return manageCourseService.getAllLanguage();
    }

    @GetMapping("/getAllCourseLevels")
    public List<CourseLevel> GetAllLevels() {
        return manageCourseService.getAllCourseLevels();
    }

    @GetMapping("/getAllCourseSubCategory/{categoryID}")
    public List<SubCategoryResponse> GetAllCourseSubCategory(@PathVariable("categoryID") Integer categoryID) {
        return manageCourseService.getAllCourseSubCategory(categoryID);
    }

    @PutMapping("/updateCoupon")
    public SuccessResponse UpdateCoupon(AddCouponsRequest addCouponsRequest) {
        return manageCourseService.updateCoupon(addCouponsRequest);
    }

    @PutMapping("/setCouponDeactive/{couponCode}")
    public SuccessResponse SetCouponDeactive(@PathVariable("couponCode") String couponCode) {
        return manageCourseService.setCouponDeactive(couponCode);
    }

    @PutMapping("/setCouponActive/{couponCode}")
    public SuccessResponse SetCouponActive(@PathVariable("couponCode") String couponCode) {
        return manageCourseService.setCouponActive(couponCode);
    }

    @GetMapping("/getAllDiscountType")
    public List<DiscountType> GetAllDiscountType() {
        return manageCourseService.getAllDiscountType();
    }

    @GetMapping("/getAllPromotionType")
    public List<PromotionType> GetAllPromotionType() {
        return manageCourseService.getAllPromotionType();
    }

    @GetMapping("/getcountries")
    public List<GetCountriesResponse> GetCountries() {
        return manageCourseService.getCountries();
    }

    @PostMapping("/setDefaultPriceRange")
    public SuccessResponse SetDefaultPriceRange(SetDefaultPriceRangeRequest setDefaultPriceRangeRequest) {
        return manageCourseService.setDefaultPriceRange(setDefaultPriceRangeRequest);
    }

    @PostMapping("/addSection")
    public AddCourseSectionResponse AddSection(AddSectionRequest addSectionRequest) {
        return manageCourseService.addSection(addSectionRequest);
    }

    @PostMapping("/addSectionItem")
    public AddSectionCurriculumItemResponse AddSection(AddSectionCurriculumItemRequest addSectionCurriculumItemRequest) {
        return manageCourseService.addSectionItem(addSectionCurriculumItemRequest);
    }

    @PostMapping("/addCurriculumItemFile")
    public SuccessResponse AddCurriculumItemFile(AddCurriculumItemFileRequest addCurriculumItemFileRequest) {
        return manageCourseService.addCurriculumItemFile(addCurriculumItemFileRequest);
    }

    @PostMapping("/addLecture")
    public SuccessResponse AddLecture(AddLectureRequest addLectureRequest) {
        return manageCourseService.addLecture(addLectureRequest);
    }

    @GetMapping("/getAllLectures/{CourseSectionId}")
    public List<GetLectureResponse> GetAllLectures(@PathVariable("CourseSectionId") String CourseSectionId) {
        return manageCourseService.getAllLectures(CourseSectionId);
    }

    @PostMapping("/addQuiz")
    public SuccessResponse AddQuiz(AddQuizRequest addQuizRequest) {
        return manageCourseService.addQuiz(addQuizRequest);
    }

    @PostMapping("/addCourseDefaultPrice")
    public SuccessResponse AddCourseDefaultPrice(AddDefaultPrice addDefaultPrice) {
        return manageCourseService.addCourseDefaultPrice(addDefaultPrice);
    }

    @PutMapping("/addQuestionAndAnswers")
    public SuccessResponse addQuestionAndAnswers(AddQuestionAndAnswersRequest addQuestionAndAnswersRequest) {
        return manageCourseService.addQuestionAndAnswers(addQuestionAndAnswersRequest);
    }
    @PutMapping("/updateQuestionAndAnswers")
    public SuccessResponse updateQuestionAndAnswers(UpdateQuestionAndAnswersRequest updateQuestionAndAnswersRequest) {
        return manageCourseService.updateQuestionAndAnswers(updateQuestionAndAnswersRequest);
    }
    @PutMapping("/deleteQuestionAndAnswers/{quizId}")
    public SuccessResponse deleteQuestionAndAnswers(@PathVariable("quizId") int quizId) {
        return manageCourseService.deleteQuestionAndAnswers(quizId);
    }

    @GetMapping("/getDefaultCoursePricing/{courseCode}")
    public CoursePricingResponse getDefaultCoursePricing(@PathVariable("courseCode") String code) {
        return manageCourseService.getDefaultCoursePricing(code);
    }

    @GetMapping("/getCoursePricing/{courseCode}")
    public GetDefaultPriceResponse getCoursePricing(@PathVariable("courseCode") String code) {
        return manageCourseService.getCoursePricing(code);
    }

    @GetMapping("/addFreeCourse/{courseCode}")
    public SuccessResponse addFreeCourse(@PathVariable("courseCode") String code) {
        return manageCourseService.addFreeCourse(code);
    }

    @GetMapping("/getCoursePayStatus/{courseCode}")
    public SuccessResponse getFreeCourse(@PathVariable("courseCode") String code) {
        return manageCourseService.getFreeCourse(code);
    }

    @GetMapping("/ownThisCourse/{courseCode}")
    public SuccessResponse ownThisCourse(@PathVariable("courseCode") String code) {
        return manageCourseService.ownThisCourse(code);
    }

    @PostMapping("/addDownloadableFile")
    public SuccessResponse addDownloadableFile(AddDownloadableFileRequest addDownloadableFileRequest) {
        return manageCourseService.addDownloadableFile(addDownloadableFileRequest);
    }

    @PostMapping("/addExternalResource")
    public SuccessResponse addExternalResource(AddExternalResourceRequest addExternalResourceRequest) {
        return manageCourseService.addExternalResource(addExternalResourceRequest);
    }

    @PostMapping("/addSourceCode")
    public SuccessResponse addSourceCode(AddSourceCodeRequest addSourceCode) {
        return manageCourseService.addSourceCode(addSourceCode);
    }

    @PutMapping("/addVideo")
    public SuccessResponse addVideo(AddVideoRequest addVideoRequest) {
        return manageCourseService.addVideo(addVideoRequest);
    }

    @PutMapping("/addArticle")
    public SuccessResponse addArticle(AddArticleRequest addArticleRequest) {
        return manageCourseService.addArticle(addArticleRequest);
    }

    @PutMapping("/addDescription")
    public SuccessResponse addDescription(AddDescriptionRequest addDescriptionRequest) {
        return manageCourseService.addDescription(addDescriptionRequest);
    }

    @PutMapping("/setUnPublishCourse/{courseCode}")
    public SuccessResponse setUnPublishCourse(@PathVariable("courseCode") String code) {
        return manageCourseService.setUnPublishCourse(code);
    }

    @GetMapping("/getCoursesUsingLinkName/{linkName}")
    public List<GetCoursesDataResponse> getCoursesUsingLinkName(@PathVariable("linkName") String linkName) {
        return manageCourseService.getCoursesUsingLinkName(linkName);
    }

    @GetMapping("/getCoursesUsingSubLinkName/{subLinkName}")
    public List<GetCoursesDataResponse> getCoursesUsingSubLinkName(@PathVariable("subLinkName") String subLinkName) {
        return manageCourseService.getCoursesUsingSubLinkName(subLinkName);
    }

    @GetMapping("/getCoursesUsingTopicLinkName/{topicLinkName}")
    public List<GetCoursesDataResponse> getCoursesUsingTopicLinkName(@PathVariable("topicLinkName") String topicLinkName) {
        return manageCourseService.getCoursesUsingTopicLinkName(topicLinkName);
    }

    @PostMapping("/deleteCurriculumItemFile")
    public SuccessResponse deleteCurriculumItemFile(DeleteCurriculumItemFileRequest deleteCurriculumItemFileRequest) {
        return manageCourseService.deleteCurriculumItemFile(deleteCurriculumItemFileRequest);
    }

    @PostMapping("/addAssignment")
    public SuccessResponse addAssignment(AddAssignmentRequest addAssignmentRequest) {
        return manageCourseService.addAssignment(addAssignmentRequest);
    }

    @PostMapping("/addCodingExercise")
    public SuccessResponse addCodingExercise(AddCodingExerciseRequest addCodingExerciseRequest) {
        return manageCourseService.addCodingExercise(addCodingExerciseRequest);
    }

    @PostMapping("/addPracticeTest")
    public SuccessResponse addPracticeTest(AddPracticeTestRequest addPracticeTestRequest) {
        return manageCourseService.addPracticeTest(addPracticeTestRequest);
    }

    @PutMapping("/deleteAssignment/{assignmentCode}")
    public SuccessResponse deleteAssignment(@PathVariable("assignmentCode") String assignmentCode) {
        return manageCourseService.deleteAssignment(assignmentCode);
    }

    @PutMapping("/deletePracticeTest/{practiceTestCode}")
    public SuccessResponse deletePracticeTest(@PathVariable("practiceTestCode") String practiceTestCode) {
        return manageCourseService.deletePracticeTest(practiceTestCode);
    }

    @PutMapping("/deleteCodingExercise/{codingExerciseCode}")
    public SuccessResponse deleteCodingExercise(@PathVariable("codingExerciseCode") String codingExerciseCode) {
        return manageCourseService.deleteCodingExercise(codingExerciseCode);
    }

    @PutMapping("/deletequiz/{curriculumItemId}")
    public SuccessResponse deletequiz(@PathVariable("curriculumItemId") Integer curriculumItemId) {
        return manageCourseService.deletequiz(curriculumItemId);
    }

    @PutMapping("/deletelecture/{curriculumItemId}")
    public SuccessResponse deletelecture(@PathVariable("curriculumItemId") Integer curriculumItemId) {
        return manageCourseService.deletelecture(curriculumItemId);
    }

    @PutMapping("/deletecourseSection/{courseSectionId}")
    public SuccessResponse deletecourseSection(@PathVariable("courseSectionId") Integer courseSectionId) {
        return manageCourseService.deletecourseSection(courseSectionId);
    }

    @PostMapping("/deleteIntendedLearners")
    public SuccessResponse deleteIntendedLearners(DeleteIntendedLearnersRequest deleteIntendedLearnersRequest) {
        return manageCourseService.deleteIntendedLearners(deleteIntendedLearnersRequest);
    }

    @PutMapping("/updateSectionName")
    public SuccessResponse updateSectionName(UpdateSectionNameRequest updateSectionNameRequest) {
        return manageCourseService.updateSectionName(updateSectionNameRequest);
    }

    @PutMapping("/updateCurriculumItemName")
    public SuccessResponse updateCurriculumItemName(UpdateCurriculumItemNameRequest updateCurriculumItemNameRequest) {
        return manageCourseService.updateCurriculumItemName(updateCurriculumItemNameRequest);
    }
    @PostMapping("/deleteExternalResources/{curriculumItemFileId}")
    public SuccessResponse deleteExternalResources(@PathVariable("curriculumItemFileId") Integer curriculumItemFileId) {
        return manageCourseService.deleteExternalResources(curriculumItemFileId);
    }
    @PutMapping("/setPreviewVideo")
    public SuccessResponse setPreviewVideo(SetPreviewVideoRequest setPreviewVideoRequest) {
        return manageCourseService.setPreviewVideo(setPreviewVideoRequest);
    }
    @PutMapping("/updateSectionCurriculumItemOrder")
    public SuccessResponse updateSectionCurriculumItemOrder(UpdateSectionCurriculumItemOrderRequest updateSectionCurriculumItemOrderRequest) {
        return manageCourseService.updateSectionCurriculumItemOrder(updateSectionCurriculumItemOrderRequest);
    }
    @PutMapping("/updateQuizOrder")
    public SuccessResponse updateQuizOrder(UpdateQuizOrderRequest updateQuizOrderRequest) {
        return manageCourseService.updateQuizOrder(updateQuizOrderRequest);
    }
    @PutMapping("/updateCourseSectionOrder")
    public SuccessResponse updateCourseSectionOrder(UpdateCourseSectionOrderRequest updateCourseSectionOrderRequest) {
        return manageCourseService.updateCourseSectionOrder(updateCourseSectionOrderRequest);
    }
    @PutMapping("/setPublishCourse/{code}")
    public SuccessResponse setPublishCourse(@PathVariable("code") String code) {
        return manageCourseService.setPublishCourse(code);
    }
}
