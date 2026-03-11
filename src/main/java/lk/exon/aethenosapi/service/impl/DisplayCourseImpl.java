package lk.exon.aethenosapi.service.impl;

import lk.exon.aethenosapi.entity.*;
import lk.exon.aethenosapi.exception.ErrorException;
import lk.exon.aethenosapi.payload.response.*;
import lk.exon.aethenosapi.repository.*;
import lk.exon.aethenosapi.service.DisplayCourseService;
import lk.exon.aethenosapi.utils.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DisplayCourseImpl implements DisplayCourseService {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseLandingPageRepository courseLandingPageRepository;
    @Autowired
    private CourseKeywordRepository courseKeywordRepository;
    @Autowired
    private CoursePriceRepository coursePriceRepository;
    @Autowired
    private CourseSectionRepository courseSectionRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private CourseLevelRepository courseLevelRepository;
    @Autowired
    private CourseSubCategoryRepository courseSubCategoryRepository;
    @Autowired
    private CourseCategoryRepository courseCategoryRepository;
    @Autowired
    private SectionCurriculumItemRepository sectionCurriculumItemRepository;
    @Autowired
    private CurriculumItemFileRepository curriculumItemFileRepository;
    @Autowired
    private CurriculumItemTypeRepository curriculumItemTypeRepository;
    @Autowired
    private CourseIntentedLearnerRepository courseIntentedLearnerRepository;
    @Autowired
    private OrderHasCourseRepository orderHasCourseRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private CurriculumItemFileTypeRepository curriculumItemFileTypeRepository;
    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private AssignmentRepository assignmentRepository;
    @Autowired
    private CodingExerciseRepository codingExerciseRepository;
    @Autowired
    private PracticeTestRepository practiceTestRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private EuroCountryRepository euroCountryRepository;
    @Autowired
    private ExternalCourseRepository externalCourseRepository;
    private SuccessResponse successResponse = new SuccessResponse();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public List<GetCoursesDataResponse> searchCourses(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            throw new ErrorException("Empty keyword", VarList.RSP_NO_DATA_FOUND);
        } else {
            List<CourseLandingPage> courseLandingPageList = courseLandingPageRepository.findCourseLandingPagesByCourse_CourseTitleContainingIgnoreCaseOrSubTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword, keyword);
            if (courseLandingPageList.size() == 0) {
                throw new ErrorException("Empty results related to keyword", VarList.RSP_NO_DATA_FOUND);
            } else {
                List<GetCoursesDataResponse> responseLists = new ArrayList<>();
                try {


                    for (CourseLandingPage courseLandingPage : courseLandingPageList) {

                        if (courseLandingPage.getCourse().getApprovalType().getId() == 5) {

                            GetCoursesDataResponse getCoursesDataResponse = new GetCoursesDataResponse();
                            getCoursesDataResponse.setCourse_code(courseLandingPage.getCourse().getCode());
                            getCoursesDataResponse.setCreated_date(courseLandingPage.getCourse().getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter));
                            getCoursesDataResponse.setId(courseLandingPage.getCourse().getId());
                            getCoursesDataResponse.setIsPaid(courseLandingPage.getCourse().getIsPaid() == 2);
                            getCoursesDataResponse.setImg(courseLandingPage.getCourse().getImg());
                            getCoursesDataResponse.setDuration(Double.toString(courseLandingPage.getCourse().getCourseLength()));
                            getCoursesDataResponse.setCourse_outline("OutLine is not added to the course");

                            ExternalCourse externalCourse = externalCourseRepository.getExternalCourseByCourse(courseLandingPage.getCourse());
                            if (externalCourse != null) {
                                ExternalCourseResponse externalCourseResponse = new ExternalCourseResponse();
                                externalCourseResponse.setExternalNumberOfStudent(externalCourse.getExternalNumberOfStudents());
                                externalCourseResponse.setExternalRating(externalCourse.getExternalRating());
                                externalCourseResponse.setAnyComments((externalCourse.getAnyComment() == null || externalCourse.getAnyComment().isEmpty()) ? "" : externalCourseResponse.getAnyComments());
                                externalCourseResponse.setLinkToCourse((externalCourse.getLinkToCourse() == null || externalCourse.getLinkToCourse().isEmpty()) ? "" : externalCourse.getLinkToCourse());
                                getCoursesDataResponse.setExternalCourseDetails(externalCourseResponse);
                            }

                            getCoursesDataResponse.setLevel((courseLandingPage != null) ? courseLandingPage.getCourseLevel().getName() : null);
                            getCoursesDataResponse.setTitle(courseLandingPage.getCourse().getCourseTitle());
                            List<OrderHasCourse> orderHasCourses = orderHasCourseRepository.getOrderHasCoursesByCourse(courseLandingPage.getCourse());
                            List<GetRatingResponse> getRatingResponses = new ArrayList<>();
                            int rating_count = 0;
                            double rating_val = 0;

                            int oneRatingCount = 0;
                            int onePointFiveRatingCount = 0;
                            int twoRatingCount = 0;
                            int twoPointFiveRatingCount = 0;
                            int threeRatingCount = 0;
                            int threePointFiveRatingCount = 0;
                            int fourRatingCount = 0;
                            int fourPointFiveRatingCount = 0;
                            int fiveRatingCount = 0;
                            List<Review> reviews = reviewRepository.getReviewsByCourse(courseLandingPage.getCourse());
                            for (Review review : reviews) {
                                GetRatingResponse getRatingResponse = new GetRatingResponse();
                                getRatingResponse.setEmail(review.getGeneralUserProfile().getEmail());
                                getRatingResponse.setName(review.getGeneralUserProfile().getFirstName() + " " + review.getGeneralUserProfile().getLastName());
                                getRatingResponse.setRating(review.getRating());
                                getRatingResponse.setDate(review.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter));
                                getRatingResponse.setImg(review.getOrderHasCourse().getOrder().getGeneralUserProfile().getProfileImg());
                                getRatingResponse.setComment(review.getComment() == null ? "" : review.getComment());
                                getRatingResponses.add(getRatingResponse);
                                rating_count++;
                                rating_val = rating_val + review.getRating();

                                if (review.getRating() == 1) {
                                    oneRatingCount++;
                                } else if (review.getRating() == 1.5) {
                                    onePointFiveRatingCount++;
                                } else if (review.getRating() == 2) {
                                    twoRatingCount++;
                                } else if (review.getRating() == 2.5) {
                                    twoPointFiveRatingCount++;
                                } else if (review.getRating() == 3) {
                                    threeRatingCount++;
                                } else if (review.getRating() == 3.5) {
                                    threePointFiveRatingCount++;
                                } else if (review.getRating() == 4) {
                                    fourRatingCount++;
                                } else if (review.getRating() == 4.5) {
                                    fourPointFiveRatingCount++;
                                } else if (review.getRating() == 5) {
                                    fiveRatingCount++;
                                }
                            }

                            getCoursesDataResponse.setReviews(getRatingResponses);
                            getCoursesDataResponse.setRating_count(rating_count);

                            StarsCountResponse starsCountResponse = new StarsCountResponse();
                            starsCountResponse.setOneRatingCount(oneRatingCount);
                            starsCountResponse.setOnePointFiveRatingCount(onePointFiveRatingCount);
                            starsCountResponse.setTwoRatingCount(twoRatingCount);
                            starsCountResponse.setTwoPointFiveRatingCount(twoPointFiveRatingCount);
                            starsCountResponse.setThreeRatingCount(threeRatingCount);
                            starsCountResponse.setThreePointFiveRatingCount(threePointFiveRatingCount);
                            starsCountResponse.setFourRatingCount(fourRatingCount);
                            starsCountResponse.setFourPointFiveRatingCount(fourPointFiveRatingCount);
                            starsCountResponse.setFiveRatingCount(fiveRatingCount);
                            getCoursesDataResponse.setRatingDetails(starsCountResponse);
                            getCoursesDataResponse.setRating(rating_count == 0 ? 0 : rating_val / rating_count);


                            List<GetCoursePricingResponse> responseList = new ArrayList<>();
                            List<CoursePrice> coursePriceList = coursePriceRepository.getCoursePriceByCourse(courseLandingPage.getCourse());
                            GetCoursePricesResponse response = new GetCoursePricesResponse();
                            if (coursePriceList.size() > 0) {

                                for (CoursePrice coursePrice : coursePriceList) {
                                    if (coursePrice.getCountry().getId() == 30 && coursePrice.getCurrency().getId() == 30) {
                                        response.setGlobalListPrice(coursePrice.getValue());
                                        response.setDiscountTypeId(coursePrice.getDiscountType().getId());
                                        response.setDiscountType(coursePrice.getDiscountType().getName());
                                        response.setDiscountAmount(coursePrice.getDiscountValue());
                                        try {
                                            response.setDiscount(coursePrice.getDiscount());
                                            response.setGlobalNetPrice(coursePrice.getNetPrice());
                                        } catch (NullPointerException ex) {
                                            response.setDiscount(0.0);
                                            response.setGlobalNetPrice(0.0);
                                        }

                                    } else {
                                        GetCoursePricingResponse getCoursePricingResponse = new GetCoursePricingResponse();
                                        try {
                                            getCoursePricingResponse.setNetPrice(coursePrice.getNetPrice());
                                            getCoursePricingResponse.setDiscount(coursePrice.getDiscount());
                                        } catch (NullPointerException ex) {
                                            getCoursePricingResponse.setNetPrice(0.0);
                                            getCoursePricingResponse.setDiscount(0.0);
                                        }
                                        getCoursePricingResponse.setListPrice(coursePrice.getValue());
                                        getCoursePricingResponse.setDiscountTypeId(coursePrice.getDiscountType().getId());
                                        getCoursePricingResponse.setDiscountType(coursePrice.getDiscountType().getName());
                                        getCoursePricingResponse.setCountry(coursePrice.getCountry().getName());

                                        getCoursePricingResponse.setDiscountAmount(coursePrice.getDiscountValue());
                                        responseList.add(getCoursePricingResponse);
                                        response.setPrices(responseList);
                                    }
                                }
                            }

                            getCoursesDataResponse.setCourse_prices(response);

                            List<CourseSection> courseSections = courseSectionRepository.getCourseSectionByCourse(courseLandingPage.getCourse());
                            getCoursesDataResponse.setLesson(courseSections.size());
                            getCoursesDataResponse.setStudent(orderHasCourses.size());
                            getCoursesDataResponse.setCategory(courseLandingPage.getCourse().getCourseCategory().getName());
                            getCoursesDataResponse.setCategory_link_name(courseLandingPage.getCourse().getCourseCategory().getLinkName());
                            getCoursesDataResponse.setSub_category(courseLandingPage.getSubcategory().getName());
                            getCoursesDataResponse.setSub_category_link_name(courseLandingPage.getSubcategory().getSubLinkName());
                            getCoursesDataResponse.setTopic(courseLandingPage.getTopic().getTopic());
                            getCoursesDataResponse.setTopic_link_name(courseLandingPage.getTopic().getLinkName());
                            getCoursesDataResponse.setShort_desc((courseLandingPage != null) ? courseLandingPage.getDescription() : null);
                            GeneralUserProfile instructor = courseLandingPage.getCourse().getInstructorId().getGeneralUserProfile();
                            getCoursesDataResponse.setInstructor_code(instructor.getUserCode());
                            getCoursesDataResponse.setInstructor(instructor.getFirstName() + " " + courseLandingPage.getCourse().getInstructorId().getGeneralUserProfile().getLastName());
                            getCoursesDataResponse.setInstructor_img(instructor.getProfileImg());
                            getCoursesDataResponse.setInstructor_title(courseLandingPage.getCourse().getInstructorId().getHeadline());
                            getCoursesDataResponse.setInstructor_desc(courseLandingPage.getCourse().getInstructorId().getBiography());
                            getCoursesDataResponse.setFeatures(null);
                            List<GetSocialLinksResponse> socialLinksResponsesList = new ArrayList<>();
                            GetSocialLinksResponse socialLinks = new GetSocialLinksResponse();
                            socialLinks.setLink(courseLandingPage.getCourse().getInstructorId().getWebsite());
                            socialLinks.setIcon("fa-solid fa-globe");
                            socialLinks.setTarget("Web Site");
                            socialLinksResponsesList.add(socialLinks);
                            socialLinks = new GetSocialLinksResponse();
                            socialLinks.setLink(courseLandingPage.getCourse().getInstructorId().getYoutube());
                            socialLinks.setIcon("fa-brands fa-youtube");
                            socialLinks.setTarget("youtube");
                            socialLinksResponsesList.add(socialLinks);
                            socialLinks = new GetSocialLinksResponse();
                            socialLinks.setLink(courseLandingPage.getCourse().getInstructorId().getFacebook());
                            socialLinks.setIcon("fa-brands fa-facebook");
                            socialLinks.setTarget("faceBook");
                            socialLinksResponsesList.add(socialLinks);
                            socialLinks = new GetSocialLinksResponse();
                            socialLinks.setLink(courseLandingPage.getCourse().getInstructorId().getTwitter());
                            socialLinks.setIcon("fa-brands fa-twitter");
                            socialLinks.setTarget("twitter");
                            socialLinksResponsesList.add(socialLinks);
                            socialLinks = new GetSocialLinksResponse();
                            socialLinks.setLink(courseLandingPage.getCourse().getInstructorId().getLinkedin());
                            socialLinks.setIcon("fa-brands fa-linkedin");
                            socialLinks.setTarget("linkdin");
                            socialLinksResponsesList.add(socialLinks);
                            getCoursesDataResponse.setSocial_links(socialLinksResponsesList);
                            getCoursesDataResponse.setLanguage((courseLandingPage != null) ? courseLandingPage.getLanguage().getName() : null);
                            getCoursesDataResponse.setSub_title((courseLandingPage != null) ? courseLandingPage.getSubTitle() : null);
                            getCoursesDataResponse.setCertificate("yes");
                            getCoursesDataResponse.setVideoId(courseLandingPage.getCourse().getTest_video());
                            getCoursesDataResponse.setCourse_main_desc((courseLandingPage != null) ? courseLandingPage.getDescription() : null);
                            getCoursesDataResponse.setCourse_desc_2((courseLandingPage != null) ? courseLandingPage.getDescription() : null);
                            getCoursesDataResponse.setLearnList(null);
                            getCoursesDataResponse.setCourse_desc_3((courseLandingPage != null) ? courseLandingPage.getDescription() : null);
                            getCoursesDataResponse.setCurriculum_desc("Curriculum Description is not added to the course");


                            int article_count = 0;
                            int video_count = 0;
                            int downloadable_resources_count = 0;

                            List<GetCourseContentResponse> getCourseContentResponses = new ArrayList<>();
                            for (CourseSection courseSectionObj : courseSections) {
                                GetCourseContentResponse getCourseContentResponse = new GetCourseContentResponse();
                                getCourseContentResponse.setSection_id(courseSectionObj.getId());
                                getCourseContentResponse.setSection_name(courseSectionObj.getSectionName());
                                int on_of_lectures = 0;
                                int on_of_quize = 0;
                                int on_of_assignment = 0;
                                int on_of_codingExercise = 0;
                                int on_of_practiceTest = 0;
                                List<SectionCurriculumItem> articleCounts = sectionCurriculumItemRepository.getSectionCurriculumItemsByCourseSection(courseSectionObj);
                                List<GetSectionCurriculumItemResponse> getSectionCurriculumItemResponses = new ArrayList<>();
                                for (SectionCurriculumItem item : articleCounts) {
                                    if (!item.getArticle().equals("N/A")) {
                                        article_count++;
                                    }
                                    if (item.getCurriculumItemType().getId() == 1) {
                                        on_of_lectures++;
                                    } else if (item.getCurriculumItemType().getId() == 2) {
                                        on_of_quize++;
                                    } else if (item.getCurriculumItemType().getId() == 3) {
                                        on_of_assignment++;
                                    } else if (item.getCurriculumItemType().getId() == 4) {
                                        on_of_codingExercise++;
                                    } else if (item.getCurriculumItemType().getId() == 5) {
                                        on_of_practiceTest++;
                                    }

                                    GetSectionCurriculumItemResponse getSectionCurriculumItemResponse = new GetSectionCurriculumItemResponse();
                                    getSectionCurriculumItemResponse.setTitle(item.getTitle());
                                    getSectionCurriculumItemResponse.setArticle(item.getArticle());
                                    getSectionCurriculumItemResponse.setDescription(item.getDescription());
                                    getSectionCurriculumItemResponse.setCurriculum_item_type(item.getCurriculumItemType().getName());
                                    getSectionCurriculumItemResponse.setCurriculumItemId(item.getId());
                                    getSectionCurriculumItemResponse.setArrangeNo(item.getArrangedNo() == null ? "" : item.getArrangedNo().toString());
                                    Assignment assignment = assignmentRepository.getAssignmentBySectionCurriculumItem(item);
                                    List<GetAssignment> getAssignments = new ArrayList<>();
                                    if (assignment != null) {
                                        GetAssignment getAssignment = new GetAssignment();
                                        getAssignment.setAssignmentCode(assignment.getAssignmentCode());
                                        getAssignment.setDuration(assignment.getDuration());
                                        getAssignment.setInstructions(assignment.getInstructions());
                                        CurriculumItemFileType curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(5);
                                        CurriculumItemFile curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                                        getAssignment.setAssignmentVideo(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                                        curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(6);
                                        curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                                        getAssignment.setDownloadableResource(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                                        getAssignment.setExternalLink(assignment.getExternalLink() != null ? assignment.getExternalLink() : "");
                                        getAssignment.setQuestion(assignment.getQuestions() != null ? assignment.getQuestions() : "");
                                        curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(7);
                                        curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                                        getAssignment.setQuestionSheet(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                                        getAssignment.setQuestionExternalLink(assignment.getQuestionsExternalLink() != null ? assignment.getQuestionsExternalLink() : "");
                                        getAssignment.setSolutions(assignment.getSolutions() != null ? assignment.getSolutions() : "");
                                        curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(8);
                                        curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                                        getAssignment.setSolutionVideo(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                                        curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(9);
                                        curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                                        getAssignment.setSolutionsSheet(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                                        getAssignment.setSolutionsExternalLink(assignment.getSolutionsExternalLink() != null ? assignment.getSolutionsExternalLink() : "");
                                        getAssignments.add(getAssignment);
                                    }
                                    getSectionCurriculumItemResponse.setGetAssignments(getAssignments);
                                    CodingExercise codingExercise = codingExerciseRepository.getCodingExerciseBySectionCurriculumItem(item);
                                    List<GetCodingExercise> getCodingExercises = new ArrayList<>();
                                    if (codingExercise != null) {
                                        GetCodingExercise getCodingExercise = new GetCodingExercise();
                                        getCodingExercise.setCodingExerciseCode(codingExercise.getCodingExerciseCode());
                                        getCodingExercise.setInstructions(codingExercise.getInstructions());
                                        CurriculumItemFileType curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(10);
                                        CurriculumItemFile curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                                        getCodingExercise.setCodingVideo(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                                        curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(11);
                                        curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                                        getCodingExercise.setDownloadableResource(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                                        getCodingExercise.setCodingExternalLink(codingExercise.getExternalLink() != null ? codingExercise.getExternalLink() : "");
                                        curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(12);
                                        curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                                        getCodingExercise.setCodingExerciseSheet(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                                        getCodingExercise.setCodingExternalLink(codingExercise.getCodingLink() != null ? codingExercise.getCodingLink() : "");
                                        curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(13);
                                        curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                                        getCodingExercise.setCodingExerciseVideo(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                                        curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(15);
                                        curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                                        getCodingExercise.setCodingSolutionsSheet(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                                        getCodingExercise.setSolutionsExternalLink(codingExercise.getSolutionLink() != null ? codingExercise.getSolutionLink() : "");
                                        curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(14);
                                        curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                                        getCodingExercise.setCodingSolutionsVideo(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                                        getCodingExercises.add(getCodingExercise);
                                    }
                                    getSectionCurriculumItemResponse.setGetCodingExercises(getCodingExercises);

                                    PracticeTest practiceTest = practiceTestRepository.getPracticeTestBySectionCurriculumItem(item);
                                    List<GetPracticeTest> getPracticeTests = new ArrayList<>();
                                    if (practiceTest != null) {
                                        GetPracticeTest getPracticeTest = new GetPracticeTest();
                                        getPracticeTest.setPracticeTestCode(practiceTest.getPracticeTestCode());
                                        getPracticeTest.setDuration(practiceTest.getDuration());
                                        getPracticeTest.setMinimumuPassMark(practiceTest.getMinimumPassMark());
                                        getPracticeTest.setInstructions(practiceTest.getInstructions());
                                        getPracticeTest.setExternalLink(practiceTest.getExternalLink() != null ? practiceTest.getExternalLink() : "");
                                        CurriculumItemFileType curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(16);
                                        CurriculumItemFile curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                                        getPracticeTest.setPracticeTestQuestionSheet(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                                        getPracticeTest.setQuestionLink(practiceTest.getQuestionLink() != null ? practiceTest.getQuestionLink() : "");
                                        curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(17);
                                        curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                                        getPracticeTest.setPracticeTestSolutionSheet(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                                        getPracticeTest.setSolutionLink(practiceTest.getSolutionLink() != null ? practiceTest.getSolutionLink() : "");
                                        getPracticeTests.add(getPracticeTest);
                                    }
                                    getSectionCurriculumItemResponse.setGetPracticeTests(getPracticeTests);

                                    List<Quiz> quizs = quizRepository.getQuizBySectionCurriculumItem(item);
                                    List<GetQuiz> getQuizList = new ArrayList<>();
                                    for (Quiz quiz : quizs) {
                                        if (quiz.getIsDelete() == null || quiz.getIsDelete() == 0) {
                                            GetQuiz getQuiz = new GetQuiz();
                                            getQuiz.setId(Integer.toString(quiz.getId()));
                                            getQuiz.setQuestion(quiz.getQuestion());
                                            List<Answer> answers = answerRepository.getAnswerByQuiz(quiz);
                                            List<GetAnswer> getAnswerList = new ArrayList<>();
                                            for (Answer answerObj : answers) {
                                                GetAnswer answer = new GetAnswer();
                                                answer.setId(Integer.toString(answerObj.getId()));
                                                answer.setCorrectAnswer(answerObj.getCorrectAnswer());
                                                answer.setExplanation(answerObj.getExplanation());
                                                answer.setName(answerObj.getName());
                                                getAnswerList.add(answer);
                                            }
                                            getQuiz.setAnswers(getAnswerList);
                                            getQuizList.add(getQuiz);
                                        }
                                    }
                                    getSectionCurriculumItemResponse.setGetQuizs(getQuizList);
                                    getSectionCurriculumItemResponses.add(getSectionCurriculumItemResponse);
                                    List<GetCurriculumItemFilesResponse> getCurriculumItemFilesResponses = new ArrayList<>();

                                    List<CurriculumItemFile> curriculumItemFiles = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItem(item);
                                    for (CurriculumItemFile curriculumItemFile : curriculumItemFiles) {
                                        if (curriculumItemFile.getCurriculumItemFileTypes().getId() == 3) {
                                            video_count++;
                                        } else if (curriculumItemFile.getCurriculumItemFileTypes().getId() == 1) {
                                            downloadable_resources_count++;
                                        }
                                        GetCurriculumItemFilesResponse getCurriculumItemFilesResponse = new GetCurriculumItemFilesResponse();
                                        getCurriculumItemFilesResponse.setTitle(curriculumItemFile.getTitle());
                                        getCurriculumItemFilesResponse.setUrl(curriculumItemFile.getUrl());
                                        getCurriculumItemFilesResponse.setCurriculum_item_file_type(curriculumItemFile.getCurriculumItemFileTypes().getName());
                                        getCurriculumItemFilesResponses.add(getCurriculumItemFilesResponse);
                                    }
                                    getSectionCurriculumItemResponse.setGet_CurriculumItem_File(getCurriculumItemFilesResponses);
                                }
                                getCourseContentResponse.setNo_of_lectures(on_of_lectures);
                                getCourseContentResponse.setNo_of_qize(on_of_quize);
                                getCourseContentResponse.setOn_of_assignment(on_of_assignment);
                                getCourseContentResponse.setOn_of_codingExercise(on_of_codingExercise);
                                getCourseContentResponse.setOn_of_practiceTest(on_of_practiceTest);
                                getCourseContentResponse.setSection_curriculum_item(getSectionCurriculumItemResponses);
                                getCourseContentResponses.add(getCourseContentResponse);

                            }
                            getCoursesDataResponse.setArticles_count(article_count);
                            getCoursesDataResponse.setNo_of_videos(video_count);
                            getCoursesDataResponse.setDownloadable_resources_count(downloadable_resources_count);
                            getCoursesDataResponse.setEnrolled_count(0);
                            List<GetIntendedLearnerResponse> getIntendedLearnerResponses = new ArrayList<>();
                            List<CourseIntentedLearner> courseIntentedLearners = courseIntentedLearnerRepository.getCourseIntentedLearnerByCourse(courseLandingPage.getCourse());
                            for (CourseIntentedLearner courseIntentedLearner : courseIntentedLearners) {

                                GetIntendedLearnerResponse getIntendedLearnerResponse = new GetIntendedLearnerResponse();
                                getIntendedLearnerResponse.setIntended_learner(courseIntentedLearner.getName());
                                getIntendedLearnerResponse.setIntended_learner_type(courseIntentedLearner.getIntendedLearnerType().getName());
                                getIntendedLearnerResponses.add(getIntendedLearnerResponse);

                            }
                            getCoursesDataResponse.setIntended_learners(getIntendedLearnerResponses);
                            getCoursesDataResponse.setCourse_content(getCourseContentResponses);

                            responseLists.add(getCoursesDataResponse);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return responseLists;
            }
        }
    }

    @Override
    public List<GetCoursesDataResponse> searchNewCourses(String keyword) {

        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        Date oneMonthAgoDate = Date.from(oneMonthAgo.atZone(ZoneId.systemDefault()).toInstant());
        List<CourseLandingPage> courseLandingPageList = courseLandingPageRepository.findCourseLandingPagesByCourse_CourseTitleContainingIgnoreCaseOrSubTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndCourse_CreatedDateAfter(keyword, keyword, keyword, oneMonthAgoDate);
        if (courseLandingPageList.size() == 0) {
            throw new ErrorException("Empty results related to keyword", VarList.RSP_NO_DATA_FOUND);
        }
        List<GetCoursesDataResponse> responseLists = new ArrayList<>();
        for (CourseLandingPage courseLandingPage : courseLandingPageList) {
            Course course = courseLandingPage.getCourse();
            if (course.getApprovalType().getId() == 5) {
                GetCoursesDataResponse getCoursesDataResponse = new GetCoursesDataResponse();
                getCoursesDataResponse.setCourse_code(course.getCode());
                getCoursesDataResponse.setCreated_date(course.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter));
                getCoursesDataResponse.setId(course.getId());
                getCoursesDataResponse.setIsPaid(course.getIsPaid() == 2);
                getCoursesDataResponse.setImg(course.getImg());
                getCoursesDataResponse.setDuration(Double.toString(course.getCourseLength()));
                getCoursesDataResponse.setCourse_outline("OutLine is not added to the course");
                getCoursesDataResponse.setLevel((courseLandingPage != null) ? courseLandingPage.getCourseLevel().getName() : null);
                getCoursesDataResponse.setTitle(course.getCourseTitle());
                List<OrderHasCourse> orderHasCourses = orderHasCourseRepository.getOrderHasCoursesByCourse(course);
                List<GetRatingResponse> getRatingResponses = new ArrayList<>();
                int rating_count = 0;
                double rating_val = 0;

                int oneRatingCount = 0;
                int onePointFiveRatingCount = 0;
                int twoRatingCount = 0;
                int twoPointFiveRatingCount = 0;
                int threeRatingCount = 0;
                int threePointFiveRatingCount = 0;
                int fourRatingCount = 0;
                int fourPointFiveRatingCount = 0;
                int fiveRatingCount = 0;
                List<Review> reviews = reviewRepository.getReviewsByCourse(course);
                for (Review review : reviews) {
                    GetRatingResponse getRatingResponse = new GetRatingResponse();
                    getRatingResponse.setEmail(review.getGeneralUserProfile().getEmail());
                    getRatingResponse.setName(review.getGeneralUserProfile().getFirstName() + " " + review.getGeneralUserProfile().getLastName());
                    getRatingResponse.setRating(review.getRating());
                    getRatingResponse.setDate(review.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter));
                    getRatingResponse.setImg(review.getOrderHasCourse().getOrder().getGeneralUserProfile().getProfileImg());
                    getRatingResponse.setComment(review.getComment() == null ? "" : review.getComment());
                    getRatingResponses.add(getRatingResponse);
                    rating_count++;
                    rating_val = rating_val + review.getRating();

                    if (review.getRating() == 1) {
                        oneRatingCount++;
                    } else if (review.getRating() == 1.5) {
                        onePointFiveRatingCount++;
                    } else if (review.getRating() == 2) {
                        twoRatingCount++;
                    } else if (review.getRating() == 2.5) {
                        twoPointFiveRatingCount++;
                    } else if (review.getRating() == 3) {
                        threeRatingCount++;
                    } else if (review.getRating() == 3.5) {
                        threePointFiveRatingCount++;
                    } else if (review.getRating() == 4) {
                        fourRatingCount++;
                    } else if (review.getRating() == 4.5) {
                        fourPointFiveRatingCount++;
                    } else if (review.getRating() == 5) {
                        fiveRatingCount++;
                    }
                }
                getCoursesDataResponse.setReviews(getRatingResponses);
                getCoursesDataResponse.setRating_count(rating_count);

                StarsCountResponse starsCountResponse = new StarsCountResponse();
                starsCountResponse.setOneRatingCount(oneRatingCount);
                starsCountResponse.setOnePointFiveRatingCount(onePointFiveRatingCount);
                starsCountResponse.setTwoRatingCount(twoRatingCount);
                starsCountResponse.setTwoPointFiveRatingCount(twoPointFiveRatingCount);
                starsCountResponse.setThreeRatingCount(threeRatingCount);
                starsCountResponse.setThreePointFiveRatingCount(threePointFiveRatingCount);
                starsCountResponse.setFourRatingCount(fourRatingCount);
                starsCountResponse.setFourPointFiveRatingCount(fourPointFiveRatingCount);
                starsCountResponse.setFiveRatingCount(fiveRatingCount);
                getCoursesDataResponse.setRatingDetails(starsCountResponse);
                getCoursesDataResponse.setRating(rating_count == 0 ? 0 : rating_val / rating_count);


                List<GetCoursePricingResponse> responseList = new ArrayList<>();
                List<CoursePrice> coursePriceList = coursePriceRepository.getCoursePriceByCourse(course);
                GetCoursePricesResponse response = new GetCoursePricesResponse();
                if (coursePriceList.size() > 0) {

                    for (CoursePrice coursePrice : coursePriceList) {
                        if (coursePrice.getCountry().getId() == 30 && coursePrice.getCurrency().getId() == 30) {
                            response.setGlobalListPrice(coursePrice.getValue());
                            response.setDiscountTypeId(coursePrice.getDiscountType().getId());
                            response.setDiscountType(coursePrice.getDiscountType().getName());
                            response.setDiscountAmount(coursePrice.getDiscountValue());
                            try {
                                response.setDiscount(coursePrice.getDiscount());
                                response.setGlobalNetPrice(coursePrice.getNetPrice());
                            } catch (NullPointerException ex) {
                                response.setDiscount(0.0);
                                response.setGlobalNetPrice(0.0);
                            }

                        } else {
                            GetCoursePricingResponse getCoursePricingResponse = new GetCoursePricingResponse();
                            try {
                                getCoursePricingResponse.setNetPrice(coursePrice.getNetPrice());
                                getCoursePricingResponse.setDiscount(coursePrice.getDiscount());
                            } catch (NullPointerException ex) {
                                getCoursePricingResponse.setNetPrice(0.0);
                                getCoursePricingResponse.setDiscount(0.0);
                            }
                            getCoursePricingResponse.setListPrice(coursePrice.getValue());
                            getCoursePricingResponse.setDiscountTypeId(coursePrice.getDiscountType().getId());
                            getCoursePricingResponse.setDiscountType(coursePrice.getDiscountType().getName());
                            getCoursePricingResponse.setCountry(coursePrice.getCountry().getName());

                            getCoursePricingResponse.setDiscountAmount(coursePrice.getDiscountValue());
                            responseList.add(getCoursePricingResponse);
                            response.setPrices(responseList);
                        }
                    }
                }

                getCoursesDataResponse.setCourse_prices(response);

                List<CourseSection> courseSections = courseSectionRepository.getCourseSectionByCourse(course);
                getCoursesDataResponse.setLesson(courseSections.size());
                getCoursesDataResponse.setStudent(orderHasCourses.size());
                getCoursesDataResponse.setCategory(course.getCourseCategory().getName());
                getCoursesDataResponse.setCategory_link_name(course.getCourseCategory().getLinkName());
                getCoursesDataResponse.setSub_category(courseLandingPage.getSubcategory().getName());
                getCoursesDataResponse.setSub_category_link_name(courseLandingPage.getSubcategory().getSubLinkName());
                getCoursesDataResponse.setTopic(courseLandingPage.getTopic().getTopic());
                getCoursesDataResponse.setTopic_link_name(courseLandingPage.getTopic().getLinkName());
                getCoursesDataResponse.setShort_desc((courseLandingPage != null) ? courseLandingPage.getDescription() : null);
                GeneralUserProfile instructor = course.getInstructorId().getGeneralUserProfile();
                getCoursesDataResponse.setInstructor_code(instructor.getUserCode());
                getCoursesDataResponse.setInstructor(instructor.getFirstName() + " " + instructor.getLastName());
                getCoursesDataResponse.setInstructor_img(instructor.getProfileImg());
                getCoursesDataResponse.setInstructor_title(course.getInstructorId().getHeadline());
                getCoursesDataResponse.setInstructor_desc(course.getInstructorId().getBiography());
                getCoursesDataResponse.setFeatures(null);
                List<GetSocialLinksResponse> socialLinksResponsesList = new ArrayList<>();
                GetSocialLinksResponse socialLinks = new GetSocialLinksResponse();
                socialLinks.setLink(course.getInstructorId().getWebsite());
                socialLinks.setIcon("fa-solid fa-globe");
                socialLinks.setTarget("Web Site");
                socialLinksResponsesList.add(socialLinks);
                socialLinks = new GetSocialLinksResponse();
                socialLinks.setLink(course.getInstructorId().getYoutube());
                socialLinks.setIcon("fa-brands fa-youtube");
                socialLinks.setTarget("youtube");
                socialLinksResponsesList.add(socialLinks);
                socialLinks = new GetSocialLinksResponse();
                socialLinks.setLink(course.getInstructorId().getFacebook());
                socialLinks.setIcon("fa-brands fa-facebook");
                socialLinks.setTarget("faceBook");
                socialLinksResponsesList.add(socialLinks);
                socialLinks = new GetSocialLinksResponse();
                socialLinks.setLink(course.getInstructorId().getTwitter());
                socialLinks.setIcon("fa-brands fa-twitter");
                socialLinks.setTarget("twitter");
                socialLinksResponsesList.add(socialLinks);
                socialLinks = new GetSocialLinksResponse();
                socialLinks.setLink(course.getInstructorId().getLinkedin());
                socialLinks.setIcon("fa-brands fa-linkedin");
                socialLinks.setTarget("linkdin");
                socialLinksResponsesList.add(socialLinks);
                getCoursesDataResponse.setSocial_links(socialLinksResponsesList);
                getCoursesDataResponse.setLanguage((courseLandingPage != null) ? courseLandingPage.getLanguage().getName() : null);
                getCoursesDataResponse.setSub_title((courseLandingPage != null) ? courseLandingPage.getSubTitle() : null);
                getCoursesDataResponse.setCertificate("yes");
                getCoursesDataResponse.setVideoId(course.getTest_video());
                getCoursesDataResponse.setCourse_main_desc((courseLandingPage != null) ? courseLandingPage.getDescription() : null);
                getCoursesDataResponse.setCourse_desc_2((courseLandingPage != null) ? courseLandingPage.getDescription() : null);
                getCoursesDataResponse.setLearnList(null);
                getCoursesDataResponse.setCourse_desc_3((courseLandingPage != null) ? courseLandingPage.getDescription() : null);
                getCoursesDataResponse.setCurriculum_desc("Curriculum Description is not added to the course");


                int article_count = 0;
                int video_count = 0;
                int downloadable_resources_count = 0;

                List<GetCourseContentResponse> getCourseContentResponses = new ArrayList<>();
                for (CourseSection courseSectionObj : courseSections) {
                    GetCourseContentResponse getCourseContentResponse = new GetCourseContentResponse();
                    getCourseContentResponse.setSection_id(courseSectionObj.getId());
                    getCourseContentResponse.setSection_name(courseSectionObj.getSectionName());
                    int on_of_lectures = 0;
                    int on_of_quize = 0;
                    int on_of_assignment = 0;
                    int on_of_codingExercise = 0;
                    int on_of_practiceTest = 0;
                    List<SectionCurriculumItem> articleCounts = sectionCurriculumItemRepository.getSectionCurriculumItemsByCourseSection(courseSectionObj);
                    List<GetSectionCurriculumItemResponse> getSectionCurriculumItemResponses = new ArrayList<>();
                    for (SectionCurriculumItem item : articleCounts) {
                        if (!item.getArticle().equals("N/A")) {
                            article_count++;
                        }
                        if (item.getCurriculumItemType().getId() == 1) {
                            on_of_lectures++;
                        } else if (item.getCurriculumItemType().getId() == 2) {
                            on_of_quize++;
                        } else if (item.getCurriculumItemType().getId() == 3) {
                            on_of_assignment++;
                        } else if (item.getCurriculumItemType().getId() == 4) {
                            on_of_codingExercise++;
                        } else if (item.getCurriculumItemType().getId() == 5) {
                            on_of_practiceTest++;
                        }

                        GetSectionCurriculumItemResponse getSectionCurriculumItemResponse = new GetSectionCurriculumItemResponse();
                        getSectionCurriculumItemResponse.setTitle(item.getTitle());
                        getSectionCurriculumItemResponse.setArticle(item.getArticle());
                        getSectionCurriculumItemResponse.setDescription(item.getDescription());
                        getSectionCurriculumItemResponse.setCurriculum_item_type(item.getCurriculumItemType().getName());
                        getSectionCurriculumItemResponse.setCurriculumItemId(item.getId());
                        getSectionCurriculumItemResponse.setArrangeNo(item.getArrangedNo() == null ? "" : item.getArrangedNo().toString());

                        Assignment assignment = assignmentRepository.getAssignmentBySectionCurriculumItem(item);
                        List<GetAssignment> getAssignments = new ArrayList<>();
                        if (assignment != null) {
                            GetAssignment getAssignment = new GetAssignment();
                            getAssignment.setAssignmentCode(assignment.getAssignmentCode());
                            getAssignment.setDuration(assignment.getDuration());
                            getAssignment.setInstructions(assignment.getInstructions());
                            CurriculumItemFileType curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(5);
                            CurriculumItemFile curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                            getAssignment.setAssignmentVideo(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                            curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(6);
                            curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                            getAssignment.setDownloadableResource(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                            getAssignment.setExternalLink(assignment.getExternalLink() != null ? assignment.getExternalLink() : "");
                            getAssignment.setQuestion(assignment.getQuestions() != null ? assignment.getQuestions() : "");
                            curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(7);
                            curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                            getAssignment.setQuestionSheet(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                            getAssignment.setQuestionExternalLink(assignment.getQuestionsExternalLink() != null ? assignment.getQuestionsExternalLink() : "");
                            getAssignment.setSolutions(assignment.getSolutions() != null ? assignment.getSolutions() : "");
                            curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(8);
                            curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                            getAssignment.setSolutionVideo(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                            curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(9);
                            curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                            getAssignment.setSolutionsSheet(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                            getAssignment.setSolutionsExternalLink(assignment.getSolutionsExternalLink() != null ? assignment.getSolutionsExternalLink() : "");
                            getAssignments.add(getAssignment);
                        }
                        getSectionCurriculumItemResponse.setGetAssignments(getAssignments);
                        CodingExercise codingExercise = codingExerciseRepository.getCodingExerciseBySectionCurriculumItem(item);
                        List<GetCodingExercise> getCodingExercises = new ArrayList<>();
                        if (codingExercise != null) {
                            GetCodingExercise getCodingExercise = new GetCodingExercise();
                            getCodingExercise.setCodingExerciseCode(codingExercise.getCodingExerciseCode());
                            getCodingExercise.setInstructions(codingExercise.getInstructions());
                            CurriculumItemFileType curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(10);
                            CurriculumItemFile curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                            getCodingExercise.setCodingVideo(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                            curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(11);
                            curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                            getCodingExercise.setDownloadableResource(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                            getCodingExercise.setCodingExternalLink(codingExercise.getExternalLink() != null ? codingExercise.getExternalLink() : "");
                            curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(12);
                            curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                            getCodingExercise.setCodingExerciseSheet(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                            getCodingExercise.setCodingExternalLink(codingExercise.getCodingLink() != null ? codingExercise.getCodingLink() : "");
                            curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(13);
                            curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                            getCodingExercise.setCodingExerciseVideo(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                            curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(15);
                            curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                            getCodingExercise.setCodingSolutionsSheet(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                            getCodingExercise.setSolutionsExternalLink(codingExercise.getSolutionLink() != null ? codingExercise.getSolutionLink() : "");
                            curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(14);
                            curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                            getCodingExercise.setCodingSolutionsVideo(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                            getCodingExercises.add(getCodingExercise);
                        }
                        getSectionCurriculumItemResponse.setGetCodingExercises(getCodingExercises);

                        PracticeTest practiceTest = practiceTestRepository.getPracticeTestBySectionCurriculumItem(item);
                        List<GetPracticeTest> getPracticeTests = new ArrayList<>();
                        if (practiceTest != null) {
                            GetPracticeTest getPracticeTest = new GetPracticeTest();
                            getPracticeTest.setPracticeTestCode(practiceTest.getPracticeTestCode());
                            getPracticeTest.setDuration(practiceTest.getDuration());
                            getPracticeTest.setMinimumuPassMark(practiceTest.getMinimumPassMark());
                            getPracticeTest.setInstructions(practiceTest.getInstructions());
                            getPracticeTest.setExternalLink(practiceTest.getExternalLink() != null ? practiceTest.getExternalLink() : "");
                            CurriculumItemFileType curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(16);
                            CurriculumItemFile curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                            getPracticeTest.setPracticeTestQuestionSheet(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                            getPracticeTest.setQuestionLink(practiceTest.getQuestionLink() != null ? practiceTest.getQuestionLink() : "");
                            curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(17);
                            curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                            getPracticeTest.setPracticeTestSolutionSheet(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                            getPracticeTest.setSolutionLink(practiceTest.getSolutionLink() != null ? practiceTest.getSolutionLink() : "");
                            getPracticeTests.add(getPracticeTest);
                        }
                        getSectionCurriculumItemResponse.setGetPracticeTests(getPracticeTests);

                        List<Quiz> quizs = quizRepository.getQuizBySectionCurriculumItem(item);
                        List<GetQuiz> getQuizList = new ArrayList<>();
                        for (Quiz quiz : quizs) {
                            if (quiz.getIsDelete() == null || quiz.getIsDelete() == 0) {
                                GetQuiz getQuiz = new GetQuiz();
                                getQuiz.setId(Integer.toString(quiz.getId()));
                                getQuiz.setQuestion(quiz.getQuestion());
                                List<Answer> answers = answerRepository.getAnswerByQuiz(quiz);
                                List<GetAnswer> getAnswerList = new ArrayList<>();
                                for (Answer answerObj : answers) {
                                    GetAnswer answer = new GetAnswer();
                                    answer.setId(Integer.toString(answerObj.getId()));
                                    answer.setCorrectAnswer(answerObj.getCorrectAnswer());
                                    answer.setExplanation(answerObj.getExplanation());
                                    answer.setName(answerObj.getName());
                                    getAnswerList.add(answer);
                                }
                                getQuiz.setAnswers(getAnswerList);
                                getQuizList.add(getQuiz);
                            }
                        }
                        getSectionCurriculumItemResponse.setGetQuizs(getQuizList);

                        getSectionCurriculumItemResponses.add(getSectionCurriculumItemResponse);
                        List<GetCurriculumItemFilesResponse> getCurriculumItemFilesResponses = new ArrayList<>();

                        List<CurriculumItemFile> curriculumItemFiles = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItem(item);
                        for (CurriculumItemFile curriculumItemFile : curriculumItemFiles) {
                            if (curriculumItemFile.getCurriculumItemFileTypes().getId() == 3) {
                                video_count++;
                            } else if (curriculumItemFile.getCurriculumItemFileTypes().getId() == 1) {
                                downloadable_resources_count++;
                            }
                            GetCurriculumItemFilesResponse getCurriculumItemFilesResponse = new GetCurriculumItemFilesResponse();
                            getCurriculumItemFilesResponse.setTitle(curriculumItemFile.getTitle());
                            getCurriculumItemFilesResponse.setUrl(curriculumItemFile.getUrl());
                            getCurriculumItemFilesResponse.setCurriculum_item_file_type(curriculumItemFile.getCurriculumItemFileTypes().getName());
                            getCurriculumItemFilesResponses.add(getCurriculumItemFilesResponse);
                        }
                        getSectionCurriculumItemResponse.setGet_CurriculumItem_File(getCurriculumItemFilesResponses);
                    }
                    getCourseContentResponse.setNo_of_lectures(on_of_lectures);
                    getCourseContentResponse.setNo_of_qize(on_of_quize);
                    getCourseContentResponse.setOn_of_assignment(on_of_assignment);
                    getCourseContentResponse.setOn_of_codingExercise(on_of_codingExercise);
                    getCourseContentResponse.setOn_of_practiceTest(on_of_practiceTest);
                    getCourseContentResponse.setSection_curriculum_item(getSectionCurriculumItemResponses);
                    getCourseContentResponses.add(getCourseContentResponse);

                }
                getCoursesDataResponse.setArticles_count(article_count);
                getCoursesDataResponse.setNo_of_videos(video_count);
                getCoursesDataResponse.setDownloadable_resources_count(downloadable_resources_count);
                getCoursesDataResponse.setEnrolled_count(0);
                List<GetIntendedLearnerResponse> getIntendedLearnerResponses = new ArrayList<>();
                List<CourseIntentedLearner> courseIntentedLearners = courseIntentedLearnerRepository.getCourseIntentedLearnerByCourse(course);
                for (CourseIntentedLearner courseIntentedLearner : courseIntentedLearners) {

                    GetIntendedLearnerResponse getIntendedLearnerResponse = new GetIntendedLearnerResponse();
                    getIntendedLearnerResponse.setIntended_learner(courseIntentedLearner.getName());
                    getIntendedLearnerResponse.setIntended_learner_type(courseIntentedLearner.getIntendedLearnerType().getName());
                    getIntendedLearnerResponses.add(getIntendedLearnerResponse);

                }
                getCoursesDataResponse.setIntended_learners(getIntendedLearnerResponses);
                getCoursesDataResponse.setCourse_content(getCourseContentResponses);

                responseLists.add(getCoursesDataResponse);
            }
        }
        return responseLists;
    }

    @Override

    public List<GetCategoryByTopicResponse> getRelatedCategoriesByTopicLinkName(String topicLinkName) {
        List<Topic> topics = topicRepository.getTopicsByLinkName(topicLinkName);
        if (topics.size() != 0) {

            List<GetCategoryByTopicResponse> getCategoryByTopicResponses = new ArrayList<>();

            for (Topic topic : topics) {
                GetCategoryByTopicResponse getCategoryByTopicResponse = new GetCategoryByTopicResponse();
                CourseCategory courseCategory = courseCategoryRepository.getCourseCategoryById(topic.getSubCategory().getCourseCategory().getId());
                if (courseCategory != null) {
                    getCategoryByTopicResponse.setCategory(courseCategory.getName());
                    getCategoryByTopicResponse.setCategoryLinkName(courseCategory.getLinkName());
                    getCategoryByTopicResponses.add(getCategoryByTopicResponse);
                } else {
                    throw new ErrorException("Course category not found", VarList.RSP_NO_DATA_FOUND);
                }
            }
            return getCategoryByTopicResponses;
        } else {
            throw new ErrorException("Topics not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public List<GetCoursesDataResponse> GetBeginnerFavoritesCoursesByTopicLinkName(String topicLinkName) {
        Topic topic = topicRepository.getTopicByLinkName(topicLinkName);
        if (topic == null) {
            throw new ErrorException("Invalid topic link name", VarList.RSP_NO_DATA_FOUND);
        }
        CourseLevel courseLevel = courseLevelRepository.getCourseLevelById(1);
        if (courseLevel == null) {
            throw new ErrorException("Course level not available", VarList.RSP_NO_DATA_FOUND);
        }
        List<CourseLandingPage> courseLandingPageList = courseLandingPageRepository.getCourseLandingPagesByCourseLevelAndTopic(courseLevel, topic);
        if (courseLandingPageList.size() == 0) {
            throw new ErrorException("Empty results related to keyword", VarList.RSP_NO_DATA_FOUND);
        }
        List<GetCoursesDataResponse> responseLists = new ArrayList<>();
        for (CourseLandingPage courseLandingPage : courseLandingPageList) {
            Course course = courseLandingPage.getCourse();
            if (course.getApprovalType().getId() == 5) {
                GetCoursesDataResponse getCoursesDataResponse = new GetCoursesDataResponse();
                getCoursesDataResponse.setCourse_code(course.getCode());
                getCoursesDataResponse.setCreated_date(course.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter));
                getCoursesDataResponse.setId(course.getId());
                getCoursesDataResponse.setIsPaid(course.getIsPaid() == 2);
                getCoursesDataResponse.setImg(course.getImg());
                getCoursesDataResponse.setDuration(Double.toString(course.getCourseLength()));
                getCoursesDataResponse.setCourse_outline("OutLine is not added to the course");

                ExternalCourse externalCourse = externalCourseRepository.getExternalCourseByCourse(course);
                if (externalCourse != null) {
                    ExternalCourseResponse externalCourseResponse = new ExternalCourseResponse();
                    externalCourseResponse.setExternalNumberOfStudent(externalCourse.getExternalNumberOfStudents());
                    externalCourseResponse.setExternalRating(externalCourse.getExternalRating());
                    externalCourseResponse.setAnyComments((externalCourse.getAnyComment() == null || externalCourse.getAnyComment().isEmpty()) ? "" : externalCourseResponse.getAnyComments());
                    externalCourseResponse.setLinkToCourse((externalCourse.getLinkToCourse() == null || externalCourse.getLinkToCourse().isEmpty()) ? "" : externalCourse.getLinkToCourse());
                    getCoursesDataResponse.setExternalCourseDetails(externalCourseResponse);
                }

                getCoursesDataResponse.setLevel((courseLandingPage != null) ? courseLandingPage.getCourseLevel().getName() : null);
                getCoursesDataResponse.setTitle(course.getCourseTitle());
                List<OrderHasCourse> orderHasCourses = orderHasCourseRepository.getOrderHasCoursesByCourse(course);
                List<GetRatingResponse> getRatingResponses = new ArrayList<>();
                int rating_count = 0;
                double rating_val = 0;
                int oneRatingCount = 0;
                int onePointFiveRatingCount = 0;
                int twoRatingCount = 0;
                int twoPointFiveRatingCount = 0;
                int threeRatingCount = 0;
                int threePointFiveRatingCount = 0;
                int fourRatingCount = 0;
                int fourPointFiveRatingCount = 0;
                int fiveRatingCount = 0;
                List<Review> reviews = reviewRepository.getReviewsByCourse(course);
                for (Review review : reviews) {
                    GetRatingResponse getRatingResponse = new GetRatingResponse();
                    getRatingResponse.setEmail(review.getGeneralUserProfile().getEmail());
                    getRatingResponse.setName(review.getGeneralUserProfile().getFirstName() + " " + review.getGeneralUserProfile().getLastName());
                    getRatingResponse.setRating(review.getRating());
                    getRatingResponse.setDate(review.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter));
                    getRatingResponse.setImg(review.getOrderHasCourse().getOrder().getGeneralUserProfile().getProfileImg());
                    getRatingResponse.setComment(review.getComment() == null ? "" : review.getComment());
                    getRatingResponses.add(getRatingResponse);
                    rating_count++;
                    rating_val = rating_val + review.getRating();

                    if (review.getRating() == 1) {
                        oneRatingCount++;
                    } else if (review.getRating() == 1.5) {
                        onePointFiveRatingCount++;
                    } else if (review.getRating() == 2) {
                        twoRatingCount++;
                    } else if (review.getRating() == 2.5) {
                        twoPointFiveRatingCount++;
                    } else if (review.getRating() == 3) {
                        threeRatingCount++;
                    } else if (review.getRating() == 3.5) {
                        threePointFiveRatingCount++;
                    } else if (review.getRating() == 4) {
                        fourRatingCount++;
                    } else if (review.getRating() == 4.5) {
                        fourPointFiveRatingCount++;
                    } else if (review.getRating() == 5) {
                        fiveRatingCount++;
                    }
                }

                getCoursesDataResponse.setReviews(getRatingResponses);
                getCoursesDataResponse.setRating_count(rating_count);
                StarsCountResponse starsCountResponse = new StarsCountResponse();
                starsCountResponse.setOneRatingCount(oneRatingCount);
                starsCountResponse.setOnePointFiveRatingCount(onePointFiveRatingCount);
                starsCountResponse.setTwoRatingCount(twoRatingCount);
                starsCountResponse.setTwoPointFiveRatingCount(twoPointFiveRatingCount);
                starsCountResponse.setThreeRatingCount(threeRatingCount);
                starsCountResponse.setThreePointFiveRatingCount(threePointFiveRatingCount);
                starsCountResponse.setFourRatingCount(fourRatingCount);
                starsCountResponse.setFourPointFiveRatingCount(fourPointFiveRatingCount);
                starsCountResponse.setFiveRatingCount(fiveRatingCount);
                getCoursesDataResponse.setRatingDetails(starsCountResponse);
                getCoursesDataResponse.setRating(rating_count == 0 ? 0 : rating_val / rating_count);


                List<GetCoursePricingResponse> responseList = new ArrayList<>();
                List<CoursePrice> coursePriceList = coursePriceRepository.getCoursePriceByCourse(course);
                GetCoursePricesResponse response = new GetCoursePricesResponse();
                if (coursePriceList.size() > 0) {

                    for (CoursePrice coursePrice : coursePriceList) {
                        if (coursePrice.getCountry().getId() == 30 && coursePrice.getCurrency().getId() == 30) {
                            response.setGlobalListPrice(coursePrice.getValue());
                            response.setDiscountTypeId(coursePrice.getDiscountType().getId());
                            response.setDiscountType(coursePrice.getDiscountType().getName());
                            response.setDiscountAmount(coursePrice.getDiscountValue());
                            try {
                                response.setDiscount(coursePrice.getDiscount());
                                response.setGlobalNetPrice(coursePrice.getNetPrice());
                            } catch (NullPointerException ex) {
                                response.setDiscount(0.0);
                                response.setGlobalNetPrice(0.0);
                            }
                        } else if (coursePrice.getCountry().getId() == 8) {
                            List<EuroCountry> euroCountries = euroCountryRepository.findAll();
                            for (EuroCountry euroCountry : euroCountries) {
                                GetCoursePricingResponse getCoursePricingResponse = new GetCoursePricingResponse();
                                try {
                                    getCoursePricingResponse.setNetPrice(coursePrice.getNetPrice());
                                    getCoursePricingResponse.setDiscount(coursePrice.getDiscount());
                                } catch (NullPointerException ex) {
                                    getCoursePricingResponse.setNetPrice(0.0);
                                    getCoursePricingResponse.setDiscount(0.0);
                                }
                                getCoursePricingResponse.setListPrice(coursePrice.getValue());
                                getCoursePricingResponse.setDiscountTypeId(coursePrice.getDiscountType().getId());
                                getCoursePricingResponse.setDiscountType(coursePrice.getDiscountType().getName());
                                getCoursePricingResponse.setCountry(euroCountry.getName());

                                getCoursePricingResponse.setDiscountAmount(coursePrice.getDiscountValue());
                                responseList.add(getCoursePricingResponse);
                                response.setPrices(responseList);
                            }
                        } else {
                            GetCoursePricingResponse getCoursePricingResponse = new GetCoursePricingResponse();
                            try {
                                getCoursePricingResponse.setNetPrice(coursePrice.getNetPrice());
                                getCoursePricingResponse.setDiscount(coursePrice.getDiscount());
                            } catch (NullPointerException ex) {
                                getCoursePricingResponse.setNetPrice(0.0);
                                getCoursePricingResponse.setDiscount(0.0);
                            }
                            getCoursePricingResponse.setListPrice(coursePrice.getValue());
                            getCoursePricingResponse.setDiscountTypeId(coursePrice.getDiscountType().getId());
                            getCoursePricingResponse.setDiscountType(coursePrice.getDiscountType().getName());
                            getCoursePricingResponse.setCountry(coursePrice.getCountry().getName());

                            getCoursePricingResponse.setDiscountAmount(coursePrice.getDiscountValue());
                            responseList.add(getCoursePricingResponse);
                            response.setPrices(responseList);
                        }
                    }
                }

                getCoursesDataResponse.setCourse_prices(response);

                List<CourseSection> courseSections = courseSectionRepository.getCourseSectionByCourse(course);
                getCoursesDataResponse.setLesson(courseSections.size());
                getCoursesDataResponse.setStudent(orderHasCourses.size());
                getCoursesDataResponse.setCategory(course.getCourseCategory().getName());
                getCoursesDataResponse.setCategory_link_name(course.getCourseCategory().getLinkName());
                getCoursesDataResponse.setSub_category(courseLandingPage.getSubcategory().getName());
                getCoursesDataResponse.setSub_category_link_name(courseLandingPage.getSubcategory().getSubLinkName());
                getCoursesDataResponse.setTopic(courseLandingPage.getTopic().getTopic());
                getCoursesDataResponse.setTopic_link_name(courseLandingPage.getTopic().getLinkName());
                getCoursesDataResponse.setShort_desc((courseLandingPage != null) ? courseLandingPage.getDescription() : null);
                GeneralUserProfile instructor = course.getInstructorId().getGeneralUserProfile();
                getCoursesDataResponse.setInstructor_code(instructor.getUserCode());
                getCoursesDataResponse.setInstructor(instructor.getFirstName() + " " + instructor.getLastName());
                getCoursesDataResponse.setInstructor_img(instructor.getProfileImg());
                getCoursesDataResponse.setInstructor_title(course.getInstructorId().getHeadline());
                getCoursesDataResponse.setInstructor_desc(course.getInstructorId().getBiography());
                getCoursesDataResponse.setFeatures(null);
                List<GetSocialLinksResponse> socialLinksResponsesList = new ArrayList<>();
                GetSocialLinksResponse socialLinks = new GetSocialLinksResponse();
                socialLinks.setLink(course.getInstructorId().getWebsite());
                socialLinks.setIcon("fa-solid fa-globe");
                socialLinks.setTarget("Web Site");
                socialLinksResponsesList.add(socialLinks);
                socialLinks = new GetSocialLinksResponse();
                socialLinks.setLink(course.getInstructorId().getYoutube());
                socialLinks.setIcon("fa-brands fa-youtube");
                socialLinks.setTarget("youtube");
                socialLinksResponsesList.add(socialLinks);
                socialLinks = new GetSocialLinksResponse();
                socialLinks.setLink(course.getInstructorId().getFacebook());
                socialLinks.setIcon("fa-brands fa-facebook");
                socialLinks.setTarget("faceBook");
                socialLinksResponsesList.add(socialLinks);
                socialLinks = new GetSocialLinksResponse();
                socialLinks.setLink(course.getInstructorId().getTwitter());
                socialLinks.setIcon("fa-brands fa-twitter");
                socialLinks.setTarget("twitter");
                socialLinksResponsesList.add(socialLinks);
                socialLinks = new GetSocialLinksResponse();
                socialLinks.setLink(course.getInstructorId().getLinkedin());
                socialLinks.setIcon("fa-brands fa-linkedin");
                socialLinks.setTarget("linkdin");
                socialLinksResponsesList.add(socialLinks);
                getCoursesDataResponse.setSocial_links(socialLinksResponsesList);
                getCoursesDataResponse.setLanguage((courseLandingPage != null) ? courseLandingPage.getLanguage().getName() : null);
                getCoursesDataResponse.setSub_title((courseLandingPage != null) ? courseLandingPage.getSubTitle() : null);
                getCoursesDataResponse.setCertificate("yes");
                getCoursesDataResponse.setVideoId(course.getTest_video());
                getCoursesDataResponse.setCourse_main_desc((courseLandingPage != null) ? courseLandingPage.getDescription() : null);
                getCoursesDataResponse.setCourse_desc_2((courseLandingPage != null) ? courseLandingPage.getDescription() : null);
                getCoursesDataResponse.setLearnList(null);
                getCoursesDataResponse.setCourse_desc_3((courseLandingPage != null) ? courseLandingPage.getDescription() : null);
                getCoursesDataResponse.setCurriculum_desc("Curriculum Description is not added to the course");


                int article_count = 0;
                int video_count = 0;
                int downloadable_resources_count = 0;

                List<GetCourseContentResponse> getCourseContentResponses = new ArrayList<>();
                for (CourseSection courseSectionObj : courseSections) {
                    GetCourseContentResponse getCourseContentResponse = new GetCourseContentResponse();
                    getCourseContentResponse.setSection_id(courseSectionObj.getId());
                    getCourseContentResponse.setSection_name(courseSectionObj.getSectionName());
                    int on_of_lectures = 0;
                    int on_of_quize = 0;
                    int on_of_assignment = 0;
                    int on_of_codingExercise = 0;
                    int on_of_practiceTest = 0;
                    List<SectionCurriculumItem> articleCounts = sectionCurriculumItemRepository.getSectionCurriculumItemsByCourseSection(courseSectionObj);
                    List<GetSectionCurriculumItemResponse> getSectionCurriculumItemResponses = new ArrayList<>();
                    for (SectionCurriculumItem item : articleCounts) {
                        if (!item.getArticle().equals("N/A")) {
                            article_count++;
                        }
                        if (item.getCurriculumItemType().getId() == 1) {
                            on_of_lectures++;
                        } else if (item.getCurriculumItemType().getId() == 2) {
                            on_of_quize++;
                        } else if (item.getCurriculumItemType().getId() == 3) {
                            on_of_assignment++;
                        } else if (item.getCurriculumItemType().getId() == 4) {
                            on_of_codingExercise++;
                        } else if (item.getCurriculumItemType().getId() == 5) {
                            on_of_practiceTest++;
                        }

                        GetSectionCurriculumItemResponse getSectionCurriculumItemResponse = new GetSectionCurriculumItemResponse();
                        getSectionCurriculumItemResponse.setTitle(item.getTitle());
                        getSectionCurriculumItemResponse.setArticle(item.getArticle());
                        getSectionCurriculumItemResponse.setDescription(item.getDescription());
                        getSectionCurriculumItemResponse.setCurriculum_item_type(item.getCurriculumItemType().getName());
                        getSectionCurriculumItemResponse.setCurriculumItemId(item.getId());
                        getSectionCurriculumItemResponse.setArrangeNo(item.getArrangedNo() == null ? "" : item.getArrangedNo().toString());
                        Assignment assignment = assignmentRepository.getAssignmentBySectionCurriculumItem(item);
                        List<GetAssignment> getAssignments = new ArrayList<>();
                        if (assignment != null) {
                            GetAssignment getAssignment = new GetAssignment();
                            getAssignment.setAssignmentCode(assignment.getAssignmentCode());
                            getAssignment.setDuration(assignment.getDuration());
                            getAssignment.setInstructions(assignment.getInstructions());
                            CurriculumItemFileType curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(5);
                            CurriculumItemFile curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                            getAssignment.setAssignmentVideo(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                            curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(6);
                            curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                            getAssignment.setDownloadableResource(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                            getAssignment.setExternalLink(assignment.getExternalLink() != null ? assignment.getExternalLink() : "");
                            getAssignment.setQuestion(assignment.getQuestions() != null ? assignment.getQuestions() : "");
                            curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(7);
                            curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                            getAssignment.setQuestionSheet(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                            getAssignment.setQuestionExternalLink(assignment.getQuestionsExternalLink() != null ? assignment.getQuestionsExternalLink() : "");
                            getAssignment.setSolutions(assignment.getSolutions() != null ? assignment.getSolutions() : "");
                            curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(8);
                            curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                            getAssignment.setSolutionVideo(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                            curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(9);
                            curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                            getAssignment.setSolutionsSheet(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                            getAssignment.setSolutionsExternalLink(assignment.getSolutionsExternalLink() != null ? assignment.getSolutionsExternalLink() : "");
                            getAssignments.add(getAssignment);
                        }
                        getSectionCurriculumItemResponse.setGetAssignments(getAssignments);
                        CodingExercise codingExercise = codingExerciseRepository.getCodingExerciseBySectionCurriculumItem(item);
                        List<GetCodingExercise> getCodingExercises = new ArrayList<>();
                        if (codingExercise != null) {
                            GetCodingExercise getCodingExercise = new GetCodingExercise();
                            getCodingExercise.setCodingExerciseCode(codingExercise.getCodingExerciseCode());
                            getCodingExercise.setInstructions(codingExercise.getInstructions());
                            CurriculumItemFileType curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(10);
                            CurriculumItemFile curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                            getCodingExercise.setCodingVideo(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                            curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(11);
                            curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                            getCodingExercise.setDownloadableResource(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                            getCodingExercise.setCodingExternalLink(codingExercise.getExternalLink() != null ? codingExercise.getExternalLink() : "");
                            curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(12);
                            curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                            getCodingExercise.setCodingExerciseSheet(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                            getCodingExercise.setCodingExternalLink(codingExercise.getCodingLink() != null ? codingExercise.getCodingLink() : "");
                            curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(13);
                            curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                            getCodingExercise.setCodingExerciseVideo(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                            curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(15);
                            curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                            getCodingExercise.setCodingSolutionsSheet(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                            getCodingExercise.setSolutionsExternalLink(codingExercise.getSolutionLink() != null ? codingExercise.getSolutionLink() : "");
                            curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(14);
                            curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                            getCodingExercise.setCodingSolutionsVideo(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                            getCodingExercises.add(getCodingExercise);
                        }
                        getSectionCurriculumItemResponse.setGetCodingExercises(getCodingExercises);

                        PracticeTest practiceTest = practiceTestRepository.getPracticeTestBySectionCurriculumItem(item);
                        List<GetPracticeTest> getPracticeTests = new ArrayList<>();
                        if (practiceTest != null) {
                            GetPracticeTest getPracticeTest = new GetPracticeTest();
                            getPracticeTest.setPracticeTestCode(practiceTest.getPracticeTestCode());
                            getPracticeTest.setDuration(practiceTest.getDuration());
                            getPracticeTest.setMinimumuPassMark(practiceTest.getMinimumPassMark());
                            getPracticeTest.setInstructions(practiceTest.getInstructions());
                            getPracticeTest.setExternalLink(practiceTest.getExternalLink() != null ? practiceTest.getExternalLink() : "");
                            CurriculumItemFileType curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(16);
                            CurriculumItemFile curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                            getPracticeTest.setPracticeTestQuestionSheet(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                            getPracticeTest.setQuestionLink(practiceTest.getQuestionLink() != null ? practiceTest.getQuestionLink() : "");
                            curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(17);
                            curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                            getPracticeTest.setPracticeTestSolutionSheet(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                            getPracticeTest.setSolutionLink(practiceTest.getSolutionLink() != null ? practiceTest.getSolutionLink() : "");
                            getPracticeTests.add(getPracticeTest);
                        }
                        getSectionCurriculumItemResponse.setGetPracticeTests(getPracticeTests);

                        List<Quiz> quizs = quizRepository.getQuizBySectionCurriculumItem(item);
                        List<GetQuiz> getQuizList = new ArrayList<>();
                        for (Quiz quiz : quizs) {
                            if (quiz.getIsDelete() == null || quiz.getIsDelete() == 0) {
                                GetQuiz getQuiz = new GetQuiz();
                                getQuiz.setId(Integer.toString(quiz.getId()));
                                getQuiz.setQuestion(quiz.getQuestion());
                                List<Answer> answers = answerRepository.getAnswerByQuiz(quiz);
                                List<GetAnswer> getAnswerList = new ArrayList<>();
                                for (Answer answerObj : answers) {
                                    GetAnswer answer = new GetAnswer();
                                    answer.setId(Integer.toString(answerObj.getId()));
                                    answer.setCorrectAnswer(answerObj.getCorrectAnswer());
                                    answer.setExplanation(answerObj.getExplanation());
                                    answer.setName(answerObj.getName());
                                    getAnswerList.add(answer);
                                }
                                getQuiz.setAnswers(getAnswerList);
                                getQuizList.add(getQuiz);
                            }
                        }
                        getSectionCurriculumItemResponse.setGetQuizs(getQuizList);
                        getSectionCurriculumItemResponses.add(getSectionCurriculumItemResponse);
                        List<GetCurriculumItemFilesResponse> getCurriculumItemFilesResponses = new ArrayList<>();

                        List<CurriculumItemFile> curriculumItemFiles = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItem(item);
                        for (CurriculumItemFile curriculumItemFile : curriculumItemFiles) {
                            if (curriculumItemFile.getCurriculumItemFileTypes().getId() == 3) {
                                video_count++;
                            } else if (curriculumItemFile.getCurriculumItemFileTypes().getId() == 1) {
                                downloadable_resources_count++;
                            }
                            GetCurriculumItemFilesResponse getCurriculumItemFilesResponse = new GetCurriculumItemFilesResponse();
                            getCurriculumItemFilesResponse.setTitle(curriculumItemFile.getTitle());
                            getCurriculumItemFilesResponse.setUrl(curriculumItemFile.getUrl());
                            getCurriculumItemFilesResponse.setCurriculum_item_file_type(curriculumItemFile.getCurriculumItemFileTypes().getName());
                            getCurriculumItemFilesResponses.add(getCurriculumItemFilesResponse);
                        }
                        getSectionCurriculumItemResponse.setGet_CurriculumItem_File(getCurriculumItemFilesResponses);
                    }
                    getCourseContentResponse.setNo_of_lectures(on_of_lectures);
                    getCourseContentResponse.setNo_of_qize(on_of_quize);
                    getCourseContentResponse.setOn_of_assignment(on_of_assignment);
                    getCourseContentResponse.setOn_of_codingExercise(on_of_codingExercise);
                    getCourseContentResponse.setOn_of_practiceTest(on_of_practiceTest);
                    getCourseContentResponse.setSection_curriculum_item(getSectionCurriculumItemResponses);
                    getCourseContentResponses.add(getCourseContentResponse);

                }
                getCoursesDataResponse.setArticles_count(article_count);
                getCoursesDataResponse.setNo_of_videos(video_count);
                getCoursesDataResponse.setDownloadable_resources_count(downloadable_resources_count);
                getCoursesDataResponse.setEnrolled_count(0);
                List<GetIntendedLearnerResponse> getIntendedLearnerResponses = new ArrayList<>();
                List<CourseIntentedLearner> courseIntentedLearners = courseIntentedLearnerRepository.getCourseIntentedLearnerByCourse(course);
                for (CourseIntentedLearner courseIntentedLearner : courseIntentedLearners) {

                    GetIntendedLearnerResponse getIntendedLearnerResponse = new GetIntendedLearnerResponse();
                    getIntendedLearnerResponse.setIntended_learner(courseIntentedLearner.getName());
                    getIntendedLearnerResponse.setIntended_learner_type(courseIntentedLearner.getIntendedLearnerType().getName());
                    getIntendedLearnerResponses.add(getIntendedLearnerResponse);

                }
                getCoursesDataResponse.setIntended_learners(getIntendedLearnerResponses);
                getCoursesDataResponse.setCourse_content(getCourseContentResponses);

                responseLists.add(getCoursesDataResponse);
            }
        }
        return responseLists;
    }

    @Override
    public GetTopRatedCoursesWithSubCategoryByTopicResponse getTopSubCategoryCoursesByTopicLinkName(String topicLinkName) {
        if (topicLinkName == null || topicLinkName.isEmpty()) {
            throw new ErrorException("Empty topicLinkName", VarList.RSP_NO_DATA_FOUND);
        }
        Topic topic = topicRepository.getTopicByLinkName(topicLinkName);
        List<CourseLandingPage> courseLandingPageList = courseLandingPageRepository.getCourseLandingPagesBySubcategory(topic.getSubCategory());
        if (courseLandingPageList.size() == 0) {
            throw new ErrorException("Empty results related to sub Category", VarList.RSP_NO_DATA_FOUND);
        } else {
            List<GetCoursesDataResponse> responseLists = new ArrayList<>();
            for (CourseLandingPage courseLandingPage : courseLandingPageList) {
                if (courseLandingPage.getCourse().getApprovalType().getId() == 5) {
                    GetCoursesDataResponse getCoursesDataResponse = new GetCoursesDataResponse();
                    getCoursesDataResponse.setCourse_code(courseLandingPage.getCourse().getCode());
                    getCoursesDataResponse.setCreated_date(courseLandingPage.getCourse().getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter));
                    getCoursesDataResponse.setId(courseLandingPage.getCourse().getId());
                    getCoursesDataResponse.setIsPaid(courseLandingPage.getCourse().getIsPaid() == 2);
                    getCoursesDataResponse.setImg(courseLandingPage.getCourse().getImg());
                    getCoursesDataResponse.setDuration(Double.toString(courseLandingPage.getCourse().getCourseLength()));
                    getCoursesDataResponse.setCourse_outline("OutLine is not added to the course");

                    ExternalCourse externalCourse = externalCourseRepository.getExternalCourseByCourse(courseLandingPage.getCourse());
                    if (externalCourse != null) {
                        ExternalCourseResponse externalCourseResponse = new ExternalCourseResponse();
                        externalCourseResponse.setExternalNumberOfStudent(externalCourse.getExternalNumberOfStudents());
                        externalCourseResponse.setExternalRating(externalCourse.getExternalRating());
                        externalCourseResponse.setAnyComments((externalCourse.getAnyComment() == null || externalCourse.getAnyComment().isEmpty()) ? "" : externalCourseResponse.getAnyComments());
                        externalCourseResponse.setLinkToCourse((externalCourse.getLinkToCourse() == null || externalCourse.getLinkToCourse().isEmpty()) ? "" : externalCourse.getLinkToCourse());
                        getCoursesDataResponse.setExternalCourseDetails(externalCourseResponse);
                    }

                    getCoursesDataResponse.setLevel((courseLandingPage != null) ? courseLandingPage.getCourseLevel().getName() : null);
                    getCoursesDataResponse.setTitle(courseLandingPage.getCourse().getCourseTitle());
                    List<OrderHasCourse> orderHasCourses = orderHasCourseRepository.getOrderHasCoursesByCourse(courseLandingPage.getCourse());
                    List<GetRatingResponse> getRatingResponses = new ArrayList<>();
                    int rating_count = 0;
                    double rating_val = 0;

                    int oneRatingCount = 0;
                    int onePointFiveRatingCount = 0;
                    int twoRatingCount = 0;
                    int twoPointFiveRatingCount = 0;
                    int threeRatingCount = 0;
                    int threePointFiveRatingCount = 0;
                    int fourRatingCount = 0;
                    int fourPointFiveRatingCount = 0;
                    int fiveRatingCount = 0;
                    List<Review> reviews = reviewRepository.getReviewsByCourse(courseLandingPage.getCourse());
                    for (Review review : reviews) {
                        GetRatingResponse getRatingResponse = new GetRatingResponse();
                        getRatingResponse.setEmail(review.getGeneralUserProfile().getEmail());
                        getRatingResponse.setName(review.getGeneralUserProfile().getFirstName() + " " + review.getGeneralUserProfile().getLastName());
                        getRatingResponse.setRating(review.getRating());
                        getRatingResponse.setDate(review.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter));
                        getRatingResponse.setImg(review.getOrderHasCourse().getOrder().getGeneralUserProfile().getProfileImg());
                        getRatingResponse.setComment(review.getComment() == null ? "" : review.getComment());
                        getRatingResponses.add(getRatingResponse);
                        rating_count++;
                        rating_val = rating_val + review.getRating();

                        if (review.getRating() == 1) {
                            oneRatingCount++;
                        } else if (review.getRating() == 1.5) {
                            onePointFiveRatingCount++;
                        } else if (review.getRating() == 2) {
                            twoRatingCount++;
                        } else if (review.getRating() == 2.5) {
                            twoPointFiveRatingCount++;
                        } else if (review.getRating() == 3) {
                            threeRatingCount++;
                        } else if (review.getRating() == 3.5) {
                            threePointFiveRatingCount++;
                        } else if (review.getRating() == 4) {
                            fourRatingCount++;
                        } else if (review.getRating() == 4.5) {
                            fourPointFiveRatingCount++;
                        } else if (review.getRating() == 5) {
                            fiveRatingCount++;
                        }
                    }
                    getCoursesDataResponse.setReviews(getRatingResponses);
                    getCoursesDataResponse.setRating_count(rating_count);
                    StarsCountResponse starsCountResponse = new StarsCountResponse();
                    starsCountResponse.setOneRatingCount(oneRatingCount);
                    starsCountResponse.setOnePointFiveRatingCount(onePointFiveRatingCount);
                    starsCountResponse.setTwoRatingCount(twoRatingCount);
                    starsCountResponse.setTwoPointFiveRatingCount(twoPointFiveRatingCount);
                    starsCountResponse.setThreeRatingCount(threeRatingCount);
                    starsCountResponse.setThreePointFiveRatingCount(threePointFiveRatingCount);
                    starsCountResponse.setFourRatingCount(fourRatingCount);
                    starsCountResponse.setFourPointFiveRatingCount(fourPointFiveRatingCount);
                    starsCountResponse.setFiveRatingCount(fiveRatingCount);
                    getCoursesDataResponse.setRatingDetails(starsCountResponse);
                    getCoursesDataResponse.setRating(rating_count == 0 ? 0 : rating_val / rating_count);


                    List<GetCoursePricingResponse> responseList = new ArrayList<>();
                    List<CoursePrice> coursePriceList = coursePriceRepository.getCoursePriceByCourse(courseLandingPage.getCourse());
                    GetCoursePricesResponse response = new GetCoursePricesResponse();
                    if (coursePriceList.size() > 0) {

                        for (CoursePrice coursePrice : coursePriceList) {
                            if (coursePrice.getCountry().getId() == 30 && coursePrice.getCurrency().getId() == 30) {
                                response.setGlobalListPrice(coursePrice.getValue());
                                response.setDiscountTypeId(coursePrice.getDiscountType().getId());
                                response.setDiscountType(coursePrice.getDiscountType().getName());
                                response.setDiscountAmount(coursePrice.getDiscountValue());
                                try {
                                    response.setDiscount(coursePrice.getDiscount());
                                    response.setGlobalNetPrice(coursePrice.getNetPrice());
                                } catch (NullPointerException ex) {
                                    response.setDiscount(0.0);
                                    response.setGlobalNetPrice(0.0);
                                }
                            } else if (coursePrice.getCountry().getId() == 8) {
                                List<EuroCountry> euroCountries = euroCountryRepository.findAll();
                                for (EuroCountry euroCountry : euroCountries) {
                                    GetCoursePricingResponse getCoursePricingResponse = new GetCoursePricingResponse();
                                    try {
                                        getCoursePricingResponse.setNetPrice(coursePrice.getNetPrice());
                                        getCoursePricingResponse.setDiscount(coursePrice.getDiscount());
                                    } catch (NullPointerException ex) {
                                        getCoursePricingResponse.setNetPrice(0.0);
                                        getCoursePricingResponse.setDiscount(0.0);
                                    }
                                    getCoursePricingResponse.setListPrice(coursePrice.getValue());
                                    getCoursePricingResponse.setDiscountTypeId(coursePrice.getDiscountType().getId());
                                    getCoursePricingResponse.setDiscountType(coursePrice.getDiscountType().getName());
                                    getCoursePricingResponse.setCountry(euroCountry.getName());

                                    getCoursePricingResponse.setDiscountAmount(coursePrice.getDiscountValue());
                                    responseList.add(getCoursePricingResponse);
                                    response.setPrices(responseList);
                                }
                            } else {
                                GetCoursePricingResponse getCoursePricingResponse = new GetCoursePricingResponse();
                                try {
                                    getCoursePricingResponse.setNetPrice(coursePrice.getNetPrice());
                                    getCoursePricingResponse.setDiscount(coursePrice.getDiscount());
                                } catch (NullPointerException ex) {
                                    getCoursePricingResponse.setNetPrice(0.0);
                                    getCoursePricingResponse.setDiscount(0.0);
                                }
                                getCoursePricingResponse.setListPrice(coursePrice.getValue());
                                getCoursePricingResponse.setDiscountTypeId(coursePrice.getDiscountType().getId());
                                getCoursePricingResponse.setDiscountType(coursePrice.getDiscountType().getName());
                                getCoursePricingResponse.setCountry(coursePrice.getCountry().getName());

                                getCoursePricingResponse.setDiscountAmount(coursePrice.getDiscountValue());
                                responseList.add(getCoursePricingResponse);
                                response.setPrices(responseList);
                            }
                        }
                    }

                    getCoursesDataResponse.setCourse_prices(response);

                    List<CourseSection> courseSections = courseSectionRepository.getCourseSectionByCourse(courseLandingPage.getCourse());
                    getCoursesDataResponse.setLesson(courseSections.size());
                    getCoursesDataResponse.setStudent(orderHasCourses.size());
                    getCoursesDataResponse.setCategory(courseLandingPage.getCourse().getCourseCategory().getName());
                    getCoursesDataResponse.setCategory_link_name(courseLandingPage.getCourse().getCourseCategory().getLinkName());
                    getCoursesDataResponse.setSub_category(courseLandingPage.getSubcategory().getName());
                    getCoursesDataResponse.setSub_category_link_name(courseLandingPage.getSubcategory().getSubLinkName());
                    getCoursesDataResponse.setTopic(courseLandingPage.getTopic().getTopic());
                    getCoursesDataResponse.setTopic_link_name(courseLandingPage.getTopic().getLinkName());
                    getCoursesDataResponse.setShort_desc((courseLandingPage != null) ? courseLandingPage.getDescription() : null);
                    GeneralUserProfile instructor = courseLandingPage.getCourse().getInstructorId().getGeneralUserProfile();
                    getCoursesDataResponse.setInstructor_code(instructor.getUserCode());
                    getCoursesDataResponse.setInstructor(instructor.getFirstName() + " " + courseLandingPage.getCourse().getInstructorId().getGeneralUserProfile().getLastName());
                    getCoursesDataResponse.setInstructor_img(instructor.getProfileImg());
                    getCoursesDataResponse.setInstructor_title(courseLandingPage.getCourse().getInstructorId().getHeadline());
                    getCoursesDataResponse.setInstructor_desc(courseLandingPage.getCourse().getInstructorId().getBiography());
                    getCoursesDataResponse.setFeatures(null);
                    List<GetSocialLinksResponse> socialLinksResponsesList = new ArrayList<>();
                    GetSocialLinksResponse socialLinks = new GetSocialLinksResponse();
                    socialLinks.setLink(courseLandingPage.getCourse().getInstructorId().getWebsite());
                    socialLinks.setIcon("fa-solid fa-globe");
                    socialLinks.setTarget("Web Site");
                    socialLinksResponsesList.add(socialLinks);
                    socialLinks = new GetSocialLinksResponse();
                    socialLinks.setLink(courseLandingPage.getCourse().getInstructorId().getYoutube());
                    socialLinks.setIcon("fa-brands fa-youtube");
                    socialLinks.setTarget("youtube");
                    socialLinksResponsesList.add(socialLinks);
                    socialLinks = new GetSocialLinksResponse();
                    socialLinks.setLink(courseLandingPage.getCourse().getInstructorId().getFacebook());
                    socialLinks.setIcon("fa-brands fa-facebook");
                    socialLinks.setTarget("faceBook");
                    socialLinksResponsesList.add(socialLinks);
                    socialLinks = new GetSocialLinksResponse();
                    socialLinks.setLink(courseLandingPage.getCourse().getInstructorId().getTwitter());
                    socialLinks.setIcon("fa-brands fa-twitter");
                    socialLinks.setTarget("twitter");
                    socialLinksResponsesList.add(socialLinks);
                    socialLinks = new GetSocialLinksResponse();
                    socialLinks.setLink(courseLandingPage.getCourse().getInstructorId().getLinkedin());
                    socialLinks.setIcon("fa-brands fa-linkedin");
                    socialLinks.setTarget("linkdin");
                    socialLinksResponsesList.add(socialLinks);
                    getCoursesDataResponse.setSocial_links(socialLinksResponsesList);
                    getCoursesDataResponse.setLanguage((courseLandingPage != null) ? courseLandingPage.getLanguage().getName() : null);
                    getCoursesDataResponse.setSub_title((courseLandingPage != null) ? courseLandingPage.getSubTitle() : null);
                    getCoursesDataResponse.setCertificate("yes");
                    getCoursesDataResponse.setVideoId(courseLandingPage.getCourse().getTest_video());
                    getCoursesDataResponse.setCourse_main_desc((courseLandingPage != null) ? courseLandingPage.getDescription() : null);
                    getCoursesDataResponse.setCourse_desc_2((courseLandingPage != null) ? courseLandingPage.getDescription() : null);
                    getCoursesDataResponse.setLearnList(null);
                    getCoursesDataResponse.setCourse_desc_3((courseLandingPage != null) ? courseLandingPage.getDescription() : null);
                    getCoursesDataResponse.setCurriculum_desc("Curriculum Description is not added to the course");


                    int article_count = 0;
                    int video_count = 0;
                    int downloadable_resources_count = 0;

                    List<GetCourseContentResponse> getCourseContentResponses = new ArrayList<>();
                    for (CourseSection courseSectionObj : courseSections) {
                        GetCourseContentResponse getCourseContentResponse = new GetCourseContentResponse();
                        getCourseContentResponse.setSection_id(courseSectionObj.getId());
                        getCourseContentResponse.setSection_name(courseSectionObj.getSectionName());
                        int on_of_lectures = 0;
                        int on_of_quize = 0;
                        List<SectionCurriculumItem> articleCounts = sectionCurriculumItemRepository.getSectionCurriculumItemsByCourseSection(courseSectionObj);
                        List<GetSectionCurriculumItemResponse> getSectionCurriculumItemResponses = new ArrayList<>();
                        for (SectionCurriculumItem item : articleCounts) {
                            if (!item.getArticle().equals("N/A")) {
                                article_count++;
                            }
                            if (item.getCurriculumItemType().getId() == 1) {
                                on_of_lectures++;
                            } else if (item.getCurriculumItemType().getId() == 2) {
                                on_of_quize++;
                            }
                            GetSectionCurriculumItemResponse getSectionCurriculumItemResponse = new GetSectionCurriculumItemResponse();
                            getSectionCurriculumItemResponse.setTitle(item.getTitle());
                            getSectionCurriculumItemResponse.setArticle(item.getArticle());
                            getSectionCurriculumItemResponse.setDescription(item.getDescription());
                            getSectionCurriculumItemResponse.setCurriculum_item_type(item.getCurriculumItemType().getName());
                            getSectionCurriculumItemResponse.setCurriculumItemId(item.getId());
                            getSectionCurriculumItemResponse.setArrangeNo(item.getArrangedNo() == null ? "" : item.getArrangedNo().toString());
                            getSectionCurriculumItemResponses.add(getSectionCurriculumItemResponse);
                            List<GetCurriculumItemFilesResponse> getCurriculumItemFilesResponses = new ArrayList<>();

                            List<CurriculumItemFile> curriculumItemFiles = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItem(item);
                            for (CurriculumItemFile curriculumItemFile : curriculumItemFiles) {
                                if (curriculumItemFile.getCurriculumItemFileTypes().getId() == 3) {
                                    video_count++;
                                } else if (curriculumItemFile.getCurriculumItemFileTypes().getId() == 1) {
                                    downloadable_resources_count++;
                                }
                                GetCurriculumItemFilesResponse getCurriculumItemFilesResponse = new GetCurriculumItemFilesResponse();
                                getCurriculumItemFilesResponse.setTitle(curriculumItemFile.getTitle());
                                getCurriculumItemFilesResponse.setUrl(curriculumItemFile.getUrl());
                                getCurriculumItemFilesResponse.setCurriculum_item_file_type(curriculumItemFile.getCurriculumItemFileTypes().getName());
                                getCurriculumItemFilesResponses.add(getCurriculumItemFilesResponse);
                            }
                            getSectionCurriculumItemResponse.setGet_CurriculumItem_File(getCurriculumItemFilesResponses);
                        }
                        getCourseContentResponse.setNo_of_lectures(on_of_lectures);
                        getCourseContentResponse.setNo_of_qize(on_of_quize);
                        getCourseContentResponse.setSection_curriculum_item(getSectionCurriculumItemResponses);
                        getCourseContentResponses.add(getCourseContentResponse);

                    }
                    getCoursesDataResponse.setArticles_count(article_count);
                    getCoursesDataResponse.setNo_of_videos(video_count);
                    getCoursesDataResponse.setDownloadable_resources_count(downloadable_resources_count);
                    getCoursesDataResponse.setEnrolled_count(0);
                    List<GetIntendedLearnerResponse> getIntendedLearnerResponses = new ArrayList<>();
                    List<CourseIntentedLearner> courseIntentedLearners = courseIntentedLearnerRepository.getCourseIntentedLearnerByCourse(courseLandingPage.getCourse());
                    for (CourseIntentedLearner courseIntentedLearner : courseIntentedLearners) {

                        GetIntendedLearnerResponse getIntendedLearnerResponse = new GetIntendedLearnerResponse();
                        getIntendedLearnerResponse.setIntended_learner(courseIntentedLearner.getName());
                        getIntendedLearnerResponse.setIntended_learner_type(courseIntentedLearner.getIntendedLearnerType().getName());
                        getIntendedLearnerResponses.add(getIntendedLearnerResponse);

                    }
                    getCoursesDataResponse.setIntended_learners(getIntendedLearnerResponses);
                    getCoursesDataResponse.setCourse_content(getCourseContentResponses);

                    responseLists.add(getCoursesDataResponse);
                }
            }
            GetTopRatedCoursesWithSubCategoryByTopicResponse response = new GetTopRatedCoursesWithSubCategoryByTopicResponse();
            response.setSubCategory(topic.getSubCategory().getName());
            response.setSubCategoryLinkName(topic.getSubCategory().getSubLinkName());
            response.setCourses(responseLists);
            return response;

        }
    }

    @Override
    public List<GetCoursesDataResponse> getAllCourses() {
        List<GetCoursesDataResponse> getCoursesDataResponses = new ArrayList<>();
        GetCoursesDataResponse getCoursesDataResponse;
        CourseLandingPage courseLandingPage;
        List<Course> courses = courseRepository.findAll();
        for (Course courseobj : courses) {


            if (courseobj.getApprovalType().getId() == 5) {

                getCoursesDataResponse = new GetCoursesDataResponse();
                getCoursesDataResponse.setCourse_code(courseobj.getCode());

                getCoursesDataResponse.setCreated_date(courseobj.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter));

                getCoursesDataResponse.setId(courseobj.getId());
                getCoursesDataResponse.setIsPaid(courseobj.getIsPaid() == 2);
                getCoursesDataResponse.setImg(courseobj.getImg());
                getCoursesDataResponse.setDuration("0");
                getCoursesDataResponse.setCourse_outline("OutLine is not added to the course");
                courseLandingPage = courseLandingPageRepository.getCourseLandingPageByCourse(courseobj);
                if (courseLandingPage != null) {

                    getCoursesDataResponse.setLevel((courseLandingPage != null) ? courseLandingPage.getCourseLevel().getName() : null);
                    getCoursesDataResponse.setTitle(courseobj.getCourseTitle());
                    List<OrderHasCourse> orderHasCourses = orderHasCourseRepository.getOrderHasCoursesByCourse(courseobj);
                    List<GetRatingResponse> getRatingResponses = new ArrayList<>();
                    int rating_count = 0;
                    double rating_val = 0;

                    int oneRatingCount = 0;
                    int onePointFiveRatingCount = 0;
                    int twoRatingCount = 0;
                    int twoPointFiveRatingCount = 0;
                    int threeRatingCount = 0;
                    int threePointFiveRatingCount = 0;
                    int fourRatingCount = 0;
                    int fourPointFiveRatingCount = 0;
                    int fiveRatingCount = 0;
                    List<Review> reviews = reviewRepository.getReviewsByCourse(courseobj);
                    for (Review review : reviews) {
                        GetRatingResponse getRatingResponse = new GetRatingResponse();
                        getRatingResponse.setEmail(review.getGeneralUserProfile().getEmail());
                        getRatingResponse.setName(review.getGeneralUserProfile().getFirstName() + " " + review.getGeneralUserProfile().getLastName());
                        getRatingResponse.setRating(review.getRating());
                        getRatingResponse.setDate(review.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter));
                        getRatingResponse.setImg(review.getOrderHasCourse().getOrder().getGeneralUserProfile().getProfileImg());
                        getRatingResponse.setComment(review.getComment() == null ? "" : review.getComment());
                        getRatingResponses.add(getRatingResponse);
                        rating_count++;
                        rating_val = rating_val + review.getRating();
                        if (review.getRating() == 1) {
                            oneRatingCount++;
                        } else if (review.getRating() == 1.5) {
                            onePointFiveRatingCount++;
                        } else if (review.getRating() == 2) {
                            twoRatingCount++;
                        } else if (review.getRating() == 2.5) {
                            twoPointFiveRatingCount++;
                        } else if (review.getRating() == 3) {
                            threeRatingCount++;
                        } else if (review.getRating() == 3.5) {
                            threePointFiveRatingCount++;
                        } else if (review.getRating() == 4) {
                            fourRatingCount++;
                        } else if (review.getRating() == 4.5) {
                            fourPointFiveRatingCount++;
                        } else if (review.getRating() == 5) {
                            fiveRatingCount++;
                        }
                    }
                    getCoursesDataResponse.setReviews(getRatingResponses);
                    getCoursesDataResponse.setRating_count(rating_count);

                    StarsCountResponse starsCountResponse = new StarsCountResponse();
                    starsCountResponse.setOneRatingCount(oneRatingCount);
                    starsCountResponse.setOnePointFiveRatingCount(onePointFiveRatingCount);
                    starsCountResponse.setTwoRatingCount(twoRatingCount);
                    starsCountResponse.setTwoPointFiveRatingCount(twoPointFiveRatingCount);
                    starsCountResponse.setThreeRatingCount(threeRatingCount);
                    starsCountResponse.setThreePointFiveRatingCount(threePointFiveRatingCount);
                    starsCountResponse.setFourRatingCount(fourRatingCount);
                    starsCountResponse.setFourPointFiveRatingCount(fourPointFiveRatingCount);
                    starsCountResponse.setFiveRatingCount(fiveRatingCount);
                    getCoursesDataResponse.setRatingDetails(starsCountResponse);
                    getCoursesDataResponse.setRating(rating_count == 0 ? 0 : rating_val / rating_count);

                    List<GetCoursePricingResponse> responseList = new ArrayList<>();
                    List<CoursePrice> coursePriceList = coursePriceRepository.getCoursePriceByCourse(courseobj);
                    GetCoursePricesResponse response = new GetCoursePricesResponse();
                    if (coursePriceList.size() > 0) {

                        for (CoursePrice coursePrice : coursePriceList) {
                            if (coursePrice.getCountry().getId() == 30 && coursePrice.getCurrency().getId() == 30) {
                                response.setGlobalListPrice(coursePrice.getValue());
                                response.setDiscountTypeId(coursePrice.getDiscountType().getId());
                                response.setDiscountType(coursePrice.getDiscountType().getName());
                                response.setDiscountAmount(coursePrice.getDiscountValue());
                                try {
                                    response.setDiscount(coursePrice.getDiscount());
                                    response.setGlobalNetPrice(coursePrice.getNetPrice());
                                } catch (NullPointerException ex) {
                                    response.setDiscount(0.0);
                                    response.setGlobalNetPrice(0.0);
                                }

                            } else {
                                GetCoursePricingResponse getCoursePricingResponse = new GetCoursePricingResponse();
                                try {
                                    getCoursePricingResponse.setNetPrice(coursePrice.getNetPrice());
                                    getCoursePricingResponse.setDiscount(coursePrice.getDiscount());
                                } catch (NullPointerException ex) {
                                    getCoursePricingResponse.setNetPrice(0.0);
                                    getCoursePricingResponse.setDiscount(0.0);
                                }
                                getCoursePricingResponse.setListPrice(coursePrice.getValue());
                                getCoursePricingResponse.setDiscountTypeId(coursePrice.getDiscountType().getId());
                                getCoursePricingResponse.setDiscountType(coursePrice.getDiscountType().getName());
                                getCoursePricingResponse.setCountry(coursePrice.getCountry().getName());

                                getCoursePricingResponse.setDiscountAmount(coursePrice.getDiscountValue());
                                responseList.add(getCoursePricingResponse);
                                response.setPrices(responseList);
                            }
                        }
                    }

                    getCoursesDataResponse.setCourse_prices(response);

                    List<CourseSection> courseSections = courseSectionRepository.getCourseSectionByCourse(courseobj);
                    getCoursesDataResponse.setLesson(courseSections.size());
                    getCoursesDataResponse.setStudent(orderHasCourses.size());
                    getCoursesDataResponse.setCategory(courseobj.getCourseCategory().getName());
                    getCoursesDataResponse.setCategory_link_name(courseobj.getCourseCategory().getLinkName());
                    getCoursesDataResponse.setSub_category(courseLandingPage.getSubcategory().getName());
                    getCoursesDataResponse.setSub_category_link_name(courseLandingPage.getSubcategory().getSubLinkName());
                    getCoursesDataResponse.setTopic(courseLandingPage.getTopic().getTopic());
                    getCoursesDataResponse.setTopic_link_name(courseLandingPage.getTopic().getLinkName());
                    getCoursesDataResponse.setShort_desc((courseLandingPage != null) ? courseLandingPage.getDescription() : null);
                    GeneralUserProfile instructor = courseobj.getInstructorId().getGeneralUserProfile();
                    getCoursesDataResponse.setInstructor_code(instructor.getUserCode());
                    getCoursesDataResponse.setInstructor(instructor.getFirstName() + " " + courseobj.getInstructorId().getGeneralUserProfile().getLastName());
                    getCoursesDataResponse.setInstructor_img(instructor.getProfileImg());
                    getCoursesDataResponse.setInstructor_title(courseobj.getInstructorId().getHeadline());
                    getCoursesDataResponse.setInstructor_desc(courseobj.getInstructorId().getBiography());
                    getCoursesDataResponse.setFeatures(null);
                    List<GetSocialLinksResponse> socialLinksResponsesList = new ArrayList<>();
                    GetSocialLinksResponse socialLinks = new GetSocialLinksResponse();
                    socialLinks.setLink(courseobj.getInstructorId().getWebsite());
                    socialLinks.setIcon("fa-solid fa-globe");
                    socialLinks.setTarget("Web Site");
                    socialLinksResponsesList.add(socialLinks);
                    socialLinks = new GetSocialLinksResponse();
                    socialLinks.setLink(courseobj.getInstructorId().getYoutube());
                    socialLinks.setIcon("fa-brands fa-youtube");
                    socialLinks.setTarget("youtube");
                    socialLinksResponsesList.add(socialLinks);
                    socialLinks = new GetSocialLinksResponse();
                    socialLinks.setLink(courseobj.getInstructorId().getFacebook());
                    socialLinks.setIcon("fa-brands fa-facebook");
                    socialLinks.setTarget("faceBook");
                    socialLinksResponsesList.add(socialLinks);
                    socialLinks = new GetSocialLinksResponse();
                    socialLinks.setLink(courseobj.getInstructorId().getTwitter());
                    socialLinks.setIcon("fa-brands fa-twitter");
                    socialLinks.setTarget("twitter");
                    socialLinksResponsesList.add(socialLinks);
                    socialLinks = new GetSocialLinksResponse();
                    socialLinks.setLink(courseobj.getInstructorId().getLinkedin());
                    socialLinks.setIcon("fa-brands fa-linkedin");
                    socialLinks.setTarget("linkdin");
                    socialLinksResponsesList.add(socialLinks);
                    getCoursesDataResponse.setSocial_links(socialLinksResponsesList);
                    getCoursesDataResponse.setLanguage((courseLandingPage != null) ? courseLandingPage.getLanguage().getName() : null);
                    getCoursesDataResponse.setSub_title((courseLandingPage != null) ? courseLandingPage.getSubTitle() : null);
                    getCoursesDataResponse.setCertificate("yes");
                    getCoursesDataResponse.setVideoId(courseobj.getTest_video());
                    getCoursesDataResponse.setCourse_main_desc((courseLandingPage != null) ? courseLandingPage.getDescription() : null);
                    getCoursesDataResponse.setCourse_desc_2((courseLandingPage != null) ? courseLandingPage.getDescription() : null);
                    getCoursesDataResponse.setLearnList(null);
                    getCoursesDataResponse.setCourse_desc_3((courseLandingPage != null) ? courseLandingPage.getDescription() : null);
                    getCoursesDataResponse.setCurriculum_desc("Curriculum Description is not added to the course");


                    int article_count = 0;
                    int video_count = 0;
                    int downloadable_resources_count = 0;

                    List<GetCourseContentResponse> getCourseContentResponses = new ArrayList<>();
                    for (CourseSection courseSectionObj : courseSections) {
                        GetCourseContentResponse getCourseContentResponse = new GetCourseContentResponse();
                        getCourseContentResponse.setSection_id(courseSectionObj.getId());
                        getCourseContentResponse.setSection_name(courseSectionObj.getSectionName());
                        int on_of_lectures = 0;
                        int on_of_quize = 0;
                        List<SectionCurriculumItem> articleCounts = sectionCurriculumItemRepository.getSectionCurriculumItemsByCourseSection(courseSectionObj);
                        List<GetSectionCurriculumItemResponse> getSectionCurriculumItemResponses = new ArrayList<>();
                        for (SectionCurriculumItem item : articleCounts) {
                            if (!item.getArticle().equals("N/A")) {
                                article_count++;
                            }
                            if (item.getCurriculumItemType().getId() == 1) {
                                on_of_lectures++;
                            } else if (item.getCurriculumItemType().getId() == 2) {
                                on_of_quize++;
                            }
                            GetSectionCurriculumItemResponse getSectionCurriculumItemResponse = new GetSectionCurriculumItemResponse();
                            getSectionCurriculumItemResponse.setTitle(item.getTitle());
                            getSectionCurriculumItemResponse.setArticle(item.getArticle());
                            getSectionCurriculumItemResponse.setDescription(item.getDescription());
                            getSectionCurriculumItemResponse.setCurriculum_item_type(item.getCurriculumItemType().getName());
                            getSectionCurriculumItemResponse.setCurriculumItemId(item.getId());
                            getSectionCurriculumItemResponse.setArrangeNo(item.getArrangedNo() == null ? "" : item.getArrangedNo().toString());
                            getSectionCurriculumItemResponses.add(getSectionCurriculumItemResponse);
                            List<GetCurriculumItemFilesResponse> getCurriculumItemFilesResponses = new ArrayList<>();

                            List<CurriculumItemFile> curriculumItemFiles = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItem(item);
                            for (CurriculumItemFile curriculumItemFile : curriculumItemFiles) {
                                if (curriculumItemFile.getCurriculumItemFileTypes().getId() == 3) {
                                    video_count++;
                                } else if (curriculumItemFile.getCurriculumItemFileTypes().getId() == 1) {
                                    downloadable_resources_count++;
                                }
                                GetCurriculumItemFilesResponse getCurriculumItemFilesResponse = new GetCurriculumItemFilesResponse();
                                getCurriculumItemFilesResponse.setTitle(curriculumItemFile.getTitle());
                                getCurriculumItemFilesResponse.setUrl(curriculumItemFile.getUrl());
                                getCurriculumItemFilesResponse.setCurriculum_item_file_type(curriculumItemFile.getCurriculumItemFileTypes().getName());
                                getCurriculumItemFilesResponses.add(getCurriculumItemFilesResponse);
                            }
                            getSectionCurriculumItemResponse.setGet_CurriculumItem_File(getCurriculumItemFilesResponses);
                        }
                        getCourseContentResponse.setNo_of_lectures(on_of_lectures);
                        getCourseContentResponse.setNo_of_qize(on_of_quize);
                        getCourseContentResponse.setSection_curriculum_item(getSectionCurriculumItemResponses);
                        getCourseContentResponses.add(getCourseContentResponse);

                    }
                    getCoursesDataResponse.setArticles_count(article_count);
                    getCoursesDataResponse.setNo_of_videos(video_count);
                    getCoursesDataResponse.setDownloadable_resources_count(downloadable_resources_count);
                    getCoursesDataResponse.setEnrolled_count(0);
                    List<GetIntendedLearnerResponse> getIntendedLearnerResponses = new ArrayList<>();
                    List<CourseIntentedLearner> courseIntentedLearners = courseIntentedLearnerRepository.getCourseIntentedLearnerByCourse(courseobj);
                    for (CourseIntentedLearner courseIntentedLearner : courseIntentedLearners) {

                        GetIntendedLearnerResponse getIntendedLearnerResponse = new GetIntendedLearnerResponse();
                        getIntendedLearnerResponse.setIntended_learner(courseIntentedLearner.getName());
                        getIntendedLearnerResponse.setIntended_learner_type(courseIntentedLearner.getIntendedLearnerType().getName());
                        getIntendedLearnerResponses.add(getIntendedLearnerResponse);

                    }
                    getCoursesDataResponse.setIntended_learners(getIntendedLearnerResponses);
                    getCoursesDataResponse.setCourse_content(getCourseContentResponses);

                    getCoursesDataResponses.add(getCoursesDataResponse);
                }
            }
        }
        return getCoursesDataResponses;
    }

    @Override
    public List<GetCourseContentResponse> getCourseContent(String courseCode) {
        Course course = courseRepository.getCourseByCode(courseCode);
        if (course == null) {
            throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
        }
        List<CourseSection> courseSections = courseSectionRepository.getCourseSectionByCourse(course);
        List<GetCourseContentResponse> getCourseContentResponses = new ArrayList<>();
        for (CourseSection courseSection : courseSections) {
            int on_of_lectures = 0;
            int on_of_quize = 0;
            GetCourseContentResponse getCourseContentResponse = new GetCourseContentResponse();
            getCourseContentResponse.setSection_id(courseSection.getId());
            getCourseContentResponse.setSection_name(courseSection.getSectionName());
            List<SectionCurriculumItem> sectionCurriculumItems = sectionCurriculumItemRepository.getSectionCurriculumItemByCourseSection(courseSection);
            List<GetSectionCurriculumItemResponse> getSectionCurriculumItemResponses = new ArrayList<>();
            for (SectionCurriculumItem curriculumItem : sectionCurriculumItems) {
                if (curriculumItem.getCurriculumItemType().getId() == 1) {
                    on_of_lectures++;
                } else if (curriculumItem.getCurriculumItemType().getId() == 2) {
                    on_of_quize++;
                }
                GetSectionCurriculumItemResponse getSectionCurriculumItemResponse = new GetSectionCurriculumItemResponse();
                getSectionCurriculumItemResponse.setTitle(curriculumItem.getTitle());
                getSectionCurriculumItemResponse.setArticle(curriculumItem.getArticle());
                getSectionCurriculumItemResponse.setDescription(curriculumItem.getDescription());
                getSectionCurriculumItemResponse.setCurriculum_item_type(curriculumItem.getCurriculumItemType().getName());
                getSectionCurriculumItemResponse.setCurriculumItemId(curriculumItem.getId());
                getSectionCurriculumItemResponse.setArrangeNo(curriculumItem.getArrangedNo() == null ? "" : curriculumItem.getArrangedNo().toString());
                getSectionCurriculumItemResponses.add(getSectionCurriculumItemResponse);
                List<GetCurriculumItemFilesResponse> getCurriculumItemFilesResponses = new ArrayList<>();
                List<CurriculumItemFile> curriculumItemFiles = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItem(curriculumItem);
                for (CurriculumItemFile curriculumItemFile : curriculumItemFiles) {
                    GetCurriculumItemFilesResponse getCurriculumItemFilesResponse = new GetCurriculumItemFilesResponse();
                    getCurriculumItemFilesResponse.setTitle(curriculumItemFile.getTitle());
                    getCurriculumItemFilesResponse.setUrl(curriculumItemFile.getUrl());
                    getCurriculumItemFilesResponse.setCurriculum_item_file_type(curriculumItemFile.getCurriculumItemFileTypes().getName());
                    getCurriculumItemFilesResponses.add(getCurriculumItemFilesResponse);
                }
                getSectionCurriculumItemResponse.setGet_CurriculumItem_File(getCurriculumItemFilesResponses);
            }
            getCourseContentResponse.setNo_of_lectures(on_of_lectures);
            getCourseContentResponse.setNo_of_qize(on_of_quize);
            getCourseContentResponse.setSection_curriculum_item(getSectionCurriculumItemResponses);
            getCourseContentResponses.add(getCourseContentResponse);
        }
        return getCourseContentResponses;
    }

    @Override
    public GetTopicCategorySubCategoryByTopicResponse getTopicCategorySubCategoryByTopic(String topicLinkName) {
        List<Topic> topics = topicRepository.getTopicsByLinkName(topicLinkName);
        if (topics.size() != 0) {
            Topic topic = topics.get(0);
            GetTopicCategorySubCategoryByTopicResponse response = new GetTopicCategorySubCategoryByTopicResponse();
            response.setTopic(topic.getTopic());
            response.setCategory(topic.getSubCategory().getCourseCategory().getName());
            response.setCategory_linkName(topic.getSubCategory().getCourseCategory().getLinkName());
            response.setSubCategory(topic.getSubCategory().getName());
            response.setSubCategory_linkName(topic.getSubCategory().getSubLinkName());
            return response;

        } else {
            throw new ErrorException("No results related to topic linkName", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public List<GetCoursesDataResponse> GetLimitedCountCoursesForHomeByLinkName(String linkName) {
        if (linkName == null || linkName.isEmpty()) {
            throw new ErrorException("Please add a link name", VarList.RSP_NO_DATA_FOUND);
        }
//        List<Course> courses = courseRepository.findTop8ByCourseCategoryOrderByCreatedDateDesc(courseCategoryRepository.getCourseCategoryByLinkName(linkName));

        List<String> courseCodes = new ArrayList<>();
        if (linkName.equals("personal-development")) {
            courseCodes = Arrays.asList(
                    "1afabb42-d9d5-44a1-abf1-9bc4917e5e44",
                    "0769d057-797d-475c-84ba-c11fd6651c04",
                    "9d93acf7-5543-45fb-9f18-c3e57b5fbe69"
//                    "3eff5c42-dc77-41b5-bd1b-44f46b2ed1ec"
//                    "aacde467-3684-43e1-8ed0-42c1f0247828",
//                    "4d87412f-1dbe-452d-847e-05dd93fbf8b3",
//                    "27f74652-e691-45a8-bcb8-24cfaeef4076",
//                    "de9429df-18d6-4aae-9990-f0242ad59ff2",
//                    "b7317e93-d1cc-45a3-b575-a98b873b7069",
//                    "90c1e353-358d-46fb-b6c4-8c57007245ea",

            );
        } else if (linkName.equals("it-software")) {
            courseCodes = Arrays.asList(
                    "bce00c98-3ea7-4eca-acab-332e2be443d8",
                    "8a1bbe61-1e33-448c-8ead-1ff54a4b62d5",
                    "de99ea0a-3a12-476a-9c8b-2fc56383539d",
                    "12916eee-44df-4481-9960-097bf90feaca",
                    "7d9635d8-c04d-42d3-a3f4-010f98c68eb7",
                    "bdad79ee-68b7-4802-8ad9-25214dbc313a",
                    "39bc573f-3c31-4276-98d8-52e1e283fefe",
                    "9d15b374-699b-4b04-9afc-5afa3de99b62",
                    "4b8c2911-2584-4acf-a559-7de8218dd4be",
                    "355ea0a2-6e43-4d0f-8b13-c308aebdb65b"

            );
        }


        List<Course> unorderedCourses = courseRepository.findByCodeIn(courseCodes);
        if (unorderedCourses.size() == 0) {
            throw new ErrorException("There are no courses with that link name", VarList.RSP_NO_DATA_FOUND);
        }

        Map<String, Course> courseMap = unorderedCourses.stream()
                .collect(Collectors.toMap(Course::getCode, course -> course, (a, b) -> a, LinkedHashMap::new));

        List<Course> courses = courseCodes.stream()
                .map(courseMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<GetCoursesDataResponse> getCoursesDataResponses = new ArrayList<>();
        GetCoursesDataResponse getCoursesDataResponse;
        CourseLandingPage courseLandingPage;

        for (Course courseobj : courses) {
            if (courseobj.getApprovalType().getId() == 5) {

                getCoursesDataResponse = new GetCoursesDataResponse();
                getCoursesDataResponse.setCourse_code(courseobj.getCode());

                getCoursesDataResponse.setCreated_date(courseobj.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter));

                getCoursesDataResponse.setId(courseobj.getId());
                getCoursesDataResponse.setIsPaid(courseobj.getIsPaid() == 2);
                getCoursesDataResponse.setImg(courseobj.getImg());
                getCoursesDataResponse.setDuration(Double.toString(courseobj.getCourseLength()));
                getCoursesDataResponse.setCourse_outline("OutLine is not added to the course");

                ExternalCourse externalCourse = externalCourseRepository.getExternalCourseByCourse(courseobj);
                if (externalCourse != null) {
                    ExternalCourseResponse externalCourseResponse = new ExternalCourseResponse();
                    externalCourseResponse.setExternalNumberOfStudent(externalCourse.getExternalNumberOfStudents());
                    externalCourseResponse.setExternalRating(externalCourse.getExternalRating());
                    externalCourseResponse.setAnyComments((externalCourse.getAnyComment() == null || externalCourse.getAnyComment().isEmpty()) ? "" : externalCourseResponse.getAnyComments());
                    externalCourseResponse.setLinkToCourse((externalCourse.getLinkToCourse() == null || externalCourse.getLinkToCourse().isEmpty()) ? "" : externalCourse.getLinkToCourse());
                    getCoursesDataResponse.setExternalCourseDetails(externalCourseResponse);
                }

                courseLandingPage = courseLandingPageRepository.getCourseLandingPageByCourse(courseobj);
                if (courseLandingPage != null) {

                    getCoursesDataResponse.setLevel((courseLandingPage != null) ? courseLandingPage.getCourseLevel().getName() : null);
                    getCoursesDataResponse.setTitle(courseobj.getCourseTitle());
                    List<OrderHasCourse> orderHasCourses = orderHasCourseRepository.getOrderHasCoursesByCourse(courseobj);
                    List<GetRatingResponse> getRatingResponses = new ArrayList<>();
                    int rating_count = 0;
                    double rating_val = 0;

                    int oneRatingCount = 0;
                    int onePointFiveRatingCount = 0;
                    int twoRatingCount = 0;
                    int twoPointFiveRatingCount = 0;
                    int threeRatingCount = 0;
                    int threePointFiveRatingCount = 0;
                    int fourRatingCount = 0;
                    int fourPointFiveRatingCount = 0;
                    int fiveRatingCount = 0;
                    List<Review> reviews = reviewRepository.getReviewsByCourse(courseobj);
                    for (Review review : reviews) {
                        GetRatingResponse getRatingResponse = new GetRatingResponse();
                        getRatingResponse.setEmail(review.getGeneralUserProfile().getEmail());
                        getRatingResponse.setName(review.getGeneralUserProfile().getFirstName() + " " + review.getGeneralUserProfile().getLastName());
                        getRatingResponse.setRating(review.getRating());
                        getRatingResponse.setDate(review.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter));
                        getRatingResponse.setImg(review.getOrderHasCourse().getOrder().getGeneralUserProfile().getProfileImg());
                        getRatingResponse.setComment(review.getComment() == null ? "" : review.getComment());
                        getRatingResponses.add(getRatingResponse);
                        rating_count++;
                        rating_val = rating_val + review.getRating();

                        if (review.getRating() == 1) {
                            oneRatingCount++;
                        } else if (review.getRating() == 1.5) {
                            onePointFiveRatingCount++;
                        } else if (review.getRating() == 2) {
                            twoRatingCount++;
                        } else if (review.getRating() == 2.5) {
                            twoPointFiveRatingCount++;
                        } else if (review.getRating() == 3) {
                            threeRatingCount++;
                        } else if (review.getRating() == 3.5) {
                            threePointFiveRatingCount++;
                        } else if (review.getRating() == 4) {
                            fourRatingCount++;
                        } else if (review.getRating() == 4.5) {
                            fourPointFiveRatingCount++;
                        } else if (review.getRating() == 5) {
                            fiveRatingCount++;
                        }
                    }
                    getCoursesDataResponse.setReviews(getRatingResponses);
                    getCoursesDataResponse.setRating_count(rating_count);
                    StarsCountResponse starsCountResponse = new StarsCountResponse();
                    starsCountResponse.setOneRatingCount(oneRatingCount);
                    starsCountResponse.setOnePointFiveRatingCount(onePointFiveRatingCount);
                    starsCountResponse.setTwoRatingCount(twoRatingCount);
                    starsCountResponse.setTwoPointFiveRatingCount(twoPointFiveRatingCount);
                    starsCountResponse.setThreeRatingCount(threeRatingCount);
                    starsCountResponse.setThreePointFiveRatingCount(threePointFiveRatingCount);
                    starsCountResponse.setFourRatingCount(fourRatingCount);
                    starsCountResponse.setFourPointFiveRatingCount(fourPointFiveRatingCount);
                    starsCountResponse.setFiveRatingCount(fiveRatingCount);
                    getCoursesDataResponse.setRatingDetails(starsCountResponse);
                    getCoursesDataResponse.setRating(rating_count == 0 ? 0 : rating_val / rating_count);


                    List<GetCoursePricingResponse> responseList = new ArrayList<>();
                    List<CoursePrice> coursePriceList = coursePriceRepository.getCoursePriceByCourse(courseobj);
                    GetCoursePricesResponse response = new GetCoursePricesResponse();
                    if (coursePriceList.size() > 0) {

                        for (CoursePrice coursePrice : coursePriceList) {
                            if (coursePrice.getCountry().getId() == 30 && coursePrice.getCurrency().getId() == 30) {
                                response.setGlobalListPrice(coursePrice.getValue());
                                response.setDiscountTypeId(coursePrice.getDiscountType().getId());
                                response.setDiscountType(coursePrice.getDiscountType().getName());
                                response.setDiscountAmount(coursePrice.getDiscountValue());
                                try {
                                    response.setDiscount(coursePrice.getDiscount());
                                    response.setGlobalNetPrice(coursePrice.getNetPrice());
                                } catch (NullPointerException ex) {
                                    response.setDiscount(0.0);
                                    response.setGlobalNetPrice(0.0);
                                }
                            } else if (coursePrice.getCountry().getId() == 8) {
                                List<EuroCountry> euroCountries = euroCountryRepository.findAll();
                                for (EuroCountry euroCountry : euroCountries) {
                                    GetCoursePricingResponse getCoursePricingResponse = new GetCoursePricingResponse();
                                    try {
                                        getCoursePricingResponse.setNetPrice(coursePrice.getNetPrice());
                                        getCoursePricingResponse.setDiscount(coursePrice.getDiscount());
                                    } catch (NullPointerException ex) {
                                        getCoursePricingResponse.setNetPrice(0.0);
                                        getCoursePricingResponse.setDiscount(0.0);
                                    }
                                    getCoursePricingResponse.setListPrice(coursePrice.getValue());
                                    getCoursePricingResponse.setDiscountTypeId(coursePrice.getDiscountType().getId());
                                    getCoursePricingResponse.setDiscountType(coursePrice.getDiscountType().getName());
                                    getCoursePricingResponse.setCountry(euroCountry.getName());

                                    getCoursePricingResponse.setDiscountAmount(coursePrice.getDiscountValue());
                                    responseList.add(getCoursePricingResponse);
                                    response.setPrices(responseList);
                                }
                            } else {
                                GetCoursePricingResponse getCoursePricingResponse = new GetCoursePricingResponse();
                                try {
                                    getCoursePricingResponse.setNetPrice(coursePrice.getNetPrice());
                                    getCoursePricingResponse.setDiscount(coursePrice.getDiscount());
                                } catch (NullPointerException ex) {
                                    getCoursePricingResponse.setNetPrice(0.0);
                                    getCoursePricingResponse.setDiscount(0.0);
                                }
                                getCoursePricingResponse.setListPrice(coursePrice.getValue());
                                getCoursePricingResponse.setDiscountTypeId(coursePrice.getDiscountType().getId());
                                getCoursePricingResponse.setDiscountType(coursePrice.getDiscountType().getName());
                                getCoursePricingResponse.setCountry(coursePrice.getCountry().getName());

                                getCoursePricingResponse.setDiscountAmount(coursePrice.getDiscountValue());
                                responseList.add(getCoursePricingResponse);
                                response.setPrices(responseList);
                            }
                        }
                    }

                    getCoursesDataResponse.setCourse_prices(response);

                    List<CourseSection> courseSections = courseSectionRepository.getCourseSectionByCourse(courseobj);
                    getCoursesDataResponse.setLesson(courseSections.size());
                    getCoursesDataResponse.setStudent(orderHasCourses.size());
                    getCoursesDataResponse.setCategory(courseobj.getCourseCategory().getName());
                    getCoursesDataResponse.setCategory_link_name(courseobj.getCourseCategory().getLinkName());
                    getCoursesDataResponse.setSub_category(courseLandingPage.getSubcategory().getName());
                    getCoursesDataResponse.setSub_category_link_name(courseLandingPage.getSubcategory().getSubLinkName());
                    getCoursesDataResponse.setTopic(courseLandingPage.getTopic().getTopic());
                    getCoursesDataResponse.setTopic_link_name(courseLandingPage.getTopic().getLinkName());
                    getCoursesDataResponse.setShort_desc((courseLandingPage != null) ? courseLandingPage.getDescription() : null);
                    GeneralUserProfile instructor = courseobj.getInstructorId().getGeneralUserProfile();
                    getCoursesDataResponse.setInstructor_code(instructor.getUserCode());
                    getCoursesDataResponse.setInstructor(instructor.getFirstName() + " " + courseobj.getInstructorId().getGeneralUserProfile().getLastName());
                    getCoursesDataResponse.setInstructor_img(instructor.getProfileImg());
                    getCoursesDataResponse.setInstructor_title(courseobj.getInstructorId().getHeadline());
                    getCoursesDataResponse.setInstructor_desc(courseobj.getInstructorId().getBiography());
                    getCoursesDataResponse.setFeatures(null);
                    List<GetSocialLinksResponse> socialLinksResponsesList = new ArrayList<>();
                    GetSocialLinksResponse socialLinks = new GetSocialLinksResponse();
                    socialLinks.setLink(courseobj.getInstructorId().getWebsite());
                    socialLinks.setIcon("fa-solid fa-globe");
                    socialLinks.setTarget("Web Site");
                    socialLinksResponsesList.add(socialLinks);
                    socialLinks = new GetSocialLinksResponse();
                    socialLinks.setLink(courseobj.getInstructorId().getYoutube());
                    socialLinks.setIcon("fa-brands fa-youtube");
                    socialLinks.setTarget("youtube");
                    socialLinksResponsesList.add(socialLinks);
                    socialLinks = new GetSocialLinksResponse();
                    socialLinks.setLink(courseobj.getInstructorId().getFacebook());
                    socialLinks.setIcon("fa-brands fa-facebook");
                    socialLinks.setTarget("faceBook");
                    socialLinksResponsesList.add(socialLinks);
                    socialLinks = new GetSocialLinksResponse();
                    socialLinks.setLink(courseobj.getInstructorId().getTwitter());
                    socialLinks.setIcon("fa-brands fa-twitter");
                    socialLinks.setTarget("twitter");
                    socialLinksResponsesList.add(socialLinks);
                    socialLinks = new GetSocialLinksResponse();
                    socialLinks.setLink(courseobj.getInstructorId().getLinkedin());
                    socialLinks.setIcon("fa-brands fa-linkedin");
                    socialLinks.setTarget("linkdin");
                    socialLinksResponsesList.add(socialLinks);
                    getCoursesDataResponse.setSocial_links(socialLinksResponsesList);
                    getCoursesDataResponse.setLanguage((courseLandingPage != null) ? courseLandingPage.getLanguage().getName() : null);
                    getCoursesDataResponse.setSub_title((courseLandingPage != null) ? courseLandingPage.getSubTitle() : null);
                    getCoursesDataResponse.setCertificate("yes");
                    getCoursesDataResponse.setVideoId(courseobj.getTest_video());
                    getCoursesDataResponse.setCourse_main_desc((courseLandingPage != null) ? courseLandingPage.getDescription() : null);
                    getCoursesDataResponse.setCourse_desc_2((courseLandingPage != null) ? courseLandingPage.getDescription() : null);
                    getCoursesDataResponse.setLearnList(null);
                    getCoursesDataResponse.setCourse_desc_3((courseLandingPage != null) ? courseLandingPage.getDescription() : null);
                    getCoursesDataResponse.setCurriculum_desc("Curriculum Description is not added to the course");


                    int article_count = 0;
                    int video_count = 0;
                    int downloadable_resources_count = 0;

                    List<GetCourseContentResponse> getCourseContentResponses = new ArrayList<>();
                    for (CourseSection courseSectionObj : courseSections) {
                        GetCourseContentResponse getCourseContentResponse = new GetCourseContentResponse();
                        getCourseContentResponse.setSection_id(courseSectionObj.getId());
                        getCourseContentResponse.setSection_name(courseSectionObj.getSectionName());
                        int on_of_lectures = 0;
                        int on_of_quize = 0;
                        List<SectionCurriculumItem> articleCounts = sectionCurriculumItemRepository.getSectionCurriculumItemsByCourseSection(courseSectionObj);
                        List<GetSectionCurriculumItemResponse> getSectionCurriculumItemResponses = new ArrayList<>();
                        for (SectionCurriculumItem item : articleCounts) {
                            if (!item.getArticle().equals("N/A")) {
                                article_count++;
                            }
                            if (item.getCurriculumItemType().getId() == 1) {
                                on_of_lectures++;
                            } else if (item.getCurriculumItemType().getId() == 2) {
                                on_of_quize++;
                            }
                            GetSectionCurriculumItemResponse getSectionCurriculumItemResponse = new GetSectionCurriculumItemResponse();
                            getSectionCurriculumItemResponse.setTitle(item.getTitle());
                            getSectionCurriculumItemResponse.setArticle(item.getArticle());
                            getSectionCurriculumItemResponse.setDescription(item.getDescription());
                            getSectionCurriculumItemResponse.setCurriculum_item_type(item.getCurriculumItemType().getName());
                            getSectionCurriculumItemResponse.setCurriculumItemId(item.getId());
                            getSectionCurriculumItemResponse.setArrangeNo(item.getArrangedNo() == null ? "" : item.getArrangedNo().toString());
                            getSectionCurriculumItemResponses.add(getSectionCurriculumItemResponse);
                            List<GetCurriculumItemFilesResponse> getCurriculumItemFilesResponses = new ArrayList<>();

                            List<CurriculumItemFile> curriculumItemFiles = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItem(item);
                            for (CurriculumItemFile curriculumItemFile : curriculumItemFiles) {
                                if (curriculumItemFile.getCurriculumItemFileTypes().getId() == 3) {
                                    video_count++;
                                } else if (curriculumItemFile.getCurriculumItemFileTypes().getId() == 1) {
                                    downloadable_resources_count++;
                                }
                                GetCurriculumItemFilesResponse getCurriculumItemFilesResponse = new GetCurriculumItemFilesResponse();
                                getCurriculumItemFilesResponse.setTitle(curriculumItemFile.getTitle());
                                getCurriculumItemFilesResponse.setUrl(curriculumItemFile.getUrl());
                                getCurriculumItemFilesResponse.setCurriculum_item_file_type(curriculumItemFile.getCurriculumItemFileTypes().getName());
                                getCurriculumItemFilesResponses.add(getCurriculumItemFilesResponse);
                            }
                            getSectionCurriculumItemResponse.setGet_CurriculumItem_File(getCurriculumItemFilesResponses);
                        }
                        getCourseContentResponse.setNo_of_lectures(on_of_lectures);
                        getCourseContentResponse.setNo_of_qize(on_of_quize);
                        getCourseContentResponse.setSection_curriculum_item(getSectionCurriculumItemResponses);
                        getCourseContentResponses.add(getCourseContentResponse);

                    }
                    getCoursesDataResponse.setArticles_count(article_count);
                    getCoursesDataResponse.setNo_of_videos(video_count);
                    getCoursesDataResponse.setDownloadable_resources_count(downloadable_resources_count);
                    getCoursesDataResponse.setEnrolled_count(0);
                    List<GetIntendedLearnerResponse> getIntendedLearnerResponses = new ArrayList<>();
                    List<CourseIntentedLearner> courseIntentedLearners = courseIntentedLearnerRepository.getCourseIntentedLearnerByCourse(courseobj);
                    for (CourseIntentedLearner courseIntentedLearner : courseIntentedLearners) {

                        GetIntendedLearnerResponse getIntendedLearnerResponse = new GetIntendedLearnerResponse();
                        getIntendedLearnerResponse.setIntended_learner(courseIntentedLearner.getName());
                        getIntendedLearnerResponse.setIntended_learner_type(courseIntentedLearner.getIntendedLearnerType().getName());
                        getIntendedLearnerResponses.add(getIntendedLearnerResponse);

                    }
                    getCoursesDataResponse.setIntended_learners(getIntendedLearnerResponses);
                    getCoursesDataResponse.setCourse_content(getCourseContentResponses);

                    getCoursesDataResponses.add(getCoursesDataResponse);
                }
            }
        }
        return getCoursesDataResponses;
    }

    @Override
    public List<TopicResponse> getRelatedTopicsByTopicLinkName(String topicLinkName) {
        List<Topic> topics = topicRepository.getTopicsByLinkName(topicLinkName);
        if (topics.size() == 0) {
            throw new ErrorException("No results related to topic linkName", VarList.RSP_NO_DATA_FOUND);
        }

        List<TopicResponse> topicResponseList = new ArrayList<>();

        for (Topic topic2 : topics) {

            List<Topic> topicList = topicRepository.getTopicsBySubCategory(topic2.getSubCategory());

            for (Topic topic1 : topicList) {
                if (topic1 != topic2) {
                    TopicResponse topicResponse = new TopicResponse();
                    topicResponse.setId(topic1.getId());
                    topicResponse.setTopic(topic1.getTopic());
                    topicResponse.setLink_name(topic1.getLinkName());
                    topicResponseList.add(topicResponse);
                }
            }
        }
        return topicResponseList;
    }

    @Override
    public List<GetCoursesDataResponse> getAllFreeCourses() {
        List<Course> courses = courseRepository.getCoursesByIsPaidOrderByCreatedDateDesc(0);
        if (courses.size() == 0) {
            throw new ErrorException("There are no courses with that link name", VarList.RSP_NO_DATA_FOUND);
        }

        List<GetCoursesDataResponse> getCoursesDataResponses = new ArrayList<>();
        GetCoursesDataResponse getCoursesDataResponse;
        CourseLandingPage courseLandingPage;

        for (Course courseobj : courses) {
            if (courseobj.getApprovalType().getId() == 5) {

                getCoursesDataResponse = new GetCoursesDataResponse();
                getCoursesDataResponse.setCourse_code(courseobj.getCode());

                getCoursesDataResponse.setCreated_date(courseobj.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter));

                getCoursesDataResponse.setId(courseobj.getId());
                getCoursesDataResponse.setIsPaid(courseobj.getIsPaid() == 2);
                getCoursesDataResponse.setImg(courseobj.getImg());
                getCoursesDataResponse.setDuration("0");
                getCoursesDataResponse.setCourse_outline("OutLine is not added to the course");
                courseLandingPage = courseLandingPageRepository.getCourseLandingPageByCourse(courseobj);
                if (courseLandingPage != null) {

                    getCoursesDataResponse.setLevel((courseLandingPage != null) ? courseLandingPage.getCourseLevel().getName() : null);
                    getCoursesDataResponse.setTitle(courseobj.getCourseTitle());
                    List<OrderHasCourse> orderHasCourses = orderHasCourseRepository.getOrderHasCoursesByCourse(courseobj);
                    List<GetRatingResponse> getRatingResponses = new ArrayList<>();
                    int rating_count = 0;
                    double rating_val = 0;

                    int oneRatingCount = 0;
                    int onePointFiveRatingCount = 0;
                    int twoRatingCount = 0;
                    int twoPointFiveRatingCount = 0;
                    int threeRatingCount = 0;
                    int threePointFiveRatingCount = 0;
                    int fourRatingCount = 0;
                    int fourPointFiveRatingCount = 0;
                    int fiveRatingCount = 0;
                    List<Review> reviews = reviewRepository.getReviewsByCourse(courseobj);
                    for (Review review : reviews) {
                        GetRatingResponse getRatingResponse = new GetRatingResponse();
                        getRatingResponse.setEmail(review.getGeneralUserProfile().getEmail());
                        getRatingResponse.setName(review.getGeneralUserProfile().getFirstName() + " " + review.getGeneralUserProfile().getLastName());
                        getRatingResponse.setRating(review.getRating());
                        getRatingResponse.setDate(review.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter));
                        getRatingResponse.setImg(review.getOrderHasCourse().getOrder().getGeneralUserProfile().getProfileImg());
                        getRatingResponse.setComment(review.getComment() == null ? "" : review.getComment());
                        getRatingResponses.add(getRatingResponse);
                        rating_count++;
                        rating_val = rating_val + review.getRating();

                        if (review.getRating() == 1) {
                            oneRatingCount++;
                        } else if (review.getRating() == 1.5) {
                            onePointFiveRatingCount++;
                        } else if (review.getRating() == 2) {
                            twoRatingCount++;
                        } else if (review.getRating() == 2.5) {
                            twoPointFiveRatingCount++;
                        } else if (review.getRating() == 3) {
                            threeRatingCount++;
                        } else if (review.getRating() == 3.5) {
                            threePointFiveRatingCount++;
                        } else if (review.getRating() == 4) {
                            fourRatingCount++;
                        } else if (review.getRating() == 4.5) {
                            fourPointFiveRatingCount++;
                        } else if (review.getRating() == 5) {
                            fiveRatingCount++;
                        }
                    }

                    getCoursesDataResponse.setReviews(getRatingResponses);
                    getCoursesDataResponse.setRating_count(rating_count);
                    StarsCountResponse starsCountResponse = new StarsCountResponse();
                    starsCountResponse.setOneRatingCount(oneRatingCount);
                    starsCountResponse.setOnePointFiveRatingCount(onePointFiveRatingCount);
                    starsCountResponse.setTwoRatingCount(twoRatingCount);
                    starsCountResponse.setTwoPointFiveRatingCount(twoPointFiveRatingCount);
                    starsCountResponse.setThreeRatingCount(threeRatingCount);
                    starsCountResponse.setThreePointFiveRatingCount(threePointFiveRatingCount);
                    starsCountResponse.setFourRatingCount(fourRatingCount);
                    starsCountResponse.setFourPointFiveRatingCount(fourPointFiveRatingCount);
                    starsCountResponse.setFiveRatingCount(fiveRatingCount);
                    getCoursesDataResponse.setRatingDetails(starsCountResponse);
                    getCoursesDataResponse.setRating(rating_count == 0 ? 0 : rating_val / rating_count);


                    GetCoursePricesResponse getCoursePricesResponse = new GetCoursePricesResponse();
                    getCoursePricesResponse.setDiscountType("N/A");
                    List<GetCoursePricingResponse> getCoursePricingResponses = new ArrayList<>();
                    GetCoursePricingResponse getCoursePricingResponse = new GetCoursePricingResponse();
                    getCoursePricingResponse.setCountry("All countries");
                    getCoursePricingResponse.setDiscountType("N/A");
                    getCoursePricingResponses.add(getCoursePricingResponse);
                    getCoursePricesResponse.setPrices(getCoursePricingResponses);
                    getCoursesDataResponse.setCourse_prices(getCoursePricesResponse);

                    List<CourseSection> courseSections = courseSectionRepository.getCourseSectionByCourse(courseobj);
                    getCoursesDataResponse.setLesson(courseSections.size());
                    getCoursesDataResponse.setStudent(orderHasCourses.size());
                    getCoursesDataResponse.setCategory(courseobj.getCourseCategory().getName());
                    getCoursesDataResponse.setCategory_link_name(courseobj.getCourseCategory().getLinkName());
                    getCoursesDataResponse.setSub_category(courseLandingPage.getSubcategory().getName());
                    getCoursesDataResponse.setSub_category_link_name(courseLandingPage.getSubcategory().getSubLinkName());
                    getCoursesDataResponse.setTopic(courseLandingPage.getTopic().getTopic());
                    getCoursesDataResponse.setTopic_link_name(courseLandingPage.getTopic().getLinkName());
                    getCoursesDataResponse.setShort_desc((courseLandingPage != null) ? courseLandingPage.getDescription() : null);
                    GeneralUserProfile instructor = courseobj.getInstructorId().getGeneralUserProfile();
                    getCoursesDataResponse.setInstructor_code(instructor.getUserCode());
                    getCoursesDataResponse.setInstructor(instructor.getFirstName() + " " + courseobj.getInstructorId().getGeneralUserProfile().getLastName());
                    getCoursesDataResponse.setInstructor_img(instructor.getProfileImg());
                    getCoursesDataResponse.setInstructor_title(courseobj.getInstructorId().getHeadline());
                    getCoursesDataResponse.setInstructor_desc(courseobj.getInstructorId().getBiography());
                    getCoursesDataResponse.setFeatures(null);
                    List<GetSocialLinksResponse> socialLinksResponsesList = new ArrayList<>();
                    GetSocialLinksResponse socialLinks = new GetSocialLinksResponse();
                    socialLinks.setLink(courseobj.getInstructorId().getWebsite());
                    socialLinks.setIcon("fa-solid fa-globe");
                    socialLinks.setTarget("Web Site");
                    socialLinksResponsesList.add(socialLinks);
                    socialLinks = new GetSocialLinksResponse();
                    socialLinks.setLink(courseobj.getInstructorId().getYoutube());
                    socialLinks.setIcon("fa-brands fa-youtube");
                    socialLinks.setTarget("youtube");
                    socialLinksResponsesList.add(socialLinks);
                    socialLinks = new GetSocialLinksResponse();
                    socialLinks.setLink(courseobj.getInstructorId().getFacebook());
                    socialLinks.setIcon("fa-brands fa-facebook");
                    socialLinks.setTarget("faceBook");
                    socialLinksResponsesList.add(socialLinks);
                    socialLinks = new GetSocialLinksResponse();
                    socialLinks.setLink(courseobj.getInstructorId().getTwitter());
                    socialLinks.setIcon("fa-brands fa-twitter");
                    socialLinks.setTarget("twitter");
                    socialLinksResponsesList.add(socialLinks);
                    socialLinks = new GetSocialLinksResponse();
                    socialLinks.setLink(courseobj.getInstructorId().getLinkedin());
                    socialLinks.setIcon("fa-brands fa-linkedin");
                    socialLinks.setTarget("linkdin");
                    socialLinksResponsesList.add(socialLinks);
                    getCoursesDataResponse.setSocial_links(socialLinksResponsesList);
                    getCoursesDataResponse.setLanguage((courseLandingPage != null) ? courseLandingPage.getLanguage().getName() : null);
                    getCoursesDataResponse.setSub_title((courseLandingPage != null) ? courseLandingPage.getSubTitle() : null);
                    getCoursesDataResponse.setCertificate("yes");
                    getCoursesDataResponse.setVideoId(courseobj.getTest_video());
                    getCoursesDataResponse.setCourse_main_desc((courseLandingPage != null) ? courseLandingPage.getDescription() : null);
                    getCoursesDataResponse.setCourse_desc_2((courseLandingPage != null) ? courseLandingPage.getDescription() : null);
                    getCoursesDataResponse.setLearnList(null);
                    getCoursesDataResponse.setCourse_desc_3((courseLandingPage != null) ? courseLandingPage.getDescription() : null);
                    getCoursesDataResponse.setCurriculum_desc("Curriculum Description is not added to the course");


                    int article_count = 0;
                    int video_count = 0;
                    int downloadable_resources_count = 0;

                    List<GetCourseContentResponse> getCourseContentResponses = new ArrayList<>();
                    for (CourseSection courseSectionObj : courseSections) {
                        GetCourseContentResponse getCourseContentResponse = new GetCourseContentResponse();
                        getCourseContentResponse.setSection_id(courseSectionObj.getId());
                        getCourseContentResponse.setSection_name(courseSectionObj.getSectionName());
                        int on_of_lectures = 0;
                        int on_of_quize = 0;
                        List<SectionCurriculumItem> articleCounts = sectionCurriculumItemRepository.getSectionCurriculumItemsByCourseSection(courseSectionObj);
                        List<GetSectionCurriculumItemResponse> getSectionCurriculumItemResponses = new ArrayList<>();
                        for (SectionCurriculumItem item : articleCounts) {
                            if (!item.getArticle().equals("N/A")) {
                                article_count++;
                            }
                            if (item.getCurriculumItemType().getId() == 1) {
                                on_of_lectures++;
                            } else if (item.getCurriculumItemType().getId() == 2) {
                                on_of_quize++;
                            }
                            GetSectionCurriculumItemResponse getSectionCurriculumItemResponse = new GetSectionCurriculumItemResponse();
                            getSectionCurriculumItemResponse.setTitle(item.getTitle());
                            getSectionCurriculumItemResponse.setArticle(item.getArticle());
                            getSectionCurriculumItemResponse.setDescription(item.getDescription());
                            getSectionCurriculumItemResponse.setCurriculum_item_type(item.getCurriculumItemType().getName());
                            getSectionCurriculumItemResponse.setCurriculumItemId(item.getId());
                            getSectionCurriculumItemResponse.setArrangeNo(item.getArrangedNo() == null ? "" : item.getArrangedNo().toString());
                            getSectionCurriculumItemResponses.add(getSectionCurriculumItemResponse);
                            List<GetCurriculumItemFilesResponse> getCurriculumItemFilesResponses = new ArrayList<>();

                            List<CurriculumItemFile> curriculumItemFiles = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItem(item);
                            for (CurriculumItemFile curriculumItemFile : curriculumItemFiles) {
                                if (curriculumItemFile.getCurriculumItemFileTypes().getId() == 3) {
                                    video_count++;
                                } else if (curriculumItemFile.getCurriculumItemFileTypes().getId() == 1) {
                                    downloadable_resources_count++;
                                }
                                GetCurriculumItemFilesResponse getCurriculumItemFilesResponse = new GetCurriculumItemFilesResponse();
                                getCurriculumItemFilesResponse.setTitle(curriculumItemFile.getTitle());
                                getCurriculumItemFilesResponse.setUrl(curriculumItemFile.getUrl());
                                getCurriculumItemFilesResponse.setCurriculum_item_file_type(curriculumItemFile.getCurriculumItemFileTypes().getName());
                                getCurriculumItemFilesResponses.add(getCurriculumItemFilesResponse);
                            }
                            getSectionCurriculumItemResponse.setGet_CurriculumItem_File(getCurriculumItemFilesResponses);
                        }
                        getCourseContentResponse.setNo_of_lectures(on_of_lectures);
                        getCourseContentResponse.setNo_of_qize(on_of_quize);
                        getCourseContentResponse.setSection_curriculum_item(getSectionCurriculumItemResponses);
                        getCourseContentResponses.add(getCourseContentResponse);

                    }
                    getCoursesDataResponse.setArticles_count(article_count);
                    getCoursesDataResponse.setNo_of_videos(video_count);
                    getCoursesDataResponse.setDownloadable_resources_count(downloadable_resources_count);
                    getCoursesDataResponse.setEnrolled_count(0);
                    List<GetIntendedLearnerResponse> getIntendedLearnerResponses = new ArrayList<>();
                    List<CourseIntentedLearner> courseIntentedLearners = courseIntentedLearnerRepository.getCourseIntentedLearnerByCourse(courseobj);
                    for (CourseIntentedLearner courseIntentedLearner : courseIntentedLearners) {

                        GetIntendedLearnerResponse getIntendedLearnerResponse = new GetIntendedLearnerResponse();
                        getIntendedLearnerResponse.setIntended_learner(courseIntentedLearner.getName());
                        getIntendedLearnerResponse.setIntended_learner_type(courseIntentedLearner.getIntendedLearnerType().getName());
                        getIntendedLearnerResponses.add(getIntendedLearnerResponse);

                    }
                    getCoursesDataResponse.setIntended_learners(getIntendedLearnerResponses);
                    getCoursesDataResponse.setCourse_content(getCourseContentResponses);

                    getCoursesDataResponses.add(getCoursesDataResponse);
                }
            }
        }
        return getCoursesDataResponses;
    }
}