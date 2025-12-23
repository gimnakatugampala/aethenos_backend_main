package lk.exon.aethenosapi.service;

import lk.exon.aethenosapi.entity.*;
import lk.exon.aethenosapi.payload.request.*;
import lk.exon.aethenosapi.payload.response.*;

import java.util.List;

public interface ManageCourseService {

    SuccessResponse saveCourseLandingPage(CourseLandingPageRequest courseLandingPageRequest);

    SuccessResponse saveIntendedLearners(IntendedLearnersRequest intendedLearnersRequest);

    SuccessResponse saveMessage(MessagesRequset messagesRequset);

    SuccessResponse addCoupons(AddCouponsRequest addCouponsRequest);

    List<CouponsResponse> getCoupons(String courseCode);

    SuccessResponse saveCoursePricing(List<CoursePricingRequest> coursePricingRequest);

    SuccessResponse saveSingleCoursePricing(AddDefaultPrice addDefaultPrice);

    CourseLandingPageResponse getCourseLandingPage(String courseCode);

    IntendedLearnersResponse getIntendedLearners(String courseCode);

    MessagesResponse getMessages(String courseCode);

    List<PricingResponse> getPricing(String courseCode);

    List<PricingRangeResponse> getPricingRange();

    List<CurriculumResponse> getCurriculum(String courseCode);

    SuccessResponse setUnpublish(String courseCode);

    CourseLandingPage getCourseLandingPageDetails(String courseCode);

    List<Language> getAllLanguage();

    List<CourseLevel> getAllCourseLevels();

    List<SubCategoryResponse> getAllCourseSubCategory(Integer categoryID);

    SuccessResponse updateCoupon(AddCouponsRequest addCouponsRequest);

    SuccessResponse setCouponDeactive(String couponCode);

    SuccessResponse setCouponActive(String couponCode);

    List<DiscountType> getAllDiscountType();

    List<PromotionType> getAllPromotionType();

    List<GetCountriesResponse> getCountries();

    SuccessResponse setDefaultPriceRange(SetDefaultPriceRangeRequest setDefaultPriceRangeRequest);

    AddCourseSectionResponse addSection(AddSectionRequest addSectionRequest);

    AddSectionCurriculumItemResponse addSectionItem(AddSectionCurriculumItemRequest addSectionCurriculumItemRequest);

    SuccessResponse addCurriculumItemFile(AddCurriculumItemFileRequest addCurriculumItemFileRequest);

    SuccessResponse addLecture(AddLectureRequest addLectureRequest);

    List<GetLectureResponse> getAllLectures(String courseSectionId);

    SuccessResponse addQuiz(AddQuizRequest addQuizRequest);

    SuccessResponse addCourseDefaultPrice(AddDefaultPrice addDefaultPrice);

    SuccessResponse addQuestionAndAnswers(AddQuestionAndAnswersRequest addQuestionAndAnswersRequest);

    CoursePricingResponse getDefaultCoursePricing(String code);

    GetDefaultPriceResponse getCoursePricing(String code);

    SuccessResponse addFreeCourse(String code);

    SuccessResponse getFreeCourse(String code);

    SuccessResponse ownThisCourse(String code);

    SuccessResponse addDownloadableFile(AddDownloadableFileRequest addDownloadableFileRequest);

    SuccessResponse addExternalResource(AddExternalResourceRequest addExternalResourceRequest);

    SuccessResponse addSourceCode(AddSourceCodeRequest addSourceCode);

    SuccessResponse addVideo(AddVideoRequest addVideoRequest);

    SuccessResponse addArticle(AddArticleRequest addArticleRequest);

    SuccessResponse addDescription(AddDescriptionRequest addDescriptionRequest);

    SuccessResponse setUnPublishCourse(String code);

    List<GetCoursesDataResponse> getCoursesUsingLinkName(String linkName);

    List<GetCoursesDataResponse> getCoursesUsingSubLinkName(String subLinkName);

    List<GetCoursesDataResponse> getCoursesUsingTopicLinkName(String topicLinkName);

    SuccessResponse deleteCurriculumItemFile(DeleteCurriculumItemFileRequest deleteCurriculumItemFileRequest);

    SuccessResponse addAssignment(AddAssignmentRequest addAssignmentRequest);

    SuccessResponse addCurriculumVideo(AddCurriculumVideoRequest addCurriculumVideoRequest);

    SuccessResponse addCurriculumDownloadableFile(AddCurriculumDownloadableFileRequest addCurriculumDownloadableFileRequest);

    SuccessResponse addCodingExercise(AddCodingExerciseRequest addCodingExerciseRequest);

    SuccessResponse addPracticeTest(AddPracticeTestRequest addPracticeTestRequest);

    SuccessResponse deleteAssignment(String assignmentCode);

    SuccessResponse deletePracticeTest(String practiceTestCode);

    SuccessResponse deleteCodingExercise(String codingExerciseCode);

    SuccessResponse deletequiz(Integer curriculumItemId);

    SuccessResponse deletelecture(Integer curriculumItemId);

    SuccessResponse deletecourseSection(Integer courseSectionId);

    SuccessResponse deleteIntendedLearners(DeleteIntendedLearnersRequest deleteIntendedLearnersRequest);

    SuccessResponse updateSectionName(UpdateSectionNameRequest updateSectionNameRequest);

    SuccessResponse updateCurriculumItemName(UpdateCurriculumItemNameRequest updateCurriculumItemNameRequest);

    SuccessResponse deleteExternalResources(Integer curriculumItemFileId);

    SuccessResponse setPreviewVideo(SetPreviewVideoRequest setPreviewVideoRequest);

    SuccessResponse updateQuestionAndAnswers(UpdateQuestionAndAnswersRequest updateQuestionAndAnswersRequest);

    SuccessResponse deleteQuestionAndAnswers(int quizId);

    SuccessResponse updateSectionCurriculumItemOrder(UpdateSectionCurriculumItemOrderRequest updateSectionCurriculumItemOrderRequest);

    SuccessResponse updateQuizOrder(UpdateQuizOrderRequest updateQuizOrderRequest);

    SuccessResponse updateCourseSectionOrder(UpdateCourseSectionOrderRequest updateCourseSectionOrderRequest);

    SuccessResponse setPublishCourse(String code);
}
