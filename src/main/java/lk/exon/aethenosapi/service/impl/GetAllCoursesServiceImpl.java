package lk.exon.aethenosapi.service.impl;

import lk.exon.aethenosapi.config.EmailConfig;
import lk.exon.aethenosapi.entity.*;
import lk.exon.aethenosapi.exception.ErrorException;
import lk.exon.aethenosapi.payload.request.AddCourseCompleteDetailsRequest;
import lk.exon.aethenosapi.payload.request.DisApproveCourseRequest;
import lk.exon.aethenosapi.payload.response.*;
import lk.exon.aethenosapi.repository.*;
import lk.exon.aethenosapi.service.GetAllCoursesService;
import lk.exon.aethenosapi.utils.EmailSender;
import lk.exon.aethenosapi.utils.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class GetAllCoursesServiceImpl implements GetAllCoursesService {

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserProfileServiceImpl userProfileServiceImpl;
    @Autowired
    private InstructorProfileRepository instructorProfileRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private ApprovalTypeRepository approvalTypeRepository;
    @Autowired
    private CourseCategoryRepository courseCategoryRepository;
    @Autowired
    private CourseSubCategoryRepository courseSubCategoryRepository;
    @Autowired
    private CourseIntentedLearnerRepository courseIntentedLearnerRepository;
    @Autowired
    private CourseSectionRepository courseSectionRepository;
    @Autowired
    private GeneralUserProfileRepository generalUserProfileRepository;
    @Autowired
    private SectionCurriculumItemRepository sectionCurriculumItemRepository;
    @Autowired
    private CourseLandingPageRepository courseLandingPageRepository;
    @Autowired
    private CoursePriceRepository coursePriceRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private CourseMessageRepository courseMessageRepository;
    @Autowired
    private PromotionCouponRepository promotionCouponRepository;
    @Autowired
    private CurriculumItemFileRepository curriculumItemFileRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private GupTypeRepository gupTypeRepository;
    @Autowired
    private OrderHasCourseRepository orderHasCourseRepository;
    @Autowired
    private CurriculumItemTypeRepository curriculumItemTypeRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private CourseCompleteRepository courseCompleteRepository;
    @Autowired
    private CurriculumItemFileTypeRepository curriculumItemFileTypeRepository;
    @Autowired
    private AssignmentRepository assignmentRepository;
    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private CodingExerciseRepository codingExerciseRepository;
    @Autowired
    private PracticeTestRepository practiceTestRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private InstructorPaymentsRepository instructorPaymentsRepository;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private ExternalCourseRepository externalCourseRepository;
    @Autowired
    private EuroCountryRepository euroCountryRepository;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private SuccessResponse successResponse = new SuccessResponse();


    @Override
    public List<CourseWithProgressResponse> findByInstructorId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile.getIsActive() == 1) {
            if (profile.getGupType().getId() == 2) {
                InstructorProfile instructorProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfileId(profile.getId());
                List<CourseWithProgressResponse> responseList = new ArrayList<>();
                List<Course> courseList = courseRepository.getAllByInstructorId(instructorProfile);
                if (courseList.size() == 0) {
                    throw new ErrorException("No courses found related to instructor", VarList.RSP_NO_DATA_FOUND);
                } else {
                    for (Course course : courseList) {
                        CourseWithProgressResponse response = new CourseWithProgressResponse();
                        response.setCourse(course);
                        response.setProgress(courseCurriculumProgress(course.getCode()));
                        responseList.add(response);
                    }
                    return responseList;
                }
            } else {
                throw new ErrorException("This is not an instructor", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public List<CourseCategory> getAllCourseCategory() {
        List<CourseCategory> courseCategories = courseCategoryRepository.findAll();
        if (courseCategories.size() > 0) {
            return courseCategories;
        } else {
            throw new ErrorException("Courses categories are not available", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public List<Course> getAllDraftedCourses() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 3) {
                    List<Course> course = courseRepository.getCourseByApprovalType(approvalTypeRepository.getApprovalTypeById(1));
                    if (course.size() > 0) {
                        return course;
                    } else {
                        throw new ErrorException("There are no test videos", VarList.RSP_NO_DATA_FOUND);
                    }
                } else {
                    throw new ErrorException("You are not a admin to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse setRequestedCourse(String courseCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    Course course = courseRepository.getCourseByCode(courseCode);
                    if (course != null) {
                        course.setApprovalType(approvalTypeRepository.getApprovalTypeById(7));
                        courseRepository.save(course);
                        successResponse.setMessage("The approval type was successfully updated");
                        successResponse.setVariable(VarList.RSP_SUCCESS);
                        return successResponse;
                    } else {
                        throw new ErrorException("invalid course code", VarList.RSP_NO_DATA_FOUND);
                    }
                } else {
                    throw new ErrorException("You are not a instructor to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public GetAllRequestedCourseResponse getAllRequestedCode() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 3) {
                    List<Course> courses = courseRepository.getCourseByApprovalType(approvalTypeRepository.getApprovalTypeById(7));
                    GetAllRequestedCourseResponse getAllRequestedCourseResponse = new GetAllRequestedCourseResponse();
                    if (courses.size() > 0) {
                        List<RequestedCourseResponse> requestedCourseResponses = new ArrayList<>();
                        for (Course course : courses) {
                            RequestedCourseResponse requestedCourseResponse = new RequestedCourseResponse();
                            requestedCourseResponse.setId(course.getId());
                            requestedCourseResponse.setCode(course.getCode());
                            requestedCourseResponse.setCourseTitle(course.getCourseTitle());
                            requestedCourseResponse.setComment(course.getComment());
                            requestedCourseResponse.setImg(course.getImg());
                            requestedCourseResponse.setTest_video(course.getTest_video());
                            requestedCourseResponse.setCourseLength(course.getCourseLength());
                            requestedCourseResponse.setCreatedDate(course.getCreatedDate());
                            requestedCourseResponse.setIsPaid(course.getIsPaid());
                            requestedCourseResponse.setInstructorId(course.getInstructorId());
                            requestedCourseResponse.setApprovalType(course.getApprovalType());
                            requestedCourseResponse.setCourseCategory(course.getCourseCategory());
                            requestedCourseResponse.setIsOwned(course.getIsOwned());
                            requestedCourseResponse.setBuyCount(course.getBuyCount());
                            requestedCourseResponse.setReferralCode(course.getReferralCode());

                            // adding external Course Details
                            ExternalCourse externalCourse = externalCourseRepository.getExternalCourseByCourse(course);
                            requestedCourseResponse.setAnyComments(externalCourse != null && externalCourse.getAnyComment() != null && !externalCourse.getAnyComment().isEmpty() ? externalCourse.getAnyComment() : "");
                            requestedCourseResponse.setExternalNumberOfStudents(externalCourse != null ? externalCourse.getExternalNumberOfStudents() : 0);
                            requestedCourseResponse.setExternalRating(externalCourse != null ? externalCourse.getExternalRating() : 0);
                            requestedCourseResponse.setLinkToCourse(externalCourse != null && externalCourse.getLinkToCourse() != null && !externalCourse.getLinkToCourse().isEmpty() ? externalCourse.getLinkToCourse() : "");

                            List<Coupon> couponList = couponRepository.getCouponByCourse(course);

                            List<CouponResponse> couponArrayList = new ArrayList<>();
                            for (Coupon coupon : couponList) {
                                CouponResponse couponResponse = new CouponResponse();
                                couponResponse.setCouponCode(coupon.getCode());
                                couponResponse.setStartDate(coupon.getStartDate());
                                couponResponse.setEndDate(coupon.getEndDate());
                                couponResponse.setCouponType(coupon.getCouponType());
                                couponResponse.setIsActive(coupon.getIsActive());
                                couponArrayList.add(couponResponse);
                            }

                            requestedCourseResponse.setPromotions(couponArrayList);

                            List<GetCourseContentResponse> getCourseContentResponses = new ArrayList<>();
                            Integer completedItemCount = 0;
                            Integer allItemCount = 0;
                            List<CourseSection> courseSections = courseSectionRepository.getCourseSectionByCourse(course);
                            for (CourseSection courseSectionObj : courseSections) {
                                if (courseSectionObj.getIsDelete() == null || courseSectionObj.getIsDelete() == 0) {
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
                                        if (item.getIsDelete() == null || item.getIsDelete() == 0) {
                                            allItemCount++;

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
                                            getSectionCurriculumItemResponse.setCurriculumItemId(item.getId());
                                            getSectionCurriculumItemResponse.setArrangeNo(item.getArrangedNo() == null ? "" : item.getArrangedNo().toString());
                                            getSectionCurriculumItemResponse.setCurriculum_item_type(item.getCurriculumItemType().getName());
                                            getSectionCurriculumItemResponse.setCurriculumItemId(item.getId());


                                            Assignment assignment = assignmentRepository.getAssignmentBySectionCurriculumItem(item);
                                            List<GetAssignment> getAssignments = new ArrayList<>();
                                            if (assignment != null && (assignment.getIsDelete() == null || assignment.getIsDelete() == 0)) {
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
                                            if (codingExercise != null && (codingExercise.getIsDelete() == null || codingExercise.getIsDelete() == 0)) {
                                                GetCodingExercise getCodingExercise = new GetCodingExercise();
                                                getCodingExercise.setCodingExerciseCode(codingExercise.getCodingExerciseCode());
                                                getCodingExercise.setInstructions(codingExercise.getInstructions());
                                                CurriculumItemFileType curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(10);
                                                CurriculumItemFile curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                                                getCodingExercise.setCodingVideo(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                                                curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(11);
                                                curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(item, curriculumItemFileType);
                                                getCodingExercise.setDownloadableResource(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                                                getCodingExercise.setExternalLink(codingExercise.getExternalLink() != null ? codingExercise.getExternalLink() : "");
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
                                            if (practiceTest != null && (practiceTest.getIsDelete() == null || practiceTest.getIsDelete() == 0)) {
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
                                            List<GetCurriculumItemFilesResponse> getCurriculumItemFilesResponses = new ArrayList<>();

                                            List<CurriculumItemFile> curriculumItemFiles = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItem(item);
                                            for (CurriculumItemFile curriculumItemFile : curriculumItemFiles) {
                                                if (curriculumItemFile.getIsDelete() == null || curriculumItemFile.getIsDelete() == 0) {
                                                    GetCurriculumItemFilesResponse getCurriculumItemFilesResponse = new GetCurriculumItemFilesResponse();
                                                    if (curriculumItemFile.getCurriculumItemFileTypes().getId() == 3) {
                                                        getCurriculumItemFilesResponse.setVideoLength(curriculumItemFile.getVideoLength() != null ? curriculumItemFile.getVideoLength() : 0);
                                                    }
                                                    getCurriculumItemFilesResponse.setTitle(curriculumItemFile.getTitle());
                                                    getCurriculumItemFilesResponse.setUrl(curriculumItemFile.getUrl());
                                                    getCurriculumItemFilesResponse.setCurriculum_item_file_type(curriculumItemFile.getCurriculumItemFileTypes().getName());
                                                    getCurriculumItemFilesResponses.add(getCurriculumItemFilesResponse);
                                                }
                                            }
                                            getSectionCurriculumItemResponse.setGet_CurriculumItem_File(getCurriculumItemFilesResponses);
                                            getSectionCurriculumItemResponses.add(getSectionCurriculumItemResponse);
                                        }
                                    }
                                    getCourseContentResponse.setNo_of_lectures(on_of_lectures);
                                    getCourseContentResponse.setNo_of_qize(on_of_quize);
                                    getCourseContentResponse.setOn_of_assignment(on_of_assignment);
                                    getCourseContentResponse.setOn_of_codingExercise(on_of_codingExercise);
                                    getCourseContentResponse.setOn_of_practiceTest(on_of_practiceTest);
                                    getCourseContentResponse.setSection_curriculum_item(getSectionCurriculumItemResponses);
                                    getCourseContentResponses.add(getCourseContentResponse);
                                }
                            }

                            requestedCourseResponse.setCourse_content(getCourseContentResponses);
                            requestedCourseResponses.add(requestedCourseResponse);
                        }
                        getAllRequestedCourseResponse.setCourse(requestedCourseResponses);
                        return getAllRequestedCourseResponse;
                    } else {
                        throw new ErrorException("There are no test videos", VarList.RSP_NO_DATA_FOUND);
                    }
                } else {
                    throw new ErrorException("You are not a admin to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse setApproveCourse(String courseCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 3) {
                    Course course = courseRepository.getCourseByCode(courseCode);
                    if (course != null) {
                        ApprovalType approvalType = approvalTypeRepository.getApprovalTypeById(3);
                        if (course.getApprovalType().getId() != approvalType.getId()) {
                            course.setApprovalType(approvalType);
                            courseRepository.save(course);
                            Notification notification = new Notification();
                            notification.setNotificationCode(UUID.randomUUID().toString());
                            notification.setNotification("Your test video for \"" + course.getCourseTitle() + "\" has been approved");
                            notification.setNotificationTime(new Date());
                            notification.setGeneralUserProfile(course.getInstructorId().getGeneralUserProfile());
                            notification.setRead(false);
                            notificationRepository.save(notification);
                            Properties properties = EmailConfig.getEmailProperties(course.getInstructorId().getGeneralUserProfile().getFirstName() + " " + course.getInstructorId().getGeneralUserProfile().getLastName(), "Test Video Approved");
                            properties.put("courseTitle", course.getCourseTitle());
                            try {
                                EmailSender emailSender = new EmailSender();
                                emailSender.sendEmail("DraftCourseApproveMessage", course.getInstructorId().getGeneralUserProfile().getEmail(), (String) properties.getProperty("from"), (String) properties.get("subject"), properties);

                            } catch (MessagingException e) {
                                e.printStackTrace();
                            }
                        }

                        successResponse.setMessage("The approval type was successfully updated to approved");
                        successResponse.setVariable(VarList.RSP_SUCCESS);
                        return successResponse;
                    } else {
                        throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
                    }
                } else {
                    throw new ErrorException("You are not a admin to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse setDisapproveCourse(DisApproveCourseRequest disApproveCourseRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 3) {
                    String comment = disApproveCourseRequest.getComment();
                    String getcode = disApproveCourseRequest.getCode();
                    if (getcode == null || getcode.isEmpty()) {
                        throw new ErrorException("Please add a course's code", VarList.RSP_NO_DATA_FOUND);
                    }
                    Course course = courseRepository.getCourseByCode(getcode);
                    if (course != null) {
                        if (comment == null || comment.isEmpty()) {
                            throw new ErrorException("Please add a comment", VarList.RSP_NO_DATA_FOUND);
                        }
                        ApprovalType approvalType = approvalTypeRepository.getApprovalTypeById(2);
                        if (course.getApprovalType().getId() != approvalType.getId()) {
                            course.setApprovalType(approvalType);
                            course.setComment(disApproveCourseRequest.getComment());
                            courseRepository.save(course);

                            Notification notification = new Notification();
                            notification.setNotificationCode(UUID.randomUUID().toString());
                            notification.setNotification("Your test video for \"" + course.getCourseTitle() + "\" has been rejected. Please refer email for further details.");
                            notification.setNotificationTime(new Date());
                            notification.setGeneralUserProfile(course.getInstructorId().getGeneralUserProfile());
                            notification.setRead(false);
                            notificationRepository.save(notification);

                            Properties properties = EmailConfig.getEmailProperties(course.getInstructorId().getGeneralUserProfile().getFirstName() + " " + course.getInstructorId().getGeneralUserProfile().getLastName(), "Test video disapproved");
                            properties.put("feedback", course.getComment());
                            properties.put("courseTitle", course.getCourseTitle());
                            try {
                                EmailSender emailSender = new EmailSender();
                                emailSender.sendEmail("DraftCourseDisapproveMessage", course.getInstructorId().getGeneralUserProfile().getEmail(), (String) properties.getProperty("from"), (String) properties.get("subject"), properties);

                            } catch (MessagingException e) {
                                e.printStackTrace();
                            }
                        }
                        successResponse.setMessage("The approval type was successfully updated to disapproved");
                        successResponse.setVariable(VarList.RSP_SUCCESS);
                        return successResponse;
                    } else {
                        throw new ErrorException("invalid course code", VarList.RSP_NO_DATA_FOUND);
                    }
                } else {
                    throw new ErrorException("You are not a admin to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public GetCourseTitleAndApproveTypeResponse getCourseTitleAndApproveType(String courseCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    if (courseCode == null || courseCode.isEmpty()) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    }
                    Course course = courseRepository.getCourseByCode(courseCode);
                    if (course != null) {
                        GetCourseTitleAndApproveTypeResponse getCourseTitleAndApproveTypeResponse = new GetCourseTitleAndApproveTypeResponse();
                        getCourseTitleAndApproveTypeResponse.setTitle(course.getCourseTitle());
                        getCourseTitleAndApproveTypeResponse.setApproveType(course.getApprovalType().getName());
                        getCourseTitleAndApproveTypeResponse.setCourseLength(course.getCourseLength());
                        return getCourseTitleAndApproveTypeResponse;
                    } else {
                        throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
                    }
                } else {
                    throw new ErrorException("You are not a instructor to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse submitForReview(String courseCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    Course course = courseRepository.getCourseByCode(courseCode);
                    if (course != null) {
                        InstructorProfile instructorProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(profile);
                        if (instructorProfile == null || instructorProfile.getIsProfileCompleted() != 1) {
                            throw new ErrorException("Instructor profile details are not complete. So please fill it and try again", VarList.RSP_NO_DATA_FOUND);
                        }

                        course.setApprovalType(approvalTypeRepository.getApprovalTypeById(7));
                        course.setComment("");
                        courseRepository.save(course);

                        GupType gupType = gupTypeRepository.getGupTypeById(3);

                        List<GeneralUserProfile> adminProfile = generalUserProfileRepository.getGeneralUserProfileByGupType(gupType);
                        String noficationCode = UUID.randomUUID().toString();
                        Properties properties;
                        try {
                            EmailSender emailSender = new EmailSender();
                            for (GeneralUserProfile adminProfileObj : adminProfile) {
                                Notification notification = new Notification();
                                notification.setNotificationCode(noficationCode);
                                notification.setNotification("You have a new Course Submission to Review");
                                notification.setNotificationTime(new Date());
                                notification.setGeneralUserProfile(adminProfileObj);
                                notification.setRead(false);
                                notificationRepository.save(notification);

                                properties = EmailConfig.getEmailProperties(adminProfileObj.getFirstName() + " " + adminProfileObj.getLastName(), "Course Submission for Review.");
                                emailSender.sendEmail("CourseSubmissionForReviewMessage", adminProfileObj.getEmail(), (String) properties.getProperty("from"), (String) properties.get("subject"), properties);

                            }

                            properties = EmailConfig.getEmailProperties(profile.getFirstName() + " " + profile.getLastName(), "Course Submitted For Review");
                            properties.put("courseTitle", course.getCourseTitle());

                            emailSender.sendEmail("SubmitForReviewMessage", profile.getEmail(), (String) properties.getProperty("from"), (String) properties.get("subject"), properties);

                        } catch (MessagingException e) {
                            e.printStackTrace();
                        }
                        successResponse.setMessage("Your course has been successfully submitted for approval.");
                        successResponse.setVariable(VarList.RSP_SUCCESS);

                        return successResponse;
                    } else {
                        throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
                    }
                } else {
                    throw new ErrorException("You are not a instructor to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse disapproveRequestedCourse(DisApproveCourseRequest disApproveCourseRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 3) {
                    Course course = courseRepository.getCourseByCode(disApproveCourseRequest.getCode());
                    if (course != null) {
                        ApprovalType approvalType = approvalTypeRepository.getApprovalTypeById(4);
                        if (course.getApprovalType().getId() != approvalType.getId()) {
                            course.setApprovalType(approvalType);
                            course.setComment(disApproveCourseRequest.getComment());
                            courseRepository.save(course);

                            Notification notification = new Notification();
                            notification.setNotificationCode(UUID.randomUUID().toString());
                            notification.setNotification("Your \"" + course.getCourseTitle() + "\" course has been disapproved. Please refer email for further details.");
                            notification.setNotificationTime(new Date());
                            notification.setGeneralUserProfile(course.getInstructorId().getGeneralUserProfile());
                            notification.setRead(false);
                            notificationRepository.save(notification);

                            Properties properties = EmailConfig.getEmailProperties(profile.getFirstName() + " " + profile.getLastName(), "Course Submission Disapproved");
                            properties.put("courseTitle", course.getCourseTitle());
                            properties.put("comment", course.getComment());
                            try {
                                EmailSender emailSender = new EmailSender();
                                emailSender.sendEmail("CourseDisapproveMessage", course.getInstructorId().getGeneralUserProfile().getEmail(), (String) properties.getProperty("from"), (String) properties.get("subject"), properties);

                            } catch (MessagingException e) {
                                e.printStackTrace();
                            }
                        }
                        successResponse.setMessage("The approval type was successfully updated to disapproved");
                        successResponse.setVariable(VarList.RSP_SUCCESS);
                        return successResponse;
                    } else {
                        throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
                    }
                } else {
                    throw new ErrorException("You are not a admin to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public String getCurrentApprovalType(String courseCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    Course course = courseRepository.getCourseByCode(courseCode);
                    if (course != null) {
                        String currentApprovalType = course.getApprovalType().getName();
                        return currentApprovalType;
                    } else {
                        throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
                    }
                } else {
                    throw new ErrorException("You are not a instructor to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse approveRequestedCourse(String courseCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 3) {
                    Course course = courseRepository.getCourseByCode(courseCode);
                    if (course != null) {

                        ApprovalType approvalType = approvalTypeRepository.getApprovalTypeById(5);

                        if (course.getApprovalType().getId() != approvalType.getId()) {

                            course.setApprovalType(approvalType);
                            courseRepository.save(course);

                            Notification notification = new Notification();
                            notification.setNotificationCode(UUID.randomUUID().toString());
                            notification.setNotification("Your \"" + course.getCourseTitle() + "\" course has been approved");
                            notification.setNotificationTime(new Date());
                            notification.setGeneralUserProfile(course.getInstructorId().getGeneralUserProfile());
                            notification.setRead(false);
                            notificationRepository.save(notification);

                            Properties properties = EmailConfig.getEmailProperties(profile.getFirstName() + " " + profile.getLastName(), "Course Approval");
                            properties.put("courseTitle", course.getCourseTitle());
                            try {
                                EmailSender emailSender = new EmailSender();
                                emailSender.sendEmail("CourseApproveMessage", course.getInstructorId().getGeneralUserProfile().getEmail(), (String) properties.getProperty("from"), (String) properties.get("subject"), properties);

                            } catch (MessagingException e) {
                                e.printStackTrace();
                            }
                        }

                        successResponse.setMessage("The approval type was successfully updated to approved");
                        successResponse.setVariable(VarList.RSP_SUCCESS);
                        return successResponse;
                    } else {
                        throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
                    }
                } else {
                    throw new ErrorException("You are not a admin to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }


    @Override
    public String getCourseIsOwned(String courseCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    Course course = courseRepository.getCourseByCode(courseCode);
                    if (course != null) {
                        String ownership = course.getIsOwned().toString();
                        return ownership;
                    } else {
                        throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
                    }
                } else {
                    throw new ErrorException("You are not a instructor to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public double courseCurriculumProgress(String courseCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    Course course = courseRepository.getCourseByCode(courseCode);
                    if (course != null) {
                        double progress = 0;
                        double count = 100 / 5;

                        CourseComplete courseComplete = courseCompleteRepository.getCourseCompleteByCourse(course);

                        if (courseComplete != null) {
                            if (courseComplete.getCourseLandingPage() != null && courseComplete.getCourseLandingPage() == 1) {
                                progress = progress + count;
                            }
                            if (courseComplete.getCourseMessages() != null && courseComplete.getCourseMessages() == 1) {
                                progress = progress + count;
                            }
                            if (courseComplete.getCurriculum() != null && courseComplete.getCurriculum() == 1) {
                                progress = progress + count;
                            }
                            if (courseComplete.getIntendedLearners() != null && courseComplete.getIntendedLearners() == 1) {
                                progress = progress + count;
                            }
                            if (courseComplete.getPricing() != null && courseComplete.getPricing() == 1) {
                                progress = progress + count;
                            }
//                            if (courseComplete.getPromotions() != null && courseComplete.getPromotions() == 1) {
//                                progress = progress + count;
//                            }
                        }
//                        if (progress == 16) {
//                            progress = 16.5;
//                        }
//                        if (progress == 32) {
//                            progress = 33;
//                        }
//                        if (progress == 48) {
//                            progress = 50;
//                        }
//                        if (progress == 64) {
//                            progress = 66.5;
//                        }
//                        if (progress == 80) {
//                            progress = 83;
//                        }
//                        if (progress == 96) {
//                            progress = 100;
//                        }
                        return progress;

                    } else {
                        throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
                    }
                } else {
                    throw new ErrorException("You are not a instructor to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }


    @Override
    public String getCourseComment(String courseCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    Course course = courseRepository.getCourseByCode(courseCode);
                    if (course != null) {
                        String comment = course.getComment();
                        return comment;
                    } else {
                        throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
                    }
                } else {
                    throw new ErrorException("You are not a instructor to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public GetCoursesDataResponse getCourseByStudent(String courseCode) {

        if (courseCode == null || courseCode.isEmpty()) {
            throw new ErrorException("Please add a course code", VarList.RSP_NO_DATA_FOUND);
        }
        Course course = courseRepository.getCourseByCode(courseCode);
        if (course == null) {
            throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
        }
        CourseLandingPage courseLandingPage = courseLandingPageRepository.getCourseLandingPageByCourse(course);
        if (courseLandingPage == null) {
            throw new ErrorException("There is no course on the course landing page", VarList.RSP_NO_DATA_FOUND);
        }
        if (course.getApprovalType().getId() != 5) {
            throw new ErrorException("Unapproved course", VarList.RSP_NO_DATA_FOUND);
        }
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

        ExternalCourse externalCourse = externalCourseRepository.getExternalCourseByCourse(course);
        if (externalCourse != null) {
            ExternalCourseResponse externalCourseResponse = new ExternalCourseResponse();
            externalCourseResponse.setExternalNumberOfStudent(externalCourse.getExternalNumberOfStudents());
            externalCourseResponse.setExternalRating(externalCourse.getExternalRating());
            externalCourseResponse.setAnyComments((externalCourse.getAnyComment() == null || externalCourse.getAnyComment().isEmpty()) ? "" : externalCourseResponse.getAnyComments());
            externalCourseResponse.setLinkToCourse((externalCourse.getLinkToCourse() == null || externalCourse.getLinkToCourse().isEmpty()) ? "" : externalCourse.getLinkToCourse());
            getCoursesDataResponse.setExternalCourseDetails(externalCourseResponse);
        }
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
        List<OrderHasCourse> orderHasCourses = orderHasCourseRepository.getOrderHasCoursesByCourse(course);

        int studentCount = 0;

        Map<String, Integer> courseStudentCounts = new HashMap<>();
        courseStudentCounts.put("1afabb42-d9d5-44a1-abf1-9bc4917e5e44", orderHasCourses.size() + 11039);
        courseStudentCounts.put("90c1e353-358d-46fb-b6c4-8c57007245ea", orderHasCourses.size() + 574);
        courseStudentCounts.put("e819dea8-cd7f-4eb4-b1b4-e8db6fc63c87", orderHasCourses.size() + 551);
        courseStudentCounts.put("bf2f01d3-9ef7-4c91-973d-2debdd2e5aaa", orderHasCourses.size() + 548);
        courseStudentCounts.put("dc4ffca5-41ae-4b35-a778-d530a4a68aa6", orderHasCourses.size() + 537);
        courseStudentCounts.put("51bd9e0d-49b3-4004-8413-81f5fed53dd2", orderHasCourses.size() + 565);
        courseStudentCounts.put("9d93acf7-5543-45fb-9f18-c3e57b5fbe69", orderHasCourses.size() + 11864);
        courseStudentCounts.put("0769d057-797d-475c-84ba-c11fd6651c04", orderHasCourses.size() + 11755);

        studentCount = courseStudentCounts.getOrDefault(courseCode, orderHasCourses.size());

        getCoursesDataResponse.setStudent(studentCount);
        getCoursesDataResponse.setCategory(course.getCourseCategory().getName());
        getCoursesDataResponse.setCategory_link_name(course.getCourseCategory().getLinkName());
        CourseSubCategory courseSubCategory = courseSubCategoryRepository.getCourseSubCategoryById(courseLandingPage.getSubcategory().getId());
        if (courseSubCategory == null) {
            throw new ErrorException("Course sub category not found", VarList.RSP_NO_DATA_FOUND);
        }
        getCoursesDataResponse.setSub_category(courseSubCategory.getName());
        getCoursesDataResponse.setSub_category_link_name(courseSubCategory.getSubLinkName());
        Topic topic = topicRepository.getTopicById(courseLandingPage.getTopic().getId());
        if (topic == null) {
            throw new ErrorException("Course topic not found", VarList.RSP_NO_DATA_FOUND);
        }
        getCoursesDataResponse.setTopic(topic.getTopic());
        getCoursesDataResponse.setTopic_link_name(topic.getLinkName());
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
        getCoursesDataResponse.setVideoId((courseLandingPage != null && !courseLandingPage.getPromotionalVideoUrl().isEmpty()) ? courseLandingPage.getPromotionalVideoUrl() : "");
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
            getCourseContentResponse.setArrangedNo(courseSectionObj.getArrangedNo());
            int on_of_lectures = 0;
            int on_of_quize = 0;
            int on_of_assignment = 0;
            int on_of_codingExercise = 0;
            int on_of_practiceTest = 0;
            List<SectionCurriculumItem> articleCounts = sectionCurriculumItemRepository.getSectionCurriculumItemsByCourseSection(courseSectionObj);
            List<GetSectionCurriculumItemResponse> getSectionCurriculumItemResponses = new ArrayList<>();
            for (SectionCurriculumItem item : articleCounts) {
                if (item.getIsDelete() == null || item.getIsDelete() == 0) {
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
                        getCurriculumItemFilesResponse.setVideoLength(curriculumItemFile.getVideoLength() == null ? 0 : curriculumItemFile.getVideoLength());
                        getCurriculumItemFilesResponse.setPreviewVideo(curriculumItemFile.getIsPreviewVideo() == null || curriculumItemFile.getIsPreviewVideo() == 0 ? false : true);
                        getCurriculumItemFilesResponses.add(getCurriculumItemFilesResponse);
                    }
                    getSectionCurriculumItemResponse.setGet_CurriculumItem_File(getCurriculumItemFilesResponses);
                }
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

        return getCoursesDataResponse;


    }

    @Override
    public String getCategorynameBylinkName(String linkName) {
        CourseCategory courseCategory = courseCategoryRepository.getCourseCategoryByLinkName(linkName);
        if (courseCategory == null) {
            throw new ErrorException("The course category name could not be found", VarList.RSP_NO_DATA_FOUND);
        }
        return courseCategory.getName();
    }

    @Override
    public List<String> getTopicBylinkName(String linkName) {
        List<CourseSubCategory> subCategoryList = courseSubCategoryRepository.getCourseSubCategoriesByCourseCategory(courseCategoryRepository.getCourseCategoryByLinkName(linkName));
        if (subCategoryList.size() != 0) {
            List<String> topicArray = new ArrayList<>();
            for (CourseSubCategory subCategory : subCategoryList) {
                List<Topic> topicList = topicRepository.getTopicsBySubCategory(subCategory);
                if (topicList.size() != 0) {
                    for (Topic topic : topicList) {
                        topicArray.add(topic.getTopic());
                    }
                } else {
                    throw new ErrorException("Topics not found according to sub-category", VarList.RSP_NO_DATA_FOUND);
                }
            }
            return topicArray;
        } else {
            throw new ErrorException("Sub categories not found according to category", VarList.RSP_NO_DATA_FOUND);
        }
    }

    public List<GetCoursesDataResponse> getCoursesData() {
        List<Course> courseList = courseRepository.findAll();
        if (courseList.size() == 0) {
            throw new ErrorException("There are no courses", VarList.RSP_NO_DATA_FOUND);
        }
        List<GetCoursesDataResponse> getCoursesDataResponses = new ArrayList<>();
        GetCoursesDataResponse getCoursesDataResponse;
        CourseLandingPage courseLandingPage;
        for (Course courseobj : courseList) {
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
                    getCoursesDataResponse.setCategory_link_name(courseLandingPage.getCourse().getCourseCategory().getLinkName());
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
    public List<GetCoursesDataResponse> getNewCourses(String linkName) {

        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        Date oneMonthAgoDate = Date.from(oneMonthAgo.atZone(ZoneId.systemDefault()).toInstant());

//        List<Course> courseList = courseRepository.findByCreatedDateAfter(oneWeekAgoDate);
        List<Course> courseList = courseRepository.getCourseByCourseCategoryAndCreatedDateAfter(courseCategoryRepository.getCourseCategoryByLinkName(linkName), oneMonthAgoDate);
        if (courseList.size() == 0) {
            throw new ErrorException("There are no courses", VarList.RSP_NO_DATA_FOUND);
        }
        List<GetCoursesDataResponse> getCoursesDataResponses = new ArrayList<>();
        GetCoursesDataResponse getCoursesDataResponse;
        CourseLandingPage courseLandingPage;
        for (Course courseobj : courseList) {
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
                    getCoursesDataResponse.setCertificate("yes");
                    getCoursesDataResponse.setVideoId(courseobj.getTest_video());
                    getCoursesDataResponse.setSub_title((courseLandingPage != null) ? courseLandingPage.getSubTitle() : null);
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
    public List<GetCoursesDataResponse> getMostPopularCourses(String linkName) {
        List<Course> courses = courseRepository.getCourseByCourseCategoryOrderByBuyCountDesc(courseCategoryRepository.getCourseCategoryByLinkName(linkName));
        if (courses == null) {
            throw new ErrorException("The courses could not be found", VarList.RSP_NO_DATA_FOUND);
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
    public GetCategoryAndSubCategorynameResponse getCategoryAndSubCategorynameBylinkName(String linkSubName) {
        CourseSubCategory courseSubCategory = courseSubCategoryRepository.getCourseSubCategoryBySubLinkName(linkSubName);
        if (courseSubCategory == null) {
            throw new ErrorException("The course Sub category name could not be found", VarList.RSP_NO_DATA_FOUND);
        }
        GetCategoryAndSubCategorynameResponse getCategoryAndSubCategorynameResponse = new GetCategoryAndSubCategorynameResponse();
        getCategoryAndSubCategorynameResponse.setCategoryName(courseSubCategory.getCourseCategory().getName());
        getCategoryAndSubCategorynameResponse.setSubCategoryName(courseSubCategory.getName());
        return getCategoryAndSubCategorynameResponse;
    }

    @Override
    public List<GetSubCategoryDetailsResponse> getSubCategoryByCourseLinkName(String linkName) {
        CourseCategory courseCategory = courseCategoryRepository.getCourseCategoryByLinkName(linkName);
        if (courseCategory == null) {
            throw new ErrorException("The course category name could not be found", VarList.RSP_NO_DATA_FOUND);
        }
        List<CourseSubCategory> subCategoryList = courseSubCategoryRepository.getCourseSubCategoriesByCourseCategory(courseCategory);
        List<GetSubCategoryDetailsResponse> getSubCategoryDetailsResponses = new ArrayList<>();
        for (CourseSubCategory courseSubCategory : subCategoryList) {
            GetSubCategoryDetailsResponse getSubCategoryDetailsResponse = new GetSubCategoryDetailsResponse();
            getSubCategoryDetailsResponse.setId(courseSubCategory.getId());
            getSubCategoryDetailsResponse.setSubCategory(courseSubCategory.getName());
            getSubCategoryDetailsResponse.setSubLinkName(courseSubCategory.getSubLinkName());
            getSubCategoryDetailsResponses.add(getSubCategoryDetailsResponse);
        }
        return getSubCategoryDetailsResponses;
    }

    @Override
    public List<GetCoursesDataResponse> getTrendingByCourseLinkName(String linkName) {

        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        Date oneMonthAgoDate = Date.from(oneMonthAgo.atZone(ZoneId.systemDefault()).toInstant());

        List<Course> courses = courseRepository.getCourseByCourseCategoryAndCreatedDateAfterOrderByBuyCountDesc(courseCategoryRepository.getCourseCategoryByLinkName(linkName), oneMonthAgoDate);
        if (courses == null) {
            throw new ErrorException("The courses could not be found", VarList.RSP_NO_DATA_FOUND);
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
    public GetInstructorDetailsResponse getInstructorDetails(String userCode) {
        GeneralUserProfile generalUserProfile = generalUserProfileRepository.getGeneralUserProfileByUserCode(userCode);
        if (generalUserProfile == null) {
            throw new ErrorException("The instructor could not be found", VarList.RSP_NO_DATA_FOUND);
        }
        InstructorProfile instructorProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(generalUserProfile);
        if (generalUserProfile == null) {
            throw new ErrorException("Instructor profile not found", VarList.RSP_NO_DATA_FOUND);
        }

        List<Course> courses = courseRepository.getCourseByInstructorId(instructorProfile);
        int totalStudentCount = 0;
        int totalReviews = 0;
        double totalRatingCount = 0;
        for (Course course : courses) {
            totalStudentCount += orderHasCourseRepository.getOrderHasCoursesByCourse(course).size();
            List<Review> reviews = reviewRepository.getReviewsByCourse(course);
            totalReviews += reviews.size();
            for (Review review : reviews) {
                totalRatingCount += review.getRating();
            }
        }
        GetInstructorDetailsResponse getInstructorDetailsResponse = new GetInstructorDetailsResponse();
        getInstructorDetailsResponse.setName(generalUserProfile.getFirstName() + " " + generalUserProfile.getLastName());
        getInstructorDetailsResponse.setProfileImage(generalUserProfile.getProfileImg());
        getInstructorDetailsResponse.setHeadline(instructorProfile.getHeadline());
        getInstructorDetailsResponse.setTotalStudents(totalStudentCount);
        getInstructorDetailsResponse.setReviews(totalReviews);
        getInstructorDetailsResponse.setAbout(instructorProfile.getBiography());
        getInstructorDetailsResponse.setEmail(generalUserProfile.getEmail());
        getInstructorDetailsResponse.setSecondaryEmail(instructorProfile.getEmail());
        getInstructorDetailsResponse.setWebsite(instructorProfile.getWebsite());
        getInstructorDetailsResponse.setTwitter(instructorProfile.getTwitter());
        getInstructorDetailsResponse.setFacebook(instructorProfile.getFacebook());
        getInstructorDetailsResponse.setLinkedin(instructorProfile.getLinkedin());
        getInstructorDetailsResponse.setYoutube(instructorProfile.getYoutube());
        getInstructorDetailsResponse.setRating(totalRatingCount / totalReviews);

        return getInstructorDetailsResponse;
    }

    @Override
    public List<GetPopularInstructorsResponse> getPopularInstructors(String linkName) {
        List<Course> courses = courseRepository.findFirst12ByCourseCategoryOrderByBuyCountDesc(courseCategoryRepository.getCourseCategoryByLinkName(linkName));
        if (courses.size() == 0) {
            throw new ErrorException("The courses could not be found", VarList.RSP_NO_DATA_FOUND);
        }

        Map<Integer, GeneralUserProfile> generalUserProfiles = new HashMap<>();
        Set<GeneralUserProfile> uniqueProfiles = new HashSet<>();
        List<GetPopularInstructorsResponse> getPopularInstructorsResponses = new ArrayList<>();

        int counter = 0;

        for (Course course : courses) {
            GeneralUserProfile userProfile = course.getInstructorId().getGeneralUserProfile();
            if (uniqueProfiles.add(userProfile)) {
                generalUserProfiles.put(counter++, userProfile);
            }
        }

        for (int i = 0; i < generalUserProfiles.size(); i++) {

            int courseCount = 0;
            GetPopularInstructorsResponse getPopularInstructorsResponse = new GetPopularInstructorsResponse();
            GeneralUserProfile profile = generalUserProfiles.get(i);
            InstructorProfile instructorProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(profile);
            getPopularInstructorsResponse.setName(profile.getFirstName() + " " + profile.getLastName());
            getPopularInstructorsResponse.setUserCode(profile.getUserCode());
            getPopularInstructorsResponse.setProfile_img(profile.getProfileImg());
            getPopularInstructorsResponse.setAbout(instructorProfile.getBiography());

            getPopularInstructorsResponse.setRating(0);
            getPopularInstructorsResponse.setStudentsCount(0);
            getPopularInstructorsResponse.setCoursesCount(courseRepository.getCourseByInstructorId(instructorProfile).size());
            getPopularInstructorsResponses.add(getPopularInstructorsResponse);
        }

        return getPopularInstructorsResponses;
    }

    @Override
    public List<GetAllcoursesViewResponse> getAllcoursesViewByLinkName(String linkName) {
        List<Course> courses = courseRepository.getCourseByCourseCategoryOrderByBuyCountDesc(courseCategoryRepository.getCourseCategoryByLinkName(linkName));
        if (courses == null) {
            throw new ErrorException("The courses could not be found", VarList.RSP_NO_DATA_FOUND);
        }
        List<GetAllcoursesViewResponse> getAllcoursesViewResponses = new ArrayList<>();
        for (Course course : courses) {
            GetAllcoursesViewResponse getAllcoursesViewResponse = new GetAllcoursesViewResponse();
            getAllcoursesViewResponse.setCourseCode(course.getCode());
            getAllcoursesViewResponse.setCourseImg(course.getImg());
            getAllcoursesViewResponse.setTitle(course.getCourseTitle());

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

            getAllcoursesViewResponse.setCourse_prices(response);
            getAllcoursesViewResponse.setRating(0);
            CourseLandingPage courseLandingPage = courseLandingPageRepository.getCourseLandingPageByCourse(course);
            if (courseLandingPage != null) {
                getAllcoursesViewResponse.setDescription(courseLandingPage.getDescription());
            }
            getAllcoursesViewResponse.setLessonsCount(courseSectionRepository.getCourseSectionByCourse(course).size());
            getAllcoursesViewResponse.setStudentCount(orderHasCourseRepository.getOrderHasCoursesByCourse(course).size());
            getAllcoursesViewResponses.add(getAllcoursesViewResponse);
        }
        return getAllcoursesViewResponses;
    }

    @Override
    public List<GetTopicsResponse> getPopularTopicByLinkName(String linkName) {
        List<CourseSubCategory> subCategoryList = courseSubCategoryRepository.getCourseSubCategoriesByCourseCategory(courseCategoryRepository.getCourseCategoryByLinkName(linkName));
        if (subCategoryList == null) {
            throw new ErrorException("There are no course titles", VarList.RSP_NO_DATA_FOUND);
        }
        List<GetTopicsResponse> getTopicsResponses = new ArrayList<>();
        for (CourseSubCategory courseSubCategory : subCategoryList) {
            List<Topic> topics = topicRepository.getTopicsBySubCategory(courseSubCategory);
            if (topics == null) {
                throw new ErrorException("There are no topics", VarList.RSP_NO_DATA_FOUND);
            }
            for (Topic topic : topics) {
                GetTopicsResponse getTopicsResponse = new GetTopicsResponse();
                getTopicsResponse.setTopic(topic.getTopic());
                getTopicsResponse.setTopicLinkName(topic.getLinkName());
                getTopicsResponses.add(getTopicsResponse);
            }
        }
        return getTopicsResponses;
    }

    @Override
    public List<GetAllcoursesViewResponse> getAllcoursesViewByInstructor(String userCode) {
        List<Course> courses = courseRepository.getCourseByInstructorId(instructorProfileRepository.getInstructorProfileByGeneralUserProfile(generalUserProfileRepository.getGeneralUserProfileByUserCode(userCode)));
        if (courses == null) {
            throw new ErrorException("There are no courses", VarList.RSP_NO_DATA_FOUND);
        }
        List<GetAllcoursesViewResponse> getAllcoursesViewResponses = new ArrayList<>();
        for (Course course : courses) {
            GetAllcoursesViewResponse getAllcoursesViewResponse = new GetAllcoursesViewResponse();
            getAllcoursesViewResponse.setCourseCode(course.getCode());
            getAllcoursesViewResponse.setCourseImg(course.getImg());
            getAllcoursesViewResponse.setTitle(course.getCourseTitle());

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

            getAllcoursesViewResponse.setCourse_prices(response);
            getAllcoursesViewResponse.setRating(0);
            CourseLandingPage courseLandingPage = courseLandingPageRepository.getCourseLandingPageByCourse(course);
            if (courseLandingPage != null) {
                getAllcoursesViewResponse.setDescription(courseLandingPage.getDescription());
            }
            getAllcoursesViewResponse.setLessonsCount(courseSectionRepository.getCourseSectionByCourse(course).size());
            getAllcoursesViewResponse.setStudentCount(orderHasCourseRepository.getOrderHasCoursesByCourse(course).size());
            getAllcoursesViewResponses.add(getAllcoursesViewResponse);
        }
        return getAllcoursesViewResponses;
    }

    @Override
    public List<GetCoursesDataResponse> getNewCoursesBySubCategory(String sublinkName) {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        Date oneMonthAgoDate = Date.from(oneMonthAgo.atZone(ZoneId.systemDefault()).toInstant());

        CourseSubCategory courseSubCategory = courseSubCategoryRepository.getCourseSubCategoryBySubLinkName(sublinkName);
        if (courseSubCategory == null) {
            throw new ErrorException("Invalid sub category link name", VarList.RSP_NO_DATA_FOUND);
        }
        List<Course> courseList = courseRepository.getCourseByCourseCategoryAndCreatedDateAfter(courseCategoryRepository.getCourseCategoryByLinkName(courseSubCategory.getCourseCategory().getLinkName()), oneMonthAgoDate);
        if (courseList.size() == 0) {
            throw new ErrorException("There are no courses", VarList.RSP_NO_DATA_FOUND);
        }
        List<GetCoursesDataResponse> getCoursesDataResponses = new ArrayList<>();
        GetCoursesDataResponse getCoursesDataResponse;
        CourseLandingPage courseLandingPage;
        for (Course courseobj : courseList) {
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
                    getCoursesDataResponse.setCategory_link_name(courseLandingPage.getCourse().getCourseCategory().getLinkName());
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
    public List<GetCoursesDataResponse> getMostPopularCoursesBySubCategory(String sublinkName) {
        CourseSubCategory courseSubCategory = courseSubCategoryRepository.getCourseSubCategoryBySubLinkName(sublinkName);
        if (courseSubCategory == null) {
            throw new ErrorException("Invalid sub category link name", VarList.RSP_NO_DATA_FOUND);
        }
        List<Course> courses = courseRepository.getCourseByCourseCategoryAndApprovalTypeIdOrderByBuyCountDesc(courseCategoryRepository.getCourseCategoryByLinkName(courseSubCategory.getCourseCategory().getLinkName()), 5);
        if (courses == null) {
            throw new ErrorException("The courses could not be found", VarList.RSP_NO_DATA_FOUND);
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
                getCoursesDataResponse.setImg(courseobj.getImg());
                getCoursesDataResponse.setIsPaid(courseobj.getIsPaid() == 2);
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
    public List<GetCoursesDataResponse> getTrendingBySubCategory(String sublinkName) {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        Date oneMonthAgoDate = Date.from(oneMonthAgo.atZone(ZoneId.systemDefault()).toInstant());

        CourseSubCategory courseSubCategory = courseSubCategoryRepository.getCourseSubCategoryBySubLinkName(sublinkName);
        if (courseSubCategory == null) {
            throw new ErrorException("Invalid sub category link name", VarList.RSP_NO_DATA_FOUND);
        }

        List<Course> courses = courseRepository.getCourseByCourseCategoryAndApprovalTypeIdAndCreatedDateAfterOrderByBuyCountDesc(courseCategoryRepository.getCourseCategoryByLinkName(courseSubCategory.getCourseCategory().getLinkName()), 5, oneMonthAgoDate);
        if (courses == null) {
            throw new ErrorException("The courses could not be found", VarList.RSP_NO_DATA_FOUND);
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
    public List<GetPopularInstructorsResponse> getPopularInstructorsBySubCategory(String sublinkName) {
        CourseSubCategory courseSubCategory = courseSubCategoryRepository.getCourseSubCategoryBySubLinkName(sublinkName);
        if (courseSubCategory == null) {
            throw new ErrorException("Invalid sub category link name", VarList.RSP_NO_DATA_FOUND);
        }
        List<Course> courses = courseRepository.getCourseByCourseCategoryOrderByBuyCountDesc(courseCategoryRepository.getCourseCategoryByLinkName(courseSubCategory.getCourseCategory().getLinkName()));
        if (courses == null) {
            throw new ErrorException("The courses could not be found", VarList.RSP_NO_DATA_FOUND);
        }

        Map<Integer, GeneralUserProfile> generalUserProfiles = new HashMap<>();
        Set<GeneralUserProfile> uniqueProfiles = new HashSet<>();
        List<GetPopularInstructorsResponse> getPopularInstructorsResponses = new ArrayList<>();

        int counter = 0;

        for (Course course : courses) {
            GeneralUserProfile userProfile = course.getInstructorId().getGeneralUserProfile();
            if (uniqueProfiles.add(userProfile)) {
                generalUserProfiles.put(counter++, userProfile);
            }
        }
        for (int i = 0; i < generalUserProfiles.size(); i++) {
            int courseCount = 0;
            GetPopularInstructorsResponse getPopularInstructorsResponse = new GetPopularInstructorsResponse();
            GeneralUserProfile profile = generalUserProfiles.get(i);
            InstructorProfile instructorProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(profile);
            getPopularInstructorsResponse.setName(profile.getFirstName() + " " + profile.getLastName());
            getPopularInstructorsResponse.setUserCode(profile.getUserCode());
            getPopularInstructorsResponse.setProfile_img(profile.getProfileImg());
            getPopularInstructorsResponse.setAbout(instructorProfile.getBiography());
            getPopularInstructorsResponse.setRating(0);
            getPopularInstructorsResponse.setStudentsCount(0);
            getPopularInstructorsResponse.setCoursesCount(courseRepository.getCourseByInstructorId(instructorProfile).size());
            getPopularInstructorsResponses.add(getPopularInstructorsResponse);
        }

        return getPopularInstructorsResponses;
    }

    @Override
    public List<GetAllcoursesViewResponse> getAllcoursesViewBySubLinkName(String sublinkName) {
        CourseSubCategory courseSubCategory = courseSubCategoryRepository.getCourseSubCategoryBySubLinkName(sublinkName);
        if (courseSubCategory == null) {
            throw new ErrorException("Invalid Sub category link name", VarList.RSP_NO_DATA_FOUND);
        }
        List<Course> courses = courseRepository.getCourseByCourseCategoryOrderByBuyCountDesc(courseCategoryRepository.getCourseCategoryByLinkName(courseSubCategory.getCourseCategory().getLinkName()));
        if (courses == null) {
            throw new ErrorException("The courses could not be found", VarList.RSP_NO_DATA_FOUND);
        }
        List<GetAllcoursesViewResponse> getAllcoursesViewResponses = new ArrayList<>();
        for (Course course : courses) {
            GetAllcoursesViewResponse getAllcoursesViewResponse = new GetAllcoursesViewResponse();
            getAllcoursesViewResponse.setCourseCode(course.getCode());
            getAllcoursesViewResponse.setCourseImg(course.getImg());
            getAllcoursesViewResponse.setTitle(course.getCourseTitle());

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

            getAllcoursesViewResponse.setCourse_prices(response);
            getAllcoursesViewResponse.setRating(0);
            CourseLandingPage courseLandingPage = courseLandingPageRepository.getCourseLandingPageByCourse(course);
            if (courseLandingPage != null) {
                getAllcoursesViewResponse.setDescription(courseLandingPage.getDescription());
            }
            getAllcoursesViewResponse.setLessonsCount(courseSectionRepository.getCourseSectionByCourse(course).size());
            getAllcoursesViewResponse.setStudentCount(orderHasCourseRepository.getOrderHasCoursesByCourse(course).size());
            getAllcoursesViewResponses.add(getAllcoursesViewResponse);
        }
        return getAllcoursesViewResponses;
    }

    @Override
    public List<GetTopicsResponse> getPopularTopicBySubLinkName(String sublinkName) {
        CourseSubCategory courseSubCategoryinfo = courseSubCategoryRepository.getCourseSubCategoryBySubLinkName(sublinkName);
        if (courseSubCategoryinfo == null) {
            throw new ErrorException("Invalid sub category link name", VarList.RSP_NO_DATA_FOUND);
        }
        List<CourseSubCategory> subCategoryList = courseSubCategoryRepository.getCourseSubCategoriesByCourseCategory(courseCategoryRepository.getCourseCategoryByLinkName(courseSubCategoryinfo.getCourseCategory().getLinkName()));
        if (subCategoryList == null) {
            throw new ErrorException("There are no course titles", VarList.RSP_NO_DATA_FOUND);
        }
        List<GetTopicsResponse> getTopicsResponses = new ArrayList<>();
        for (CourseSubCategory courseSubCategory : subCategoryList) {
            List<Topic> topics = topicRepository.getTopicsBySubCategory(courseSubCategory);
            if (topics == null) {
                throw new ErrorException("There are no topics", VarList.RSP_NO_DATA_FOUND);
            }
            for (Topic topic : topics) {
                GetTopicsResponse getTopicsResponse = new GetTopicsResponse();
                getTopicsResponse.setTopic(topic.getTopic());
                getTopicsResponse.setTopicLinkName(topic.getLinkName());
                getTopicsResponses.add(getTopicsResponse);
            }
        }
        return getTopicsResponses;
    }

    @Override
    public List<GetCoursesDataResponse> getMostPopularCoursesByTopic(String linkName) {
        List<Topic> topics = topicRepository.getTopicsByLinkName(linkName);
        if (topics.size() != 0) {
            List<GetCoursesDataResponse> getCoursesDataResponses = new ArrayList<>();
            for (Topic topic : topics) {
                CourseCategory courseCategory = courseCategoryRepository.getCourseCategoryById(topic.getSubCategory().getCourseCategory().getId());
                if (courseCategory != null) {
                    List<Course> courses = courseRepository.getCourseByCourseCategoryAndApprovalTypeIdOrderByBuyCountDesc(courseCategory, 5);
                    if (courses == null) {
                        throw new ErrorException("The courses could not be found", VarList.RSP_NO_DATA_FOUND);
                    }

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
                            if (courseLandingPage == null) {
                                throw new ErrorException("There are no course landing pages related to courses", VarList.RSP_NO_DATA_FOUND);
                            }
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

                } else {
                    throw new ErrorException("Course category not found", VarList.RSP_NO_DATA_FOUND);
                }
            }
            return getCoursesDataResponses;
        } else {
            throw new ErrorException("Topics not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public List<GetCoursesDataResponse> getNewCoursesByTopic(String topicLinkName) {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        Date oneMonthAgoDate = Date.from(oneMonthAgo.atZone(ZoneId.systemDefault()).toInstant());

        List<Topic> topics = topicRepository.getTopicsByLinkName(topicLinkName);
        if (topics.size() != 0) {
            List<GetCoursesDataResponse> getCoursesDataResponses = new ArrayList<>();
            for (Topic topic : topics) {
                CourseCategory courseCategory = courseCategoryRepository.getCourseCategoryById(topic.getSubCategory().getCourseCategory().getId());
                if (courseCategory != null) {
                    List<Course> courses = courseRepository.getCourseByCourseCategoryAndCreatedDateAfter(courseCategory, oneMonthAgoDate);
                    if (courses == null) {
                        throw new ErrorException("The courses could not be found", VarList.RSP_NO_DATA_FOUND);
                    }

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
                            if (courseLandingPage == null) {
                                throw new ErrorException("There are no course landing pages related to courses", VarList.RSP_NO_DATA_FOUND);
                            }
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

                } else {
                    throw new ErrorException("Course category not found", VarList.RSP_NO_DATA_FOUND);
                }
            }
            return getCoursesDataResponses;
        } else {
            throw new ErrorException("Topics not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public List<GetCoursesDataResponse> getTrendingCoursesByTopic(String topicLinkName) {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        Date oneMonthAgoDate = Date.from(oneMonthAgo.atZone(ZoneId.systemDefault()).toInstant());

        List<Topic> topics = topicRepository.getTopicsByLinkName(topicLinkName);
        if (topics.size() != 0) {
            List<GetCoursesDataResponse> getCoursesDataResponses = new ArrayList<>();
            for (Topic topic : topics) {
                CourseCategory courseCategory = courseCategoryRepository.getCourseCategoryById(topic.getSubCategory().getCourseCategory().getId());
                if (courseCategory != null) {
                    List<Course> courses = courseRepository.getCourseByCourseCategoryAndCreatedDateAfterOrderByBuyCountDesc(courseCategory, oneMonthAgoDate);
                    if (courses == null) {
                        throw new ErrorException("The courses could not be found", VarList.RSP_NO_DATA_FOUND);
                    }

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
                            if (courseLandingPage == null) {
                                throw new ErrorException("There are no course landing pages related to courses", VarList.RSP_NO_DATA_FOUND);
                            }
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

                } else {
                    throw new ErrorException("Course category not found", VarList.RSP_NO_DATA_FOUND);
                }
            }
            return getCoursesDataResponses;
        } else {
            throw new ErrorException("Topics not found", VarList.RSP_NO_DATA_FOUND);
        }

    }

    @Override
    public List<GetCoursesDataResponse> getAllCoursesByInstructorCode(String userCode) {
        GeneralUserProfile profile = generalUserProfileRepository.getGeneralUserProfileByUserCode(userCode);
        if (profile != null) {
            if (profile.getGupType().getId() == 2) {
                if (profile.getIsActive() == 1) {
                    List<GetCoursesDataResponse> responsesList = new ArrayList<>();
                    InstructorProfile instructor = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(profile);
                    List<Course> courseList = courseRepository.getAllByInstructorId(instructor);
                    for (Course course : courseList) {
                        CourseLandingPage courseLandingPage = courseLandingPageRepository.getCourseLandingPageByCourse(course);
                        if (courseLandingPage != null) {

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
                                int studentCount = 500 + (int) (Math.random() * 500);
//                                getCoursesDataResponse.setStudent(studentCount);
                                getCoursesDataResponse.setCategory(course.getCourseCategory().getName());
                                getCoursesDataResponse.setCategory_link_name(course.getCourseCategory().getLinkName());
                                getCoursesDataResponse.setSub_category(courseLandingPage.getSubcategory().getName());
                                getCoursesDataResponse.setSub_category_link_name(courseLandingPage.getSubcategory().getSubLinkName());
                                getCoursesDataResponse.setTopic(courseLandingPage.getTopic().getTopic());
                                getCoursesDataResponse.setTopic_link_name(courseLandingPage.getTopic().getLinkName());
                                getCoursesDataResponse.setShort_desc((courseLandingPage != null) ? courseLandingPage.getDescription() : null);
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
                                getCoursesDataResponse.setEnrolled_count(orderHasCourseRepository.getOrderHasCoursesByCourse(course).size());
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

                                responsesList.add(getCoursesDataResponse);
                            }
                        }
                    }
                    return responsesList;

                } else {
                    throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("You are not an instructor", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public CheckCourseCompleteDetailsResponse checkCourseCompleteDetails(String courseCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {

                    Course course = courseRepository.getCourseByCode(courseCode);
                    if (course == null) {
                        throw new ErrorException("Course code is not valid", VarList.RSP_NO_DATA_FOUND);
                    }

                    if (!course.getInstructorId().getGeneralUserProfile().getUserCode().equals(profile.getUserCode())) {
                        throw new ErrorException("You cannot access this course because it is not yours", VarList.RSP_NO_DATA_FOUND);
                    }

                    CheckCourseCompleteDetailsResponse checkCourseCompleteDetailsResponse = new CheckCourseCompleteDetailsResponse();

                    CourseComplete courseComplete = courseCompleteRepository.getCourseCompleteByCourse(course);
                    if (courseComplete != null) {
                        if (courseComplete.getIntendedLearners() != null && courseComplete.getIntendedLearners() == 1) {
                            checkCourseCompleteDetailsResponse.setIntendedLearners(true);
                        }
                        if (courseComplete.getCurriculum() != null && courseComplete.getCurriculum() == 1) {
                            checkCourseCompleteDetailsResponse.setCurriculum(true);
                        }
                        if (courseComplete.getCourseLandingPage() != null && courseComplete.getCourseLandingPage() == 1) {
                            checkCourseCompleteDetailsResponse.setCourseLandingPage(true);
                        }
                        if (courseComplete.getCourseMessages() != null && courseComplete.getCourseMessages() == 1) {
                            checkCourseCompleteDetailsResponse.setCourseMessages(true);
                        }
                        if (courseComplete.getPromotions() != null && courseComplete.getPromotions() == 1) {
                            checkCourseCompleteDetailsResponse.setPromotions(true);
                        }
                        if (courseComplete.getPricing() != null && courseComplete.getPricing() == 1) {
                            checkCourseCompleteDetailsResponse.setPricing(true);
                        }
                    }

                    return checkCourseCompleteDetailsResponse;

                } else {
                    throw new ErrorException("You are not a instructor to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public CheckPaidCourseValidationResponse checkPaidCourseValidation(String courseCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {

                    Course course = courseRepository.getCourseByCode(courseCode);
                    if (course == null) {
                        throw new ErrorException("Course code is not valid", VarList.RSP_NO_DATA_FOUND);
                    }

                    if (!course.getInstructorId().getGeneralUserProfile().getUserCode().equals(profile.getUserCode())) {
                        throw new ErrorException("You cannot access this course because it is not yours", VarList.RSP_NO_DATA_FOUND);
                    }


                    int lectureCount = 0;

                    List<CourseSection> courseSections = courseSectionRepository.getCourseSectionByCourse(course);
                    for (CourseSection courseSection : courseSections) {
                        List<SectionCurriculumItem> sectionCurriculumItems = sectionCurriculumItemRepository.getSectionCurriculumItemsByCourseSection(courseSection);
                        for (SectionCurriculumItem sectionCurriculumItem : sectionCurriculumItems) {
                            if (sectionCurriculumItem.getCurriculumItemType().getId() == 1 && (sectionCurriculumItem.getIsDelete() == null || sectionCurriculumItem.getIsDelete() != 1)) {
                                lectureCount++;
                            }
                        }
                    }

                    InstructorProfile instructorProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(profile);

                    if (instructorProfile == null) {
                        throw new ErrorException("Instructor profile not found", VarList.RSP_NO_DATA_FOUND);
                    }

                    InstructorPayments instructorPayments = instructorPaymentsRepository.getInstructorPaymentsByInstructorProfile(instructorProfile);
                    boolean isComplete = false;
                    if (instructorPayments != null) {
                        boolean isPaypalComplete = instructorPayments.getPaypalEmail() != null && !instructorPayments.getPaypalEmail().isEmpty()
                                && instructorPayments.getPaypalUserName() != null && !instructorPayments.getPaypalUserName().isEmpty();

                        boolean isPayoneerComplete = instructorPayments.getPayoneerEmail() != null && !instructorPayments.getPayoneerEmail().isEmpty()
                                && instructorPayments.getPayoneerUserName() != null && !instructorPayments.getPayoneerUserName().isEmpty();
                        boolean isUkComplete = !instructorPayments.getAccountNumber().isEmpty() && !instructorPayments.getSort1().isEmpty()
                                && !instructorPayments.getSort2().isEmpty() && !instructorPayments.getSort3().isEmpty();

                        isComplete = isPaypalComplete || isPayoneerComplete || isUkComplete;

                    }

                    CheckPaidCourseValidationResponse checkPaidCourseValidationResponse = new CheckPaidCourseValidationResponse();
                    checkPaidCourseValidationResponse.setCourseLength(course.getCourseLength());
                    checkPaidCourseValidationResponse.setLectureCount(lectureCount);
                    checkPaidCourseValidationResponse.setPaymentDetails(isComplete);

                    return checkPaidCourseValidationResponse;


                } else {
                    throw new ErrorException("You are not a instructor to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public ReferralCodeValidationResponse checkReferralCodeValidation(String referralCode) {
        Course course = courseRepository.getCourseByReferralCode(referralCode);

        ReferralCodeValidationResponse response = new ReferralCodeValidationResponse();

        if (course == null) {
            response.setValidation(false);
            response.setMessage("Invalid referral code");
            response.setCourseCode("");
        } else {
            response.setValidation(true);
            response.setMessage("Valid referral code");
            response.setCourseCode(course.getCode());
        }


        return response;
    }

    @Override
    public List<GetCoursesDataResponse> getAllCoursesByAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() != 3) {
                    throw new ErrorException("You cannot access this process because you are not an admin", VarList.RSP_NO_DATA_FOUND);
                }
                List<Course> courses = courseRepository.findAll();
                List<GetCoursesDataResponse> getCoursesDataResponses = new ArrayList<>();
                for (Course course : courses) {
                    CourseLandingPage courseLandingPage = courseLandingPageRepository.getCourseLandingPageByCourse(course);
                    if (courseLandingPage != null) {
                        GetCoursesDataResponse getCoursesDataResponse = new GetCoursesDataResponse();
                        getCoursesDataResponse.setCourse_code(course.getCode());
                        getCoursesDataResponse.setCreated_date(course.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter));
                        getCoursesDataResponse.setId(course.getId());
                        getCoursesDataResponse.setImg(course.getImg());
                        getCoursesDataResponse.setDuration(Double.toString(course.getCourseLength()));
                        getCoursesDataResponse.setLevel((courseLandingPage != null) ? courseLandingPage.getCourseLevel().getName() : null);
                        getCoursesDataResponse.setTitle(course.getCourseTitle());
                        getCoursesDataResponse.setIsPaid(course.getIsPaid() == 2);
                        getCoursesDataResponse.setApprovalTypeId(course.getApprovalType().getId().toString());
                        List<Review> reviews = reviewRepository.getReviewsByCourse(course);
                        getCoursesDataResponse.setIsReview((reviews.size() > 0) ? true : false);
                        List<GetRatingResponse> getRatingResponses = new ArrayList<>();
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

                        for (Review review : reviews) {

                            GetRatingResponse getRatingResponse = new GetRatingResponse();
                            getRatingResponse.setEmail(review.getGeneralUserProfile().getEmail());
                            getRatingResponse.setName(review.getGeneralUserProfile().getFirstName() + " " + review.getGeneralUserProfile().getLastName());
                            getRatingResponse.setRating(review.getRating());
                            getRatingResponse.setDate(review.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter));
                            getRatingResponse.setImg(review.getOrderHasCourse().getOrder().getGeneralUserProfile().getProfileImg());
                            getRatingResponse.setComment(review.getComment() == null ? "" : review.getComment());
                            List<GetRatingResponse> getRatingResponsesList = new ArrayList<>();
                            if (!review.getGeneralUserProfile().getUserCode().equals(profile.getUserCode())) {
                                getRatingResponses.add(getRatingResponse);
                            } else {
                                getRatingResponsesList.add(getRatingResponse);
                            }
                            getCoursesDataResponse.setOwnReview(getRatingResponsesList);

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
                        getCoursesDataResponse.setRating_count(reviews.size());
                        getCoursesDataResponse.setRating(reviews.size() == 0 ? 0 : rating_val / reviews.size());

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
                        List<OrderHasCourse> orderHasCourses = orderHasCourseRepository.getOrderHasCoursesByCourse(course);
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
                        getCoursesDataResponse.setCurriculum_desc("Curriculum description is not added to the course");


                        int article_count = 0;
                        int video_count = 0;
                        int downloadable_resources_count = 0;

                        List<GetCourseContentResponse> getCourseContentResponses = new ArrayList<>();
                        Integer completedItemCount = 0;
                        Integer allItemCount = 0;

                        for (CourseSection courseSectionObj : courseSections) {
                            if (courseSectionObj.getIsDelete() == null || courseSectionObj.getIsDelete() == 0) {
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
                                    if (item.getIsDelete() == null || item.getIsDelete() == 0) {
                                        allItemCount++;
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
                                        getSectionCurriculumItemResponse.setCurriculumItemId(item.getId());
                                        getSectionCurriculumItemResponse.setArrangeNo(item.getArrangedNo() == null ? "" : item.getArrangedNo().toString());
                                        getSectionCurriculumItemResponse.setCurriculum_item_type(item.getCurriculumItemType().getName());
                                        getSectionCurriculumItemResponse.setCurriculumItemId(item.getId());


                                        Assignment assignment = assignmentRepository.getAssignmentBySectionCurriculumItem(item);
                                        List<GetAssignment> getAssignments = new ArrayList<>();
                                        if (assignment != null && (assignment.getIsDelete() == null || assignment.getIsDelete() == 0)) {
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
                                        if (codingExercise != null && (codingExercise.getIsDelete() == null || codingExercise.getIsDelete() == 0)) {
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
                                        if (practiceTest != null && (practiceTest.getIsDelete() == null || practiceTest.getIsDelete() == 0)) {
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
                                        List<GetCurriculumItemFilesResponse> getCurriculumItemFilesResponses = new ArrayList<>();

                                        List<CurriculumItemFile> curriculumItemFiles = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItem(item);
                                        for (CurriculumItemFile curriculumItemFile : curriculumItemFiles) {
                                            if (curriculumItemFile.getIsDelete() == null || curriculumItemFile.getIsDelete() == 0) {
                                                GetCurriculumItemFilesResponse getCurriculumItemFilesResponse = new GetCurriculumItemFilesResponse();
                                                if (curriculumItemFile.getCurriculumItemFileTypes().getId() == 3) {
                                                    video_count++;
                                                    getCurriculumItemFilesResponse.setVideoLength(curriculumItemFile.getVideoLength() != null ? curriculumItemFile.getVideoLength() : 0);
                                                } else if (curriculumItemFile.getCurriculumItemFileTypes().getId() == 1) {
                                                    downloadable_resources_count++;
                                                }
                                                getCurriculumItemFilesResponse.setTitle(curriculumItemFile.getTitle());
                                                getCurriculumItemFilesResponse.setUrl(curriculumItemFile.getUrl());
                                                getCurriculumItemFilesResponse.setCurriculum_item_file_type(curriculumItemFile.getCurriculumItemFileTypes().getName());
                                                getCurriculumItemFilesResponses.add(getCurriculumItemFilesResponse);
                                            }
                                        }
                                        getSectionCurriculumItemResponse.setGet_CurriculumItem_File(getCurriculumItemFilesResponses);
                                        getSectionCurriculumItemResponses.add(getSectionCurriculumItemResponse);
                                    }
                                }
                                getCourseContentResponse.setNo_of_lectures(on_of_lectures);
                                getCourseContentResponse.setNo_of_qize(on_of_quize);
                                getCourseContentResponse.setOn_of_assignment(on_of_assignment);
                                getCourseContentResponse.setOn_of_codingExercise(on_of_codingExercise);
                                getCourseContentResponse.setOn_of_practiceTest(on_of_practiceTest);
                                getCourseContentResponse.setSection_curriculum_item(getSectionCurriculumItemResponses);
                                getCourseContentResponses.add(getCourseContentResponse);
                            }
                        }
                        getCoursesDataResponse.setArticles_count(article_count);
                        getCoursesDataResponse.setNo_of_videos(video_count);
                        getCoursesDataResponse.setDownloadable_resources_count(downloadable_resources_count);
                        getCoursesDataResponse.setEnrolled_count(orderHasCourseRepository.getOrderHasCoursesByCourse(course).size());
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
                        getCoursesDataResponse.setAllItemsCount(allItemCount);
                        getCoursesDataResponse.setCompletedItemCount(completedItemCount);

                        getCoursesDataResponses.add(getCoursesDataResponse);
                    }
//                    else {
//                        throw new ErrorException("Course landing page not found related to course"+ course.getCode(), VarList.RSP_NO_DATA_FOUND);
//                    }
                }
                return getCoursesDataResponses;
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public int getAvailableCouponCountForThisMonth(String courseCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    Course course = courseRepository.getCourseByCode(courseCode);
                    if (course == null)
                        throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);

                    if (!course.getInstructorId().getGeneralUserProfile().equals(profile))
                        throw new ErrorException("You cannot access this course because it is not yours", VarList.RSP_NO_DATA_FOUND);

                    LocalDate localDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    int currentMonth = localDate.getMonthValue();

                    List<Coupon> couponList = couponRepository.getCouponByCourse(course);
                    int couponCountForThisMonth = 0;

                    for (Coupon coupon : couponList) {
                        Date couponStartDate = coupon.getStartDate();
                        LocalDate couponLocalDate = couponStartDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        int couponMonth = couponLocalDate.getMonthValue();

                        if (couponMonth == currentMonth)
                            couponCountForThisMonth++;

                    }

                    return couponCountForThisMonth;

                } else {
                    throw new ErrorException("You are not a instructor to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public GetExternalCourseLinkAndRatingsResponse getExternalCourseLinkAndRatings(String courseCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    Course course = courseRepository.getCourseByCode(courseCode);
                    if (course == null)
                        throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);

                    if (!course.getInstructorId().getGeneralUserProfile().equals(profile))
                        throw new ErrorException("You cannot access this course because it is not yours", VarList.RSP_NO_DATA_FOUND);

                    ExternalCourse externalCourse = externalCourseRepository.getExternalCourseByCourse(course);
                    GetExternalCourseLinkAndRatingsResponse getExternalCourseLinkAndRatingsResponse = new GetExternalCourseLinkAndRatingsResponse();
                    String linkToCourse = "";
                    double externalRating = 0;
                    long externalNumberOfStudents = 0;
                    String anyComments = "";
                    if (externalCourse != null) {
                        linkToCourse = externalCourse.getLinkToCourse();
                        externalRating = externalCourse.getExternalRating();
                        externalNumberOfStudents = externalCourse.getExternalNumberOfStudents();
                        anyComments = externalCourse.getAnyComment();
                    }
                    getExternalCourseLinkAndRatingsResponse.setLinkToCourse(linkToCourse);
                    getExternalCourseLinkAndRatingsResponse.setExternalRating(externalRating);
                    getExternalCourseLinkAndRatingsResponse.setExternalNumberOfStudents(externalNumberOfStudents);
                    getExternalCourseLinkAndRatingsResponse.setAnyComments(anyComments);
                    return getExternalCourseLinkAndRatingsResponse;

                } else {
                    throw new ErrorException("You are not a instructor to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }
}
