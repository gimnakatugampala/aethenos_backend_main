package lk.exon.aethenosapi.service.impl;

import lk.exon.aethenosapi.config.Config;
import lk.exon.aethenosapi.entity.*;
import lk.exon.aethenosapi.exception.ErrorException;
import lk.exon.aethenosapi.payload.request.*;
import lk.exon.aethenosapi.payload.response.*;
import lk.exon.aethenosapi.repository.*;
import lk.exon.aethenosapi.service.ManageCourseService;
import lk.exon.aethenosapi.utils.FileUploadUtil;
import lk.exon.aethenosapi.utils.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ManageCourseImpl implements ManageCourseService {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseKeywordRepository courseKeywordRepository;
    @Autowired
    private CourseLandingPageRepository courseLandingPageRepository;
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private CourseLevelRepository courseLevelRepository;
    @Autowired
    private CourseCategoryRepository courseCategoryRepository;
    @Autowired
    private CourseIntentedLearnerRepository courseIntentedLearnerRepository;
    @Autowired
    private IntendedLearnerTypeRepository intendedLearnerTypeRepository;
    @Autowired
    private CourseMessageRepository courseMessageRepository;
    @Autowired
    private PromotionCouponRepository promotionCouponRepository;
    @Autowired
    private PromotionTypeRepository promotionTypeRepository;
    @Autowired
    private PriceSetupRepository priceSetupRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private CoursePriceRepository coursePriceRepository;
    @Autowired
    private DiscountTypeRepository discountTypeRepository;
    @Autowired
    private CourseSectionRepository courseSectionRepository;
    @Autowired
    private CurriculumItemFileTypeRepository curriculumItemFileTypeRepository;
    @Autowired
    private SectionCurriculumItemRepository sectionCurriculumItemRepository;
    @Autowired
    private ApprovalTypeRepository approvalTypeRepository;
    @Autowired
    private CurriculumItemFileRepository curriculumItemFileRepository;
    @Autowired
    private CourseSubCategoryRepository courseSubCategoryRepository;
    @Autowired
    private UserProfileServiceImpl userProfileServiceImpl;
    @Autowired
    private CurriculumItemTypeRepository curriculumItemTypeRepository;
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
    private TopicRepository topicRepository;
    @Autowired
    private InstructorProfileRepository instructorProfileRepository;
    @Autowired
    private OrderHasCourseRepository orderHasCourseRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private CourseCompleteRepository courseCompleteRepository;
    @Autowired
    private ReadCurriculumItemRepository readCurriculumItemRepository;
    @Autowired
    private EuroCountryRepository euroCountryRepository;
    @Autowired
    private ExternalCourseRepository externalCourseRepository;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private SuccessResponse successResponse = new SuccessResponse();
    private GetDefaultPriceResponse response;
    @Autowired
    private PreviousViewRepository previousViewRepository;

    @Override
    public SuccessResponse saveCourseLandingPage(CourseLandingPageRequest courseLandingPageRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    final String course_code = courseLandingPageRequest.getCourseCode();
                    final String course_title = courseLandingPageRequest.getCourse_tile();
                    final String course_subtitle = courseLandingPageRequest.getCourse_subtitle();
                    final String course_description = courseLandingPageRequest.getDescription();
                    final int language = courseLandingPageRequest.getLanguage();
                    final int level = courseLandingPageRequest.getLevel();
                    final int category = courseLandingPageRequest.getCategory();
                    final int subcategory = courseLandingPageRequest.getSubcategory();
                    final String[] keywords = courseLandingPageRequest.getKeywords();
                    final MultipartFile image = courseLandingPageRequest.getCourse_image();
                    final String video = courseLandingPageRequest.getPromotional_video();
                    final Integer topic_id = courseLandingPageRequest.getTopic();
                    final String ownTopic = courseLandingPageRequest.getOwnTopic();

                    if (course_code == null || course_code.isEmpty()) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    } else if (course_title == null || course_title.isEmpty()) {
                        throw new ErrorException("Please add course's title", VarList.RSP_NO_DATA_FOUND);
                    } else if (course_subtitle.isEmpty()) {
                        throw new ErrorException("Please add course's subtitle", VarList.RSP_NO_DATA_FOUND);
                    } else if (course_description.isEmpty()) {
                        throw new ErrorException("Please add course's description", VarList.RSP_NO_DATA_FOUND);
                    } else if (level == 0) {
                        throw new ErrorException("Please select level", VarList.RSP_NO_DATA_FOUND);
                    } else if (category == 0) {
                        throw new ErrorException("Please select course's category", VarList.RSP_NO_DATA_FOUND);
                    } else if (subcategory == 0) {
                        throw new ErrorException("Please select course's subcategory", VarList.RSP_NO_DATA_FOUND);
                    } else if (topic_id == null || topic_id.toString().isEmpty() || topic_id == 0) {
                        if (ownTopic == null || ownTopic.isEmpty()) {
                            throw new ErrorException("Please select the topic or add your Own topic", VarList.RSP_NO_DATA_FOUND);
                        }
                    }
                    if (keywords == null || keywords.length < 5) {
                        throw new ErrorException("Please add at least 5 keywords", VarList.RSP_NO_DATA_FOUND);
                    }

                    Course course = courseRepository.findByCode(courseLandingPageRequest.getCourseCode());
                    if (course == null) {
                        throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
                    }

                    CourseCategory courseCategory = courseCategoryRepository.getCourseCategoryById(category);
                    if (courseCategory == null) {
                        throw new ErrorException("Invalid course category id", VarList.RSP_NO_DATA_FOUND);
                    }

                    Language languageObj = languageRepository.getLanguageById(language);
                    if (languageObj == null) {
                        throw new ErrorException("Invalid language id", VarList.RSP_NO_DATA_FOUND);
                    }

                    CourseLevel courseLevel = courseLevelRepository.getCourseLevelById(level);
                    if (courseLevel == null) {
                        throw new ErrorException("Invalid course level id", VarList.RSP_NO_DATA_FOUND);
                    }
                    CourseSubCategory courseSubCategory = courseSubCategoryRepository.getCourseSubCategoryById(subcategory);
                    if (courseSubCategory == null) {
                        throw new ErrorException("Invalid course sub category id", VarList.RSP_NO_DATA_FOUND);
                    }

                    Topic topic;

                    if (topic_id != null && topic_id != 0) {
                        topic = topicRepository.getTopicById(topic_id);
                        if (topic == null) {
                            throw new ErrorException("Invalid topic id", VarList.RSP_NO_DATA_FOUND);
                        }
                    } else {
                        Topic ownTopicCheckObj = topicRepository.getTopicByLinkName(convertToSnakeCase(ownTopic));
                        if (ownTopicCheckObj != null)
                            throw new ErrorException("Your topic already exists. So, select the available topic.", VarList.RSP_NO_DATA_FOUND);
                        topic = new Topic();
                        topic.setTopic(ownTopic);
                        topic.setLinkName(convertToSnakeCase(ownTopic));
                        topic.setSubCategory(courseSubCategory);
                        topicRepository.save(topic);
                    }


                    CourseLandingPage courseLandingPage = courseLandingPageRepository.findByCourseId(course.getId());

                    if (courseLandingPage != null) {

                        if (image != null && !image.isEmpty()) {

                            if (!image.getContentType().startsWith("image/") || !image.getOriginalFilename().matches(".*\\.(jpg|jpeg|png|gif|bmp)$")) {
                                throw new ErrorException("Invalid image file type or extension. Only image files are allowed.", VarList.RSP_NO_DATA_FOUND);
                            }
                            try {
                                final String OldImg = course.getImg();
                                FileUploadResponse imageUploadResponse = FileUploadUtil.saveFile(image, "courses-images");
                                course.setImg(imageUploadResponse.getFilename());
                                if (OldImg != null && !OldImg.isEmpty()) {
                                    try {
                                        Files.delete(Paths.get(Config.UPLOAD_URL + OldImg));
                                    } catch (IOException e) {
                                        // Ignore if file does not exist or cannot be deleted
                                        System.out.println("Old image not deleted: " + e.getMessage());
                                    }
                                }

                            } catch (Exception exception) {
                                throw new ErrorException(exception.getMessage(), VarList.RSP_NO_DATA_FOUND);
                            }

                        }
                        if (video != null && !video.isEmpty()) {
                            try {
                                //promotional-video
                                final String OldVideo = courseLandingPage.getPromotionalVideoUrl();
                                Files.delete(Paths.get(Config.UPLOAD_URL + OldVideo));
                                courseLandingPage.setPromotionalVideoUrl(Config.PROMOTIONAL_VIDEO_UPLOAD_URL + video);
                            } catch (Exception e) {
                                throw new ErrorException("Error", e.getMessage());
                            }
                        }
                        course.setCourseTitle(course_title);

                        course.setCourseCategory(courseCategory);
                        courseLandingPage.setSubTitle(course_subtitle);
                        courseLandingPage.setDescription(course_description);
                        courseLandingPage.setLanguage(languageObj);
                        courseLandingPage.setCourseLevel(courseLevel);
                        courseLandingPage.setSubcategory(courseSubCategory);
                        courseLandingPage.setTopic(topic);

                        successResponse.setMessage("Course landing page update successfully");

                    } else {
                        if (video == null || video.isEmpty()) {
                            throw new ErrorException("Please add a video", VarList.RSP_NO_DATA_FOUND);
                        }
                        try {
                            if (image != null && !image.isEmpty()) {

                                if (!image.getContentType().startsWith("image/") || !image.getOriginalFilename().matches(".*\\.(jpg|jpeg|png|gif|bmp)$")) {
                                    throw new ErrorException("Invalid image file type or extension. Only image files are allowed.", VarList.RSP_NO_DATA_FOUND);
                                }
                                try {
                                    final String OldImg = course.getImg();
                                    FileUploadResponse imageUploadResponse = FileUploadUtil.saveFile(image, "courses-images");
                                    course.setImg(imageUploadResponse.getFilename());
                                    if (OldImg != null && !OldImg.isEmpty()) {
                                        Files.delete(Paths.get(Config.UPLOAD_URL + OldImg));
                                    }

                                } catch (Exception exception) {
                                    throw new ErrorException(exception.getMessage(), VarList.RSP_NO_DATA_FOUND);
                                }

                            }

                            course.setCourseTitle(course_title);
                            course.setCourseCategory(courseCategory);
                            courseLandingPage = new CourseLandingPage();
                            courseLandingPage.setSubTitle(course_subtitle);
                            courseLandingPage.setDescription(course_description);
                            courseLandingPage.setLanguage(languageObj);
                            courseLandingPage.setCourseLevel(courseLevel);
                            courseLandingPage.setSubcategory(courseSubCategory);
                            try {
                                //promotional-video
                                courseLandingPage.setPromotionalVideoUrl(video);
                            } catch (Exception e) {
                                throw new ErrorException("Error", e.getMessage());
                            }
                            courseLandingPage.setCourse(course);
                            courseLandingPage.setTopic(topic);

                            successResponse.setMessage("Course landing page added successfully");

                        } catch (Exception e) {
                            throw new ErrorException(e.getMessage(), VarList.RSP_NO_DATA_FOUND);
                        }
                    }
                    List<CourseKeyword> courseKeywordList = courseKeywordRepository.findByCourse(course);

                    for (int i = 0; i < keywords.length; i++) {
                        CourseKeyword courseKeyword = new CourseKeyword();
                        courseKeyword.setName(keywords[i]);
                        courseKeyword.setCourse(course);
                        courseKeywordRepository.save(courseKeyword);
                    }

                    if (courseKeywordList != null) {
                        int i = 0;
                        for (CourseKeyword obj : courseKeywordList) {
                            obj.setName(courseKeywordList.get(i).getName());
                            i = i + 1;
                            courseKeywordRepository.delete(obj);
                        }
                    }

                    courseRepository.save(course);
                    courseLandingPageRepository.save(courseLandingPage);

                    CourseComplete courseComplete = courseCompleteRepository.getCourseCompleteByCourse(course);

                    if (courseComplete == null) {
                        courseComplete = new CourseComplete();
                        courseComplete.setCourse(course);
                    }
                    courseComplete.setCourseLandingPage((byte) 1);

                    courseCompleteRepository.save(courseComplete);

                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;


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

    private String convertToSnakeCase(String input) {
        if (input == null) {
            return null;
        }
        // Trim leading and trailing spaces, convert to lowercase, and replace multiple spaces with a single underscore
        return input.trim().toLowerCase().replaceAll("\\s+", "_");
    }

    @Override
    public SuccessResponse saveIntendedLearners(IntendedLearnersRequest intendedLearnersRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    final String[] studentsLearn = intendedLearnersRequest.getStudentsLearn();
                    final String[] requirements = intendedLearnersRequest.getRequirements();
                    final String[] who = intendedLearnersRequest.getWho();
                    final String course_code = intendedLearnersRequest.getCourse_code();

                    if (course_code.isEmpty()) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    } else if (studentsLearn == null || studentsLearn.length < 3) {
                        throw new ErrorException("Please enter at least 3 inputs for what will be taught in the course", VarList.RSP_NO_DATA_FOUND);
                    } else if (requirements == null || requirements.length < 3) {
                        throw new ErrorException("Please enter at least 3 inputs in requirements or prerequisites", VarList.RSP_NO_DATA_FOUND);
                    } else if (who.length < 1) {
                        throw new ErrorException("Please add target audience", VarList.RSP_NO_DATA_FOUND);
                    } else {
                        Course course = courseRepository.findByCode(course_code);
                        if (course != null) {
                            List<CourseIntentedLearner> courseIntentedLearnerinfo = courseIntentedLearnerRepository.getCourseIntentedLearnerByCourseId(course.getId());
                            if (courseIntentedLearnerinfo.size() > 0) {
                                int i = 0;
                                for (CourseIntentedLearner obj : courseIntentedLearnerinfo) {
                                    obj.setName(courseIntentedLearnerinfo.get(i).getName());
                                    i = i + 1;
                                    courseIntentedLearnerRepository.delete(obj);
                                }
                            }
                            for (int i = 0; i < studentsLearn.length; i++) {
                                CourseIntentedLearner courseIntentedLearner = new CourseIntentedLearner();
                                courseIntentedLearner.setName(studentsLearn[i]);
                                courseIntentedLearner.setIntendedLearnerType(intendedLearnerTypeRepository.findById(1));
                                courseIntentedLearner.setCourse(course);
                                courseIntentedLearnerRepository.save(courseIntentedLearner);
                            }
                            for (int i = 0; i < requirements.length; i++) {
                                CourseIntentedLearner courseIntentedLearner1 = new CourseIntentedLearner();
                                courseIntentedLearner1.setName(requirements[i]);
                                courseIntentedLearner1.setIntendedLearnerType(intendedLearnerTypeRepository.findById(2));
                                courseIntentedLearner1.setCourse(course);
                                courseIntentedLearnerRepository.save(courseIntentedLearner1);
                            }
                            for (int i = 0; i < who.length; i++) {
                                CourseIntentedLearner courseIntentedLearner2 = new CourseIntentedLearner();
                                courseIntentedLearner2.setName(who[i]);
                                courseIntentedLearner2.setIntendedLearnerType(intendedLearnerTypeRepository.findById(3));
                                courseIntentedLearner2.setCourse(course);
                                courseIntentedLearnerRepository.save(courseIntentedLearner2);
                            }
                        } else {
                            throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
                        }

                        CourseComplete courseComplete = courseCompleteRepository.getCourseCompleteByCourse(course);

                        if (courseComplete == null) {
                            courseComplete = new CourseComplete();
                            courseComplete.setCourse(course);
                        }
                        courseComplete.setIntendedLearners((byte) 1);

                        courseCompleteRepository.save(courseComplete);

                        successResponse.setMessage("Target audience added successfully");
                        successResponse.setVariable(VarList.RSP_SUCCESS);
                        return successResponse;
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
    public SuccessResponse saveMessage(MessagesRequset messagesRequset) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    final String course_code = messagesRequset.getCourse_code();
                    final String welcomeMessage = messagesRequset.getWelcome_msg();
                    final String congratulationsMessage = messagesRequset.getCongratulations_msg();
                    if (course_code.isEmpty()) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    } else if (welcomeMessage.isEmpty()) {
                        throw new ErrorException("Please add a welcome message", VarList.RSP_NO_DATA_FOUND);
                    } else if (congratulationsMessage.isEmpty()) {
                        throw new ErrorException("Please add a congratulations message", VarList.RSP_NO_DATA_FOUND);
                    } else {
                        Course course = courseRepository.findByCode(course_code);
                        if (course != null) {
                            CourseMessage message = courseMessageRepository.getCourseMessageByCourseId(course.getId());
                            if (message == null) {
                                message = new CourseMessage();
                            }
                            message.setCourse(course);
                            message.setCongratulationsMsg(congratulationsMessage);
                            message.setWelcomeMsg(welcomeMessage);
                            courseMessageRepository.save(message);
                        } else {
                            throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
                        }

                        CourseComplete courseComplete = courseCompleteRepository.getCourseCompleteByCourse(course);

                        if (courseComplete == null) {
                            courseComplete = new CourseComplete();
                            courseComplete.setCourse(course);
                        }
                        courseComplete.setCourseMessages((byte) 1);

                        courseCompleteRepository.save(courseComplete);


                        successResponse.setMessage("Course messages added successfully");
                        successResponse.setVariable(VarList.RSP_SUCCESS);
                        return successResponse;
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
    public SuccessResponse addCoupons(AddCouponsRequest addCouponsRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    final String course_code = addCouponsRequest.getCourse_code();
                    final String coupon_code = addCouponsRequest.getCoupon_code();
                    final String description = addCouponsRequest.getCoupon_description();
                    final int type = addCouponsRequest.getPromotion_type();
                    final double amount = addCouponsRequest.getAmount();
                    final Date ex_date = addCouponsRequest.getEx_date();
                    if (course_code.isEmpty()) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    } else if (coupon_code.isEmpty()) {
                        throw new ErrorException("Please add coupon Code", VarList.RSP_NO_DATA_FOUND);
                    } else if (description.isEmpty()) {
                        throw new ErrorException("Please add coupon description", VarList.RSP_NO_DATA_FOUND);
                    } else if (type == 0) {
                        throw new ErrorException("Please select a promotion type", VarList.RSP_NO_DATA_FOUND);
                    } else if (amount == 0.0) {
                        throw new ErrorException("Please add a coupon amount", VarList.RSP_NO_DATA_FOUND);
                    } else if (ex_date == null) {
                        throw new ErrorException("Please select a coupon expiry date", VarList.RSP_NO_DATA_FOUND);
                    } else {
                        Course course = courseRepository.findByCode(course_code);
                        if (course != null) {
                            PromotionCoupon promotionCoupon = new PromotionCoupon();
                            promotionCoupon.setCode(coupon_code);
                            promotionCoupon.setDescription(description);
                            promotionCoupon.setAmount(amount);
                            promotionCoupon.setExpire_date(ex_date);
                            promotionCoupon.setPromotionType(promotionTypeRepository.getPromotionTypeById(type));
                            promotionCoupon.setIs_active((byte) 1);
                            promotionCoupon.setCourse(course);
                            promotionCouponRepository.save(promotionCoupon);
                        } else {
                            throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
                        }

                        CourseComplete courseComplete = courseCompleteRepository.getCourseCompleteByCourse(course);

                        if (courseComplete == null) {
                            courseComplete = new CourseComplete();
                            courseComplete.setCourse(course);
                        }
                        courseComplete.setPromotions((byte) 1);

                        courseCompleteRepository.save(courseComplete);


                        successResponse.setMessage("Promotion coupon added successfully");
                        successResponse.setVariable(VarList.RSP_SUCCESS);
                        return successResponse;
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
    public List<CouponsResponse> getCoupons(String courseCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2 || profile.getGupType().getId() == 3) {
                    if (courseCode.isEmpty()) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    } else {
                        Course course = courseRepository.findByCode(courseCode);
                        if (course != null) {
                            List<PromotionCoupon> promotionCoupons = promotionCouponRepository.getPromotionCouponByCourseId(course.getId());
                            List<CouponsResponse> couponsResponseList = new ArrayList<>();
                            if (promotionCoupons.size() > 0) {
                                for (PromotionCoupon promotionCoupon : promotionCoupons) {
                                    CouponsResponse response = new CouponsResponse();
                                    response.setAmount(promotionCoupon.getAmount());
                                    response.setCoupon_description(promotionCoupon.getDescription());
                                    response.setCoupon_code(promotionCoupon.getCode());
                                    response.setPromotion_type_id(promotionCoupon.getPromotionType().getId());
                                    response.setPromotion_type(promotionCoupon.getPromotionType().getName());
                                    response.setEx_date(promotionCoupon.getExpire_date());
                                    couponsResponseList.add(response);
                                }
                                return couponsResponseList;
                            } else {
                                throw new ErrorException("Not available coupons for course", VarList.RSP_NO_DATA_FOUND);
                            }
                        } else {
                            throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
                        }
                    }
                } else {
                    throw new ErrorException("You are not a instructor or admin to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    private List<CouponsResponse> convertToCouponsResponse(List<PromotionCoupon> coupons) {
        List<CouponsResponse> simplifiedCoupons = new ArrayList<>();
        for (PromotionCoupon couponsObj : coupons) {
            CouponsResponse couponsResponse = new CouponsResponse();
            couponsResponse.setCoupon_code(couponsObj.getCode());
            couponsResponse.setCoupon_description(couponsObj.getDescription());
            couponsResponse.setEx_date(couponsObj.getExpire_date());
            couponsResponse.setAmount(couponsObj.getAmount());
            couponsResponse.setPromotion_type_id(couponsObj.getPromotionType().getId());
            couponsResponse.setPromotion_type(couponsObj.getPromotionType().getName());
            simplifiedCoupons.add(couponsResponse);
        }
        return simplifiedCoupons;
    }

    @Override
    public SuccessResponse saveCoursePricing(List<CoursePricingRequest> coursePricingRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 3) {
                    String country;
                    String minPrice;
                    String maxPrice;
                    String tip;
                    String minimumPrice;
                    for (int i = 0; i < coursePricingRequest.size(); i++) {
                        country = coursePricingRequest.get(i).getCountry();
                        minPrice = coursePricingRequest.get(i).getMinPrice();
                        maxPrice = coursePricingRequest.get(i).getMaxPrice();
                        tip = coursePricingRequest.get(i).getTip();
                        minimumPrice = coursePricingRequest.get(i).getMinimumPrice();

                        if (country == null || country.isEmpty()) {
                            throw new ErrorException("Please add a country", VarList.RSP_NO_DATA_FOUND);
                        } else if (minPrice == null || minPrice.isEmpty()) {
                            throw new ErrorException("Please add a valid min price", VarList.RSP_NO_DATA_FOUND);
                        } else if (Double.parseDouble(minPrice) == 0) {
                            throw new ErrorException("Please add a valid min price", VarList.RSP_NO_DATA_FOUND);
                        } else if (maxPrice == null || maxPrice.isEmpty()) {
                            throw new ErrorException("Please add a valid max price", VarList.RSP_NO_DATA_FOUND);
                        } else if (Double.parseDouble(maxPrice) == 0) {
                            throw new ErrorException("Error", "Please add a valid max price");
                        } else {
                            Double minimumPriceVal = Double.parseDouble(minimumPrice);
                            Double minPriceVal = Double.parseDouble(minPrice);
                            Double maxPriceVal = Double.parseDouble(maxPrice);

//                            if (!(minPriceVal <= minimumPriceVal && minimumPriceVal <= maxPriceVal)) {
                            if (!(minimumPriceVal > 0 && minimumPriceVal <= maxPriceVal)) {
                                throw new ErrorException("Invalid minimum price because the price is not in the price range: " + country, VarList.RSP_NO_DATA_FOUND);
                            }
                            Country getcountry = countryRepository.getCountryByName(country);
                            if (getcountry != null) {
                                PriceSetup priceSetup = priceSetupRepository.getPriceSetupByCountryId(getcountry.getId());
                                if (priceSetup != null) {
                                    priceSetup.setMinPrice(Double.parseDouble(minPrice));
                                    priceSetup.setMaxPrice(Double.parseDouble(maxPrice));
                                    if (tip == null || tip.isEmpty()) {
                                        priceSetup.setTip("N/A");
                                    } else {
                                        priceSetup.setTip(tip);
                                    }
                                    if (minimumPrice == null || minimumPrice.isEmpty()) {
                                        priceSetup.setMinimumPrice(minPriceVal);
                                    } else {
                                        priceSetup.setMinimumPrice(minimumPriceVal);
                                    }

                                    priceSetup.setMinimumPrice(Double.parseDouble(minimumPrice));
                                    priceSetupRepository.save(priceSetup);
                                } else {
                                    throw new ErrorException("Country name not available", VarList.RSP_NO_DATA_FOUND);
                                }
                            } else {
                                throw new ErrorException("Country name not available", VarList.RSP_NO_DATA_FOUND);
                            }
                        }
                    }
                    successResponse.setMessage("Course prices added successfully");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;
                } else {
                    throw new ErrorException("You are not an admin for this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }


    @Override
    public SuccessResponse saveSingleCoursePricing(AddDefaultPrice addDefaultPrice) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    final String globalListPrice = addDefaultPrice.getGlobalListPrice();
                    final String courseCode = addDefaultPrice.getCourseCode();
                    final int discountTypeId = addDefaultPrice.getDiscountType();
                    final String discountAmount = addDefaultPrice.getDiscountAmount();
                    final String globalNetPrice = addDefaultPrice.getGlobalNetPrice();

                    if (courseCode == null || courseCode.isEmpty() || globalListPrice == null || globalListPrice.isEmpty() || discountTypeId == 0 || discountAmount == null || discountAmount.isEmpty() || globalNetPrice == null || globalNetPrice.isEmpty()) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    }
                    Course course = courseRepository.getCourseByCode(courseCode);
                    if (course == null) {
                        throw new ErrorException("Course not found", VarList.RSP_NO_DATA_FOUND);
                    }
//                    InstructorProfile instructorProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(profile);
//                    if (instructorProfile == null || instructorProfile.getIsProfileCompleted() != 1) {
//                        throw new ErrorException("Error", "Instructor profile details are not complete. So please fill it and try again");
//                    }
//                    InstructorPayments instructorPayments = instructorPaymentsRepository.getInstructorPaymentsByInstructorProfile(instructorProfile);
//                    if (instructorPayments == null) {
//                        throw new ErrorException("Please complete instructor payment details", VarList.RSP_NO_DATA_FOUND);
//                    } else {
//                        if (instructorPayments.getInstructorTerms() == null || instructorPayments.getInstructorTerms() == 0) {
//                            throw new ErrorException("Please agree to the instructor terms", VarList.RSP_NO_DATA_FOUND);
//                        }
//                    }

                    Country country = countryRepository.getCountryById(30);
                    CoursePrice coursePrice = coursePriceRepository.getCoursePriceByCourseAndCountry(course, country);
                    PriceSetup priceSetup = priceSetupRepository.getPriceSetupByCountryId(30);
                    Double globalNetPriceVal = Double.parseDouble(globalNetPrice);
                    Double discount2 = Double.parseDouble(addDefaultPrice.getDiscount());
                    double globalListPriceVal = Double.parseDouble(globalListPrice);

                    if (priceSetup == null) {
                        throw new ErrorException("Not found priceSetup", VarList.RSP_NO_DATA_FOUND);
                    }
                    if (!(globalListPriceVal == 0 || globalListPriceVal >= priceSetup.getMinPrice() && globalListPriceVal <= priceSetup.getMaxPrice())) {
                        throw new ErrorException("The global list price is not in the price range", VarList.RSP_NO_DATA_FOUND);
                    }

                    if (globalNetPriceVal == 0) {
                        DiscountType discountType = discountTypeRepository.getDiscountTypeById(discountTypeId);
                        if (coursePrice == null) {
                            coursePrice = new CoursePrice();
                        }
                        coursePrice.setDiscountValue(Double.parseDouble(discountAmount));
                        coursePrice.setValue(Double.parseDouble(globalListPrice));
                        coursePrice.setCountry(country);
                        coursePrice.setCourse(course);
                        coursePrice.setCurrency(priceSetup.getCurrency());
                        coursePrice.setDiscountType(discountType);
                        coursePrice.setDiscount(discount2);
                        coursePrice.setNetPrice(globalNetPriceVal);
                        coursePriceRepository.save(coursePrice);
                    } else if (!(globalNetPriceVal >= 0 && globalNetPriceVal <= priceSetup.getMaxPrice())) {
                        throw new ErrorException("The global net price is not in the price range", VarList.RSP_NO_DATA_FOUND);
                    } else {
                        DiscountType discountType = discountTypeRepository.getDiscountTypeById(discountTypeId);
                        if (coursePrice == null) {
                            coursePrice = new CoursePrice();
                            coursePrice.setDiscountValue(Double.parseDouble(discountAmount));
                            coursePrice.setValue(Double.parseDouble(globalListPrice));
                            coursePrice.setCountry(country);
                            coursePrice.setCourse(course);
                            coursePrice.setCurrency(priceSetup.getCurrency());
                            coursePrice.setDiscountType(discountType);
                            coursePrice.setDiscount(discount2);
                        } else {
                            coursePrice.setDiscountValue(Double.parseDouble(discountAmount));
                            coursePrice.setValue(Double.parseDouble(globalListPrice));
                            coursePrice.setCountry(country);
                            coursePrice.setCourse(course);
                            coursePrice.setCurrency(priceSetup.getCurrency());
                            coursePrice.setDiscountType(discountType);
                            coursePrice.setDiscount(discount2);
                        }
                        coursePrice.setNetPrice(globalNetPriceVal);
                        coursePriceRepository.save(coursePrice);
                    }
                    List<SetCoursePricingRequest> setCoursePricingRequests = addDefaultPrice.getPrices();
                    String listPrice;
                    String discountType2;
                    String country2;
                    String discount;
                    String netPrice;
                    String discountAmount2;
                    for (SetCoursePricingRequest setCoursePricingRequest : setCoursePricingRequests) {
                        listPrice = setCoursePricingRequest.getListPrice();
                        discountType2 = setCoursePricingRequest.getDiscountType();
                        country2 = setCoursePricingRequest.getCountry();
                        discount = setCoursePricingRequest.getDiscount();
                        discountAmount2 = setCoursePricingRequest.getDiscountAmount();
                        netPrice = setCoursePricingRequest.getNetPrice();

                        if (country2.isEmpty() || country == null) {
                            throw new ErrorException("Please add a country", VarList.RSP_NO_DATA_FOUND);
                        } else if (listPrice.isEmpty()) {
                            throw new ErrorException("Please add a list price", VarList.RSP_NO_DATA_FOUND);
                        } else if (netPrice.isEmpty()) {
                            throw new ErrorException("Please add a net price", VarList.RSP_NO_DATA_FOUND);
                        } else if (discountType2.isEmpty()) {
                            throw new ErrorException("Please add a discount type", VarList.RSP_NO_DATA_FOUND);
                        } else {
                            double listPriceVal = Double.parseDouble(listPrice);
                            int discountTypeVal = Integer.parseInt(discountType2);
                            double discountValueVal = Double.parseDouble(discount);
                            double discountAmount2Val = Double.parseDouble(discountAmount2);
                            double netPriceVal = Double.parseDouble(netPrice);

                            Country getcountry = countryRepository.getCountryByName(country2);
                            if (getcountry == null) {
                                throw new ErrorException("Country name not available", VarList.RSP_NO_DATA_FOUND);
                            }
                            PriceSetup priceSetup2 = priceSetupRepository.getPriceSetupByCountryId(getcountry.getId());
                            if (priceSetup2 == null) {
                                throw new ErrorException("Country name not available", VarList.RSP_NO_DATA_FOUND);
                            }

                            if (!(listPriceVal == 0 || listPriceVal >= priceSetup2.getMinPrice() && listPriceVal <= priceSetup2.getMaxPrice())) {
                                throw new ErrorException("The list price is not in the price range: " + getcountry.getName(), VarList.RSP_NO_DATA_FOUND);
                            }

                            if (netPriceVal == 0) {
                                CoursePrice coursePrice2 = coursePriceRepository.getCoursePriceByCountryIdAndCourseId(getcountry.getId(), course.getId());
                                if (coursePrice2 != null) {
                                    coursePrice2.setValue(listPriceVal);
                                    coursePrice2.setDiscountValue(discountAmount2Val);
                                    coursePrice2.setDiscountType(discountTypeRepository.getDiscountTypeById(discountTypeVal));
                                    coursePrice2.setDiscount(discountValueVal);
                                    coursePrice2.setNetPrice(netPriceVal);
                                } else {
                                    coursePrice2 = new CoursePrice();
                                    coursePrice2.setValue(listPriceVal);
                                    coursePrice2.setDiscountValue(discountAmount2Val);
                                    coursePrice2.setCurrency(priceSetup2.getCurrency());
                                    coursePrice2.setCountry(priceSetup2.getCountry());
                                    coursePrice2.setDiscount(discountValueVal);
                                    coursePrice2.setNetPrice(netPriceVal);
                                    coursePrice2.setCourse(course);
                                    coursePrice2.setDiscountType(discountTypeRepository.getDiscountTypeById(discountTypeVal));
                                }
                                coursePriceRepository.save(coursePrice2);
                                course.setIsPaid(2);
                                courseRepository.save(course);
                            } else if (!(netPriceVal >= 0 && netPriceVal <= priceSetup2.getMaxPrice())) {
                                throw new ErrorException("Invalid price because the price is not in the price range: " + getcountry.getName(), VarList.RSP_NO_DATA_FOUND);
                            } else {
                                CoursePrice coursePrice2 = coursePriceRepository.getCoursePriceByCountryIdAndCourseId(getcountry.getId(), course.getId());
                                if (coursePrice2 != null) {
                                    coursePrice2.setValue(listPriceVal);
                                    coursePrice2.setDiscountType(discountTypeRepository.getDiscountTypeById(discountTypeVal));
                                    coursePrice2.setDiscount(discountValueVal);
                                    coursePrice2.setDiscountValue(discountAmount2Val);
                                    coursePrice2.setNetPrice(netPriceVal);
                                } else {
                                    coursePrice2 = new CoursePrice();
                                    coursePrice2.setValue(listPriceVal);
                                    coursePrice2.setCurrency(priceSetup2.getCurrency());
                                    coursePrice2.setCountry(priceSetup2.getCountry());
                                    coursePrice2.setDiscount(discountValueVal);
                                    coursePrice2.setDiscountValue(discountAmount2Val);
                                    coursePrice2.setNetPrice(netPriceVal);
                                    coursePrice2.setCourse(course);
                                    coursePrice2.setDiscountType(discountTypeRepository.getDiscountTypeById(discountTypeVal));
                                }
                                coursePriceRepository.save(coursePrice2);
                                course.setIsPaid(2);
                                courseRepository.save(course);
                            }
                        }
                    }

                    CourseComplete courseComplete = courseCompleteRepository.getCourseCompleteByCourse(course);

                    if (courseComplete == null) {
                        courseComplete = new CourseComplete();
                        courseComplete.setCourse(course);
                    }
                    courseComplete.setPricing((byte) 1);

                    courseCompleteRepository.save(courseComplete);


                    successResponse.setMessage("Course prices added successfully");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;
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

    private void validateInput(String courseCode, String sectionName, String article, MultipartFile video,
                               String description, MultipartFile downloadableFile, String resourcesTitle,
                               String url, MultipartFile sourceCode) {
        if (courseCode == null || courseCode.isEmpty() || sectionName == null || sectionName.isEmpty()
                || ((video == null || video.isEmpty()) && (article == null || article.isEmpty()))
                || description == null || description.isEmpty() || downloadableFile == null || downloadableFile.isEmpty()
                || resourcesTitle == null || resourcesTitle.isEmpty() || url == null || url.isEmpty()
                || sourceCode == null || sourceCode.isEmpty()) {
            throw new ErrorException("Invalid request. Please check your input.", VarList.RSP_NO_DATA_FOUND);
        }
    }

    private void validateVideoFile(MultipartFile video) {
        if (!video.getContentType().startsWith("video/") || !video.getOriginalFilename().matches(".*\\.(mp4|avi|mov|mkv|webm)$")) {
            throw new ErrorException("Invalid video file type. Only video files are allowed.", VarList.RSP_NO_DATA_FOUND);
        }
    }

    private FileUploadResponse saveFile(MultipartFile file) {
        try {
            return FileUploadUtil.saveFile(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createCurriculumItemFile(SectionCurriculumItem item, String url, String title, String fileType) {
        CurriculumItemFile curriculumItemFile = new CurriculumItemFile();
        curriculumItemFile.setTitle(title);
        curriculumItemFile.setUrl(url);
        curriculumItemFile.setCurriculumItemFileTypes(curriculumItemFileTypeRepository.getCurriculumItemFileTypeByName(fileType));
        curriculumItemFile.setSectionCurriculumItem(item);
        curriculumItemFileRepository.save(curriculumItemFile);
    }


    @Override
    public CourseLandingPageResponse getCourseLandingPage(String courseCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2 || profile.getGupType().getId() == 3) {
                    if (courseCode == null || courseCode.isEmpty()) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    } else {
                        Course course = courseRepository.findByCode(courseCode);
                        if (course != null) {
                            CourseLandingPage courseLandingPage = courseLandingPageRepository.getCourseLandingPageByCourse(course);
                            CourseLandingPageResponse courseLandingPageResponse = new CourseLandingPageResponse();
                            courseLandingPageResponse.setCourseTitle(course.getCourseTitle());
                            courseLandingPageResponse.setCourseLength(course.getCourseLength() == 0 ? 0 : course.getCourseLength());
                            courseLandingPageResponse.setCategoryId(course.getCourseCategory().getId().toString());
                            courseLandingPageResponse.setCategory(course.getCourseCategory().getName());
                            List<CourseKeyword> courseKeywordList = courseKeywordRepository.findByCourse(course);
                            if (courseKeywordList.size() > 0) {
                                String[] keywordsArray = new String[courseKeywordList.size()];
                                for (int i = 0; i < courseKeywordList.size(); i++) {
                                    keywordsArray[i] = courseKeywordList.get(i).getName();
                                }
                                courseLandingPageResponse.setKeywords(keywordsArray);
                            }
                            courseLandingPageResponse.setCourseImage(course.getImg());
                            if (courseLandingPage != null) {
                                courseLandingPageResponse.setCourseSubTitle(courseLandingPage.getSubTitle());
                                courseLandingPageResponse.setDescription(courseLandingPage.getDescription());
                                courseLandingPageResponse.setLanguageId(courseLandingPage.getLanguage().getId().toString());
                                courseLandingPageResponse.setLanguage(courseLandingPage.getLanguage().getName());
                                courseLandingPageResponse.setLevelId(courseLandingPage.getCourseLevel().getId().toString());
                                courseLandingPageResponse.setLevel(courseLandingPage.getCourseLevel().getName());
                                Integer subCategoryId = courseLandingPage.getSubcategory().getId();
                                courseLandingPageResponse.setSubCategoryId(subCategoryId.toString());
                                courseLandingPageResponse.setSubCategory(courseLandingPage.getSubcategory().getName());
                                courseLandingPageResponse.setPromotionalVideo(courseLandingPage.getPromotionalVideoUrl());
                                courseLandingPageResponse.setTopicId(courseLandingPage.getTopic().getId().toString());
                            } else {
                                courseLandingPageResponse.setCourseSubTitle("");
                                courseLandingPageResponse.setDescription("");
                                courseLandingPageResponse.setLanguageId("");
                                courseLandingPageResponse.setLanguage("");
                                courseLandingPageResponse.setLevelId("");
                                courseLandingPageResponse.setLevel("");
                                courseLandingPageResponse.setSubCategoryId("");
                                courseLandingPageResponse.setSubCategory("");
                                courseLandingPageResponse.setPromotionalVideo("");
                                courseLandingPageResponse.setTopicId("");
                            }
                            return courseLandingPageResponse;
                        } else {
                            throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
                        }
                    }
                } else {
                    throw new ErrorException("You are not a instructor or admin to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public IntendedLearnersResponse getIntendedLearners(String courseCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2 || profile.getGupType().getId() == 3) {
                    if (courseCode == null || courseCode.isEmpty()) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    } else {
                        Course course = courseRepository.findByCode(courseCode);
                        if (course != null) {
                            List<CourseIntentedLearner> courseIntentedLearner = courseIntentedLearnerRepository.getCourseIntentedLearnerByCourseId(course.getId());
                            IntendedLearnersResponse intendedLearnersResponse = new IntendedLearnersResponse();
                            List<String> studentsLearnList = new ArrayList<>();
                            List<String> requirementsList = new ArrayList<>();
                            List<String> whoList = new ArrayList<>();

                            for (int i = 0; i < courseIntentedLearner.size(); i++) {
                                if (courseIntentedLearner.get(i).getIntendedLearnerType().getId() == 1) {
                                    studentsLearnList.add(courseIntentedLearner.get(i).getName());
                                } else if (courseIntentedLearner.get(i).getIntendedLearnerType().getId() == 2) {
                                    requirementsList.add(courseIntentedLearner.get(i).getName());
                                } else if (courseIntentedLearner.get(i).getIntendedLearnerType().getId() == 3) {
                                    whoList.add(courseIntentedLearner.get(i).getName());
                                }
                            }
                            intendedLearnersResponse.setStudentsLearn(studentsLearnList.toArray(new String[0]));
                            intendedLearnersResponse.setRequirements(requirementsList.toArray(new String[0]));
                            intendedLearnersResponse.setWho(whoList.toArray(new String[0]));
                            return intendedLearnersResponse;
                        } else {
                            throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
                        }
                    }
                } else {
                    throw new ErrorException("You are not a instructor or admin to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public MessagesResponse getMessages(String courseCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2 || profile.getGupType().getId() == 3) {
                    if (courseCode == null || courseCode.isEmpty()) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    } else {
                        Course course = courseRepository.findByCode(courseCode);
                        if (course != null) {
                            CourseMessage courseMessage = courseMessageRepository.getCourseMessageByCourseId(course.getId());
                            if (courseMessage != null) {
                                MessagesResponse messagesResponse = new MessagesResponse();
                                messagesResponse.setCongratulations_msg(courseMessage.getCongratulationsMsg());
                                messagesResponse.setWelcome_msg(courseMessage.getWelcomeMsg());
                                return messagesResponse;
                            } else {
                                throw new ErrorException("Unavailable course messages", VarList.RSP_NO_DATA_FOUND);
                            }

                        } else {
                            throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
                        }
                    }
                } else {
                    throw new ErrorException("You are not a instructor or admin to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public List<PricingResponse> getPricing(String courseCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2 || profile.getGupType().getId() == 3) {
                    if (courseCode == null || courseCode.isEmpty()) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    } else {
                        Course course = courseRepository.findByCode(courseCode);
                        if (course != null) {
                            List<CoursePrice> coursePrice = coursePriceRepository.getCoursePriceByCourseId(course.getId());
                            if (coursePrice.size() > 0) {
                                return convertToPricingResponse(coursePrice);
                            } else {
                                throw new ErrorException("Unavailable course prices", VarList.RSP_NO_DATA_FOUND);
                            }
                        } else {
                            throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
                        }
                    }
                } else {
                    throw new ErrorException("You are not a instructor or admin to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    private List<PricingResponse> convertToPricingResponse(List<CoursePrice> coursePrices) { // Fix method signature
        List<PricingResponse> pricingResponses = new ArrayList<>();
        for (CoursePrice coursePrice : coursePrices) {
            PricingResponse pricingResponse = new PricingResponse();
            pricingResponse.setCountry(coursePrice.getCountry().getName());
            pricingResponse.setCurrency(coursePrice.getCurrency().getName());
            pricingResponse.setValue(coursePrice.getValue());
            pricingResponse.setDiscountType(coursePrice.getDiscountType().getName());
            pricingResponse.setDiscountValue(coursePrice.getDiscountValue());
            pricingResponses.add(pricingResponse);
        }
        return pricingResponses;
    }


    @Override
    public List<PricingRangeResponse> getPricingRange() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2 || profile.getGupType().getId() == 3) {
                    List<PriceSetup> priceSetups = priceSetupRepository.findAll();
                    if (priceSetups.size() > 0) {
                        return convertToPringRangeResponse(priceSetups);
                    } else {
                        throw new ErrorException("Unavailable price setup", VarList.RSP_NO_DATA_FOUND);
                    }
                } else {
                    throw new ErrorException("You are not a instructor or admin to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    private List<PricingRangeResponse> convertToPringRangeResponse(List<PriceSetup> priceSetups) {
        List<PricingRangeResponse> simplifiedPriceSetups = new ArrayList<>();
        for (PriceSetup priceSetup : priceSetups) {
            PricingRangeResponse pricingRangeResponse = new PricingRangeResponse();
            if (priceSetup.getCountry().getId() == 13) {
                pricingRangeResponse.setMinPrice(String.valueOf((int) Math.round(priceSetup.getMinPrice())));
                pricingRangeResponse.setMaxPrice(String.valueOf((int) Math.round(priceSetup.getMaxPrice())));
                pricingRangeResponse.setCountryCurrency(priceSetup.getCurrency().getName());
                pricingRangeResponse.setCountry(priceSetup.getCountry().getName());
                pricingRangeResponse.setTip(priceSetup.getTip());
                pricingRangeResponse.setMinimumPrice(String.valueOf((int) Math.round(priceSetup.getMinimumPrice())));
            } else {
                pricingRangeResponse.setMinPrice(priceSetup.getMinPrice().toString());
                pricingRangeResponse.setMaxPrice(priceSetup.getMaxPrice().toString());
                pricingRangeResponse.setCountryCurrency(priceSetup.getCurrency().getName());
                pricingRangeResponse.setCountry(priceSetup.getCountry().getName());
                pricingRangeResponse.setTip(priceSetup.getTip());
                pricingRangeResponse.setMinimumPrice(priceSetup.getMinimumPrice().toString());
            }
            simplifiedPriceSetups.add(pricingRangeResponse);
        }
        return simplifiedPriceSetups;
    }

    public List<CurriculumResponse> getCurriculum(String courseCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);

        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2 || profile.getGupType().getId() == 3) {
                    if (courseCode == null || courseCode.isEmpty()) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    } else {
                        Course course = courseRepository.findByCode(courseCode);

                        if (course != null) {
                            List<CourseSection> courseSectionList = courseSectionRepository.findByCourseOrderByArrangedNoAsc(course);

                            if (courseSectionList.size() > 0) {
                                List<CurriculumResponse> curriculumResponseList = new ArrayList<>();

                                for (CourseSection courseSectionObj : courseSectionList) {
                                    if (courseSectionObj.getIsDelete() == null || courseSectionObj.getIsDelete() == 0) {
                                        List<SectionCurriculumItem> sectionCurriculumItemList = sectionCurriculumItemRepository.getSectionCurriculumItemsByCourseSection(courseSectionObj);

                                        List<GetSectionItem> curriculumItemList = new ArrayList<>();

                                        for (SectionCurriculumItem sectionCurriculumItem : sectionCurriculumItemList) {
                                            if (sectionCurriculumItem.getIsDelete() == null || sectionCurriculumItem.getIsDelete() == 0) {
                                                List<CurriculumItemFile> curriculumItemFiles = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItem(sectionCurriculumItem);

                                                List<Getcurriculumitemfile> curriculumItemFilesList = new ArrayList<>();
                                                for (CurriculumItemFile curriculumItemFileObj : curriculumItemFiles) {
                                                    if (curriculumItemFileObj.getIsDelete() == null || curriculumItemFileObj.getIsDelete() == 0) {
                                                        Getcurriculumitemfile getcurriculumitemfile = new Getcurriculumitemfile();
                                                        getcurriculumitemfile.setId(curriculumItemFileObj.getId().toString());
                                                        getcurriculumitemfile.setTitle(curriculumItemFileObj.getTitle());
                                                        getcurriculumitemfile.setUrl(curriculumItemFileObj.getUrl());
                                                        getcurriculumitemfile.setFiletype(curriculumItemFileObj.getCurriculumItemFileTypes().getName());
                                                        getcurriculumitemfile.setPreviewVideo(curriculumItemFileObj != null && curriculumItemFileObj.getIsPreviewVideo() != null && curriculumItemFileObj.getIsPreviewVideo() == 1 ? true : false);
                                                        curriculumItemFilesList.add(getcurriculumitemfile);
                                                    }
                                                }

                                                GetSectionItem getSectionItem = new GetSectionItem();
                                                getSectionItem.setId(sectionCurriculumItem.getId().toString());
                                                getSectionItem.setTitle(sectionCurriculumItem.getTitle());
                                                getSectionItem.setArticle(sectionCurriculumItem.getArticle());
                                                getSectionItem.setDescription(sectionCurriculumItem.getDescription());
                                                getSectionItem.setType(sectionCurriculumItem.getCurriculumItemType().getName());
                                                getSectionItem.setArrangeNo(sectionCurriculumItem.getArrangedNo() == null ? "" : sectionCurriculumItem.getArrangedNo().toString());
                                                getSectionItem.setCurriculumItemFiles(curriculumItemFilesList);
                                                List<Quiz> quizs = quizRepository.getQuizBySectionCurriculumItem(sectionCurriculumItem);
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
                                                getSectionItem.setGetQuizs(getQuizList);

                                                curriculumItemList.add(getSectionItem);
                                                Assignment assignment = assignmentRepository.getAssignmentBySectionCurriculumItem(sectionCurriculumItem);
                                                List<GetAssignment> getAssignments = new ArrayList<>();
                                                if (assignment != null) {
                                                    GetAssignment getAssignment = new GetAssignment();
                                                    getAssignment.setAssignmentCode(assignment.getAssignmentCode());
                                                    getAssignment.setDuration(assignment.getDuration());
                                                    getAssignment.setInstructions(assignment.getInstructions());
                                                    CurriculumItemFileType curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(5);
                                                    CurriculumItemFile curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(sectionCurriculumItem, curriculumItemFileType);
                                                    getAssignment.setAssignmentVideo(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                                                    getAssignment.setAssignmentVideoTitle(curriculumItemFile != null ? curriculumItemFile.getTitle() : "");
                                                    curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(6);
                                                    curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(sectionCurriculumItem, curriculumItemFileType);
                                                    getAssignment.setDownloadableResource(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                                                    getAssignment.setDownloadableResourceTitle(curriculumItemFile != null ? curriculumItemFile.getTitle() : "");
                                                    getAssignment.setExternalLink(assignment.getExternalLink() != null ? assignment.getExternalLink() : "");
                                                    getAssignment.setQuestion(assignment.getQuestions() != null ? assignment.getQuestions() : "");
                                                    curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(7);
                                                    curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(sectionCurriculumItem, curriculumItemFileType);
                                                    getAssignment.setQuestionSheet(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                                                    getAssignment.setQuestionSheetTitle(curriculumItemFile != null ? curriculumItemFile.getTitle() : "");
                                                    getAssignment.setQuestionExternalLink(assignment.getQuestionsExternalLink() != null ? assignment.getQuestionsExternalLink() : "");
                                                    getAssignment.setSolutions(assignment.getSolutions() != null ? assignment.getSolutions() : "");
                                                    curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(8);
                                                    curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(sectionCurriculumItem, curriculumItemFileType);
                                                    getAssignment.setSolutionVideo(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                                                    getAssignment.setSolutionVideoTitle(curriculumItemFile != null ? curriculumItemFile.getTitle() : "");
                                                    curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(9);
                                                    curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(sectionCurriculumItem, curriculumItemFileType);
                                                    getAssignment.setSolutionsSheet(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                                                    getAssignment.setSolutionsSheetTitle(curriculumItemFile != null ? curriculumItemFile.getTitle() : "");
                                                    getAssignment.setSolutionsExternalLink(assignment.getSolutionsExternalLink() != null ? assignment.getSolutionsExternalLink() : "");
                                                    getAssignments.add(getAssignment);
                                                }
                                                getSectionItem.setGetAssignment(getAssignments);
                                                CodingExercise codingExercise = codingExerciseRepository.getCodingExerciseBySectionCurriculumItem(sectionCurriculumItem);
                                                List<GetCodingExercise> getCodingExercises = new ArrayList<>();
                                                if (codingExercise != null) {
                                                    GetCodingExercise getCodingExercise = new GetCodingExercise();
                                                    getCodingExercise.setCodingExerciseCode(codingExercise.getCodingExerciseCode());
                                                    getCodingExercise.setInstructions(codingExercise.getInstructions());
                                                    CurriculumItemFileType curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(10);
                                                    CurriculumItemFile curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(sectionCurriculumItem, curriculumItemFileType);
                                                    getCodingExercise.setCodingVideo(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                                                    curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(11);
                                                    curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(sectionCurriculumItem, curriculumItemFileType);
                                                    getCodingExercise.setDownloadableResource(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                                                    getCodingExercise.setDownloadableResourceTitle(curriculumItemFile != null ? curriculumItemFile.getTitle() : "");
                                                    getCodingExercise.setCodingExternalLink(codingExercise.getExternalLink() != null ? codingExercise.getExternalLink() : "");
                                                    curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(12);
                                                    curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(sectionCurriculumItem, curriculumItemFileType);
                                                    getCodingExercise.setCodingExerciseSheet(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                                                    getCodingExercise.setCodingExerciseSheetTitle(curriculumItemFile != null ? curriculumItemFile.getTitle() : "");
                                                    getCodingExercise.setCodingExternalLink(codingExercise.getCodingLink() != null ? codingExercise.getCodingLink() : "");
                                                    curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(13);
                                                    curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(sectionCurriculumItem, curriculumItemFileType);
                                                    getCodingExercise.setCodingExerciseVideo(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                                                    getCodingExercise.setCodingExerciseVideoTitle(curriculumItemFile != null ? curriculumItemFile.getTitle() : "");
                                                    curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(15);
                                                    curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(sectionCurriculumItem, curriculumItemFileType);
                                                    getCodingExercise.setCodingSolutionsSheet(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                                                    getCodingExercise.setCodingSolutionsSheetTitle(curriculumItemFile != null ? curriculumItemFile.getTitle() : "");
                                                    getCodingExercise.setSolutionsExternalLink(codingExercise.getSolutionLink() != null ? codingExercise.getSolutionLink() : "");
                                                    curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(14);
                                                    curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(sectionCurriculumItem, curriculumItemFileType);
                                                    getCodingExercise.setCodingSolutionsVideo(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                                                    getCodingExercise.setCodingSolutionsVideoTitle(curriculumItemFile != null ? curriculumItemFile.getTitle() : "");
                                                    getCodingExercises.add(getCodingExercise);
                                                }
                                                getSectionItem.setGetCodingExercises(getCodingExercises);

                                                PracticeTest practiceTest = practiceTestRepository.getPracticeTestBySectionCurriculumItem(sectionCurriculumItem);
                                                List<GetPracticeTest> getPracticeTests = new ArrayList<>();
                                                if (practiceTest != null) {
                                                    GetPracticeTest getPracticeTest = new GetPracticeTest();
                                                    getPracticeTest.setTitle(practiceTest.getSectionCurriculumItem().getTitle());
                                                    getPracticeTest.setPracticeTestCode(practiceTest.getPracticeTestCode());
                                                    getPracticeTest.setDuration(practiceTest.getDuration());
                                                    getPracticeTest.setMinimumuPassMark(practiceTest.getMinimumPassMark());
                                                    getPracticeTest.setInstructions(practiceTest.getInstructions());
                                                    getPracticeTest.setExternalLink(practiceTest.getExternalLink() != null ? practiceTest.getExternalLink() : "");
                                                    CurriculumItemFileType curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(16);
                                                    CurriculumItemFile curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(sectionCurriculumItem, curriculumItemFileType);
                                                    getPracticeTest.setPracticeTestQuestionSheet(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                                                    getPracticeTest.setPracticeTestQuestionSheetTitle(curriculumItemFile != null ? curriculumItemFile.getTitle() : "");
                                                    getPracticeTest.setQuestionLink(practiceTest.getQuestionLink() != null ? practiceTest.getQuestionLink() : "");
                                                    curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(17);
                                                    curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(sectionCurriculumItem, curriculumItemFileType);
                                                    getPracticeTest.setPracticeTestSolutionSheet(curriculumItemFile != null ? curriculumItemFile.getUrl() : "");
                                                    getPracticeTest.setPracticeTestSolutionSheetTitle(curriculumItemFile != null ? curriculumItemFile.getTitle() : "");
                                                    getPracticeTest.setSolutionLink(practiceTest.getSolutionLink() != null ? practiceTest.getSolutionLink() : "");
                                                    getPracticeTests.add(getPracticeTest);
                                                }
                                                getSectionItem.setGetPracticeTests(getPracticeTests);

                                            }
                                        }

                                        CurriculumResponse curriculumResponse = new CurriculumResponse();
                                        GetCourseSection getCourseSection = new GetCourseSection();
                                        getCourseSection.setSectionId(courseSectionObj.getId().toString());
                                        getCourseSection.setSectionName(courseSectionObj.getSectionName());
                                        getCourseSection.setSectionCurriculumItem(curriculumItemList);
                                        curriculumResponse.setCourseSection(getCourseSection);

                                        curriculumResponseList.add(curriculumResponse);
                                    }
                                }

                                if (curriculumResponseList.size() == 0) {
                                    throw new ErrorException("Unavailable course section", VarList.RSP_NO_DATA_FOUND);
                                }

                                return curriculumResponseList;

                            } else {
                                throw new ErrorException("Unavailable course section", VarList.RSP_NO_DATA_FOUND);
                            }
                        } else {
                            throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
                        }
                    }
                } else {
                    throw new ErrorException("You are not a instructor or admin to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse setUnpublish(String courseCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    if (courseCode == null || courseCode.isEmpty()) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    } else {
                        Course course = courseRepository.findByCode(courseCode);
                        if (course != null) {
                            course.setApprovalType(approvalTypeRepository.getApprovalTypeById(6));
                            courseRepository.save(course);
                            successResponse.setMessage("Course unpublished");
                            successResponse.setVariable(VarList.RSP_SUCCESS);
                            return successResponse;
                        } else {
                            throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
                        }
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
    public CourseLandingPage getCourseLandingPageDetails(String courseCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2 || profile.getGupType().getId() == 3) {
                    if (courseCode == null || courseCode.isEmpty()) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    } else {
                        Course course = courseRepository.findByCode(courseCode);
                        if (course != null) {
                            CourseLandingPage courseLandingPage = courseLandingPageRepository.getCourseLandingPageByCourse(course);
                            if (courseLandingPage == null) {
                                throw new ErrorException("Course not available in course landing page", VarList.RSP_NO_DATA_FOUND);
                            }
                            return courseLandingPage;
                        } else {
                            throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
                        }
                    }
                } else {
                    throw new ErrorException("You are not a instructor or admin to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public List<Language> getAllLanguage() {
        List<Language> language = languageRepository.findAllByOrderByNameAsc();
        if (language.size() > 0) {
            return language;
        } else {
            throw new ErrorException("Languages not available", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public List<CourseLevel> getAllCourseLevels() {
        List<CourseLevel> levels = courseLevelRepository.findAll();
        if (levels.size() > 0) {
            return levels;
        } else {
            throw new ErrorException("Course levels not available", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public List<SubCategoryResponse> getAllCourseSubCategory(Integer categoryID) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                CourseCategory courseCategory = courseCategoryRepository.getCourseCategoryById(categoryID);
                if (courseCategory != null) {
                    List<CourseSubCategory> courseSubCategoryList = courseSubCategoryRepository.getCourseSubCategoriesByCourseCategory(courseCategory);
                    if (courseSubCategoryList.size() > 0) {
                        List<SubCategoryResponse> subCategoryResponseList = new ArrayList<>();
                        for (CourseSubCategory courseSubCategory : courseSubCategoryList) {
                            SubCategoryResponse subCategoryResponse = new SubCategoryResponse();
                            subCategoryResponse.setId(courseSubCategory.getId());
                            subCategoryResponse.setName(courseSubCategory.getName());
                            subCategoryResponseList.add(subCategoryResponse);
                        }
                        return subCategoryResponseList;
                    } else {
                        throw new ErrorException("Course sub categories not available", VarList.RSP_NO_DATA_FOUND);
                    }
                } else {
                    throw new ErrorException("Course category not found", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse updateCoupon(AddCouponsRequest addCouponsRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    if (addCouponsRequest.getCoupon_code() == null || addCouponsRequest.getCoupon_code().isEmpty()) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    } else if (addCouponsRequest.getCoupon_description() == null || addCouponsRequest.getCoupon_description().isEmpty()) {
                        throw new ErrorException("Please add a description", VarList.RSP_NO_DATA_FOUND);
                    } else if (addCouponsRequest.getPromotion_type() == 0) {
                        throw new ErrorException("Please add a promotion type", VarList.RSP_NO_DATA_FOUND);
                    } else if (addCouponsRequest.getAmount() == 0.0) {
                        throw new ErrorException("Please add a amount", VarList.RSP_NO_DATA_FOUND);
                    } else if (addCouponsRequest.getEx_date() == null) {
                        throw new ErrorException("please add a expire date", VarList.RSP_NO_DATA_FOUND);
                    }
                    PromotionCoupon promotionCoupon = promotionCouponRepository.getPromotionCouponByCode(addCouponsRequest.getCoupon_code());
                    if (promotionCoupon == null) {
                        throw new ErrorException("Course coupon not available", VarList.RSP_NO_DATA_FOUND);
                    }
                    promotionCoupon.setAmount(addCouponsRequest.getAmount());
                    promotionCoupon.setDescription(addCouponsRequest.getCoupon_description());
                    promotionCoupon.setExpire_date(addCouponsRequest.getEx_date());
                    PromotionType promotionType = promotionTypeRepository.getPromotionTypeById(addCouponsRequest.getPromotion_type());
                    if (promotionType == null) {
                        throw new ErrorException("Course coupon type not available", VarList.RSP_NO_DATA_FOUND);
                    }
                    promotionCoupon.setPromotionType(promotionType);
                    promotionCouponRepository.save(promotionCoupon);
                    successResponse.setMessage("Course coupon updated successfully");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;
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
    public SuccessResponse setCouponDeactive(String couponCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    PromotionCoupon promotionCoupon = promotionCouponRepository.getPromotionCouponByCode(couponCode);
                    if (promotionCoupon == null) {
                        throw new ErrorException("Course coupon not available", VarList.RSP_NO_DATA_FOUND);
                    }
                    promotionCoupon.setIs_active((byte) 2);
                    promotionCouponRepository.save(promotionCoupon);
                    successResponse.setMessage("Course coupon updated successfully");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;
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
    public SuccessResponse setCouponActive(String couponCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    PromotionCoupon promotionCoupon = promotionCouponRepository.getPromotionCouponByCode(couponCode);
                    if (promotionCoupon == null) {
                        throw new ErrorException("Course coupon not available", VarList.RSP_NO_DATA_FOUND);
                    }
                    promotionCoupon.setIs_active((byte) 1);
                    promotionCouponRepository.save(promotionCoupon);
                    successResponse.setMessage("Course coupon updated successfully");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;
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

    public List<DiscountType> getAllDiscountType() {
        List<DiscountType> discountTypes = discountTypeRepository.findAll();
        if (discountTypes.size() > 0) {
            return discountTypes;
        } else {
            throw new ErrorException("Discount types not available", VarList.RSP_NO_DATA_FOUND);
        }
    }

    public List<PromotionType> getAllPromotionType() {
        List<PromotionType> promotionTypes = promotionTypeRepository.findAll();
        if (promotionTypes.size() > 0) {
            return promotionTypes;
        } else {
            throw new ErrorException("Promotion types not available", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public List<GetCountriesResponse> getCountries() {
        List<PriceSetup> priceSetups = priceSetupRepository.findAll();
        List<GetCountriesResponse> CountriesResponsesList = new ArrayList<>();
        if (priceSetups.size() > 0) {
            for (PriceSetup priceSetup : priceSetups) {
                GetCountriesResponse getCountriesResponse = new GetCountriesResponse();
                getCountriesResponse.setCountry(priceSetup.getCountry().getName());
                getCountriesResponse.setCurrency(priceSetup.getCurrency().getName());
                CountriesResponsesList.add(getCountriesResponse);
            }
            return CountriesResponsesList;
        } else {
            throw new ErrorException("priceSetup not available", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse setDefaultPriceRange(SetDefaultPriceRangeRequest setDefaultPriceRangeRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        String tip = setDefaultPriceRangeRequest.getTip();
        Double minimumPrice = setDefaultPriceRangeRequest.getMinimumPrice();
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 3) {
                    if (setDefaultPriceRangeRequest.getMaxPrice() == null || setDefaultPriceRangeRequest.getMinPrice() == null) {
                        throw new ErrorException("Please add min and max prices", VarList.RSP_NO_DATA_FOUND);
                    } else if (setDefaultPriceRangeRequest.getMinPrice() == 0) {
                        throw new ErrorException("Please add a valid min price", VarList.RSP_NO_DATA_FOUND);
                    } else if (setDefaultPriceRangeRequest.getMaxPrice() == 0) {
                        throw new ErrorException("Please add a valid max price", VarList.RSP_NO_DATA_FOUND);
                    } else if (tip == null || tip.isEmpty()) {
                        tip = "N/A";
                    } else if (minimumPrice == null || minimumPrice == 0) {
                        minimumPrice = setDefaultPriceRangeRequest.getMinPrice();
                    }
//                    if (!(setDefaultPriceRangeRequest.getMinPrice() <= minimumPrice && minimumPrice <= setDefaultPriceRangeRequest.getMaxPrice())) {
//                        throw new ErrorException("Error", "Invalid minimum price because the price is not in the price range");
//                    }
                    if (!(minimumPrice > 0 && minimumPrice <= setDefaultPriceRangeRequest.getMaxPrice())) {
                        throw new ErrorException("Please add a valid minimum price", VarList.RSP_NO_DATA_FOUND);
                    }
                    PriceSetup priceSetup = priceSetupRepository.getPriceSetupByCountryId(countryRepository.getCountryByName("Other Countries").getId());
                    priceSetup.setMaxPrice(setDefaultPriceRangeRequest.getMaxPrice());
                    priceSetup.setMinPrice(setDefaultPriceRangeRequest.getMinPrice());
                    priceSetup.setTip(tip);
                    priceSetup.setMinimumPrice(minimumPrice);
                    priceSetupRepository.save(priceSetup);
                    successResponse.setMessage("Default countries price range updated successfully");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;
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
    public AddCourseSectionResponse addSection(AddSectionRequest addSectionRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    final String courseCode = addSectionRequest.getCourseCode();
                    final String sectionName = addSectionRequest.getSectionName();
                    if (courseCode == null || courseCode.isEmpty() || sectionName == null || sectionName.isEmpty()) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    }
                    Course course = courseRepository.getCourseByCode(courseCode);
                    if (course == null) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    }
                    CourseSection courseSection = courseSectionRepository.getCourseSectionByCourseAndSectionName(course, sectionName);
                    if (courseSection != null) {
                        throw new ErrorException("Already added", VarList.RSP_NO_DATA_FOUND);
                    }

                    List<CourseSection> courseSections = courseSectionRepository.getCourseSectionByCourse(course);

                    int i = 0;
                    int previousIndex = 0;
                    for (CourseSection itemObj : courseSections) {
                        if (itemObj.getArrangedNo() == null) {
                            i++;
                            itemObj.setArrangedNo(i);
                            courseSectionRepository.save(itemObj);
                            previousIndex++;
                        } else {
                            if (itemObj.getArrangedNo() > previousIndex) {
                                previousIndex = itemObj.getArrangedNo();
                            }
                        }
                    }
                    previousIndex++;

                    courseSection = new CourseSection();
                    courseSection.setCourse(course);
                    courseSection.setSectionName(sectionName);
                    courseSection.setArrangedNo(previousIndex);
                    courseSectionRepository.save(courseSection);


                    CourseComplete courseComplete = courseCompleteRepository.getCourseCompleteByCourse(course);

                    if (courseComplete == null) {
                        courseComplete = new CourseComplete();
                        courseComplete.setCourse(course);
                    }
                    courseComplete.setCurriculum((byte) 1);

                    courseCompleteRepository.save(courseComplete);

                    AddCourseSectionResponse addCourseSectionResponse = new AddCourseSectionResponse();
                    addCourseSectionResponse.setMessage("Course section added successfully");
                    addCourseSectionResponse.setStatusCode(VarList.RSP_SUCCESS);
                    addCourseSectionResponse.setSectionCode(courseSection.getId());
                    return addCourseSectionResponse;
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
    public AddSectionCurriculumItemResponse addSectionItem(AddSectionCurriculumItemRequest addSectionCurriculumItemRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    final String courseCode = addSectionCurriculumItemRequest.getCourseCode();
                    final String courseSection = addSectionCurriculumItemRequest.getCourseSection();
                    String article = addSectionCurriculumItemRequest.getArticle();
                    final MultipartFile video = addSectionCurriculumItemRequest.getVideo();
                    final String description = addSectionCurriculumItemRequest.getDescription();
                    final String title = addSectionCurriculumItemRequest.getTitle();
                    if (courseCode == null || courseCode.isEmpty() || courseSection == null || courseSection.isEmpty() || description == null || description.isEmpty() || title == null || title.isEmpty() || ((article != null && !article.isEmpty()) && (video != null && !video.isEmpty())) || ((article == null || article.isEmpty()) && (video == null || video.isEmpty()))) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    }
                    Course course = courseRepository.getCourseByCode(courseCode);
                    if (course == null) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    }
                    CourseSection getcourseSection = courseSectionRepository.getCourseSectionByCourseAndId(course, Integer.parseInt(courseSection));
                    if (getcourseSection == null) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    }
                    SectionCurriculumItem item = sectionCurriculumItemRepository.getSectionCurriculumItemByCourseSectionAndTitle(getcourseSection, title);
                    if (item != null) {
                        throw new ErrorException("Already added", VarList.RSP_NO_DATA_FOUND);
                    }

                    List<SectionCurriculumItem> sectionCurriculumItems = sectionCurriculumItemRepository.getSectionCurriculumItemsByCourseSection(getcourseSection);

                    int i = 0;
                    int previousIndex = 0;
                    for (SectionCurriculumItem itemObj : sectionCurriculumItems) {
                        if (itemObj.getArrangedNo() == null) {
                            i++;
                            itemObj.setArrangedNo(i);
                            sectionCurriculumItemRepository.save(itemObj);
                        } else {
                            if (itemObj.getArrangedNo() > previousIndex) {
                                previousIndex = itemObj.getArrangedNo();
                            }
                        }
                    }
                    previousIndex++;
                    item = new SectionCurriculumItem();
                    article = addSectionCurriculumItemRequest.getArticle() == null || addSectionCurriculumItemRequest.getArticle().isEmpty() ? "N/A" : addSectionCurriculumItemRequest.getArticle();
                    item.setArticle(article);
                    item.setDescription(description);
                    item.setTitle(title);
                    item.setCourseSection(getcourseSection);
                    item.setArrangedNo(previousIndex);
                    sectionCurriculumItemRepository.save(item);
                    if (video != null && !video.isEmpty()) {
                        validateVideoFile(video);

                        FileUploadResponse videoUploadResponse = saveFile(video);
                        CurriculumItemFile curriculumItemFile = new CurriculumItemFile();
                        curriculumItemFile.setTitle(videoUploadResponse.getUrl());
                        curriculumItemFile.setUrl(videoUploadResponse.getFilename());
                        curriculumItemFile.setCurriculumItemFileTypes(curriculumItemFileTypeRepository.getCurriculumItemFileTypeByName("Video"));
                        curriculumItemFile.setSectionCurriculumItem(item);
                        curriculumItemFileRepository.save(curriculumItemFile);
                    }


                    CourseComplete courseComplete = courseCompleteRepository.getCourseCompleteByCourse(course);

                    if (courseComplete == null) {
                        courseComplete = new CourseComplete();
                        courseComplete.setCourse(course);
                    }
                    courseComplete.setCurriculum((byte) 1);

                    courseCompleteRepository.save(courseComplete);

                    AddSectionCurriculumItemResponse addSectionCurriculumItemResponse = new AddSectionCurriculumItemResponse();
                    addSectionCurriculumItemResponse.setMessage("Section curriculum item added successfully");
                    addSectionCurriculumItemResponse.setStatusCode(VarList.RSP_SUCCESS);
                    addSectionCurriculumItemResponse.setSectionItemCode(item.getId());
                    return addSectionCurriculumItemResponse;
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
    public SuccessResponse addCurriculumItemFile(AddCurriculumItemFileRequest addCurriculumItemFileRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    final String courseCode = addCurriculumItemFileRequest.getCourseCode();
                    final String sectionCode = addCurriculumItemFileRequest.getSectionCode();
                    final Integer curriculumItemCode = addCurriculumItemFileRequest.getCurriculumItemCode();
                    final String title = addCurriculumItemFileRequest.getTitle() == null || addCurriculumItemFileRequest.getTitle().isEmpty() ? "N/A" : addCurriculumItemFileRequest.getTitle();
                    final String url = addCurriculumItemFileRequest.getUrl();
                    final MultipartFile video = addCurriculumItemFileRequest.getVideo();
                    final MultipartFile sourceCode = addCurriculumItemFileRequest.getSourceCode();
                    final MultipartFile downloadableFile = addCurriculumItemFileRequest.getDownloadableFile();

                    if (courseCode == null || courseCode.isEmpty() || sectionCode == null || sectionCode.isEmpty() || curriculumItemCode == null || curriculumItemCode.toString().isEmpty() || url == null || url.isEmpty() || sourceCode == null || sourceCode.isEmpty() || downloadableFile == null || downloadableFile.isEmpty()) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    }
                    Course course = courseRepository.getCourseByCode(courseCode);
                    if (course == null) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    }
                    CourseSection courseSection = courseSectionRepository.getCourseSectionByCourseAndId(course, Integer.parseInt(sectionCode));
                    if (courseSection == null) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    }
                    SectionCurriculumItem item = sectionCurriculumItemRepository.getSectionCurriculumItemById(curriculumItemCode);
                    if (item == null) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    }
                    CurriculumItemFile curriculumItemFile = new CurriculumItemFile();
                    curriculumItemFile.setTitle(title);
                    curriculumItemFile.setUrl(url);
                    curriculumItemFile.setCurriculumItemFileTypes(curriculumItemFileTypeRepository.getCurriculumItemFileTypeByName("External Resourses"));
                    curriculumItemFile.setSectionCurriculumItem(item);


                    if (video != null && !video.isEmpty()) {

                        FileUploadResponse videoFileResponse = saveFile(video);
                        CurriculumItemFile videoFileObj = new CurriculumItemFile();
                        videoFileObj.setTitle(videoFileResponse.getUrl());
                        videoFileObj.setUrl(videoFileResponse.getFilename());
                        videoFileObj.setCurriculumItemFileTypes(curriculumItemFileTypeRepository.getCurriculumItemFileTypeByName("Video"));
                        videoFileObj.setSectionCurriculumItem(item);
                        curriculumItemFileRepository.save(videoFileObj);

                        double courseLength = 0;
                        List<CourseSection> courseSections = courseSectionRepository.getCourseSectionByCourse(course);
                        for (CourseSection courseSectionObj : courseSections) {
                            List<SectionCurriculumItem> sectionCurriculumItems = sectionCurriculumItemRepository.getSectionCurriculumItemsByCourseSection(courseSectionObj);
                            for (SectionCurriculumItem sectionCurriculumItem : sectionCurriculumItems) {
                                List<CurriculumItemFile> curriculumItemFiles = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItem(sectionCurriculumItem);
                                for (CurriculumItemFile curriculumItemFileObj : curriculumItemFiles) {
                                    if (curriculumItemFileObj.getCurriculumItemFileTypes().getId() == 3) {
                                        courseLength += curriculumItemFileObj.getVideoLength();
                                    }
                                }
                            }
                        }

                        if (courseLength != 0) {
                            course.setCourseLength(courseLength);
                            courseRepository.save(course);
                        }

                    }

                    if (downloadableFile != null && !downloadableFile.isEmpty()) {

                        FileUploadResponse downloadableFileResponse = saveFile(downloadableFile);
                        CurriculumItemFile downloadableFileObj = new CurriculumItemFile();
                        downloadableFileObj.setTitle(downloadableFileResponse.getUrl());
                        downloadableFileObj.setUrl(downloadableFileResponse.getFilename());
                        downloadableFileObj.setCurriculumItemFileTypes(curriculumItemFileTypeRepository.getCurriculumItemFileTypeByName("Downloadable Items"));
                        downloadableFileObj.setSectionCurriculumItem(item);
                        curriculumItemFileRepository.save(downloadableFileObj);
                    }
                    if (sourceCode != null && !sourceCode.isEmpty()) {
                        FileUploadResponse sourseCodeResponse = saveFile(sourceCode);
                        CurriculumItemFile SourseCodeObj = new CurriculumItemFile();
                        SourseCodeObj.setTitle(sourseCodeResponse.getUrl());
                        SourseCodeObj.setUrl(sourseCodeResponse.getFilename());
                        SourseCodeObj.setCurriculumItemFileTypes(curriculumItemFileTypeRepository.getCurriculumItemFileTypeByName("Source Code"));
                        SourseCodeObj.setSectionCurriculumItem(item);
                        curriculumItemFileRepository.save(SourseCodeObj);
                    }

                    successResponse.setMessage("Curriculum item file added successfully");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;
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
    public SuccessResponse addLecture(AddLectureRequest addLectureRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    String title = addLectureRequest.getTitle();
                    int CourseSectionID = addLectureRequest.getCourseSectionId();
                    if (title == null || title.isEmpty() || CourseSectionID == 0) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    }
                    CourseSection courseSection = courseSectionRepository.getCourseSectionById(addLectureRequest.getCourseSectionId());
                    if (courseSection != null) {
                        CurriculumItemType curriculumItemType = curriculumItemTypeRepository.getCurriculumItemTypeById(1);
                        if (curriculumItemType != null) {
                            SectionCurriculumItem sectionCurriculumItem = sectionCurriculumItemRepository.getSectionCurriculumItemByCourseSectionAndTitleAndCurriculumItemType(courseSection, title, curriculumItemType);
                            if (sectionCurriculumItem == null) {
                                sectionCurriculumItem = new SectionCurriculumItem();
                            } else {
                                if (sectionCurriculumItem.getIsDelete() == null || sectionCurriculumItem.getIsDelete() == 0) {
                                    throw new ErrorException("The lecture has already been added", VarList.RSP_NO_DATA_FOUND);
                                }
                            }
                            sectionCurriculumItem.setArticle("N/A");
                            sectionCurriculumItem.setDescription("N/A");
                            sectionCurriculumItem.setTitle(title);
                            sectionCurriculumItem.setCourseSection(courseSection);
                            sectionCurriculumItem.setCurriculumItemType(curriculumItemType);
                            sectionCurriculumItem.setIsDelete((byte) 0);
                            sectionCurriculumItemRepository.save(sectionCurriculumItem);
                            updateCourseProgress(courseSection.getCourse());
                            successResponse.setMessage("Lesson added successfully");
                            successResponse.setVariable(VarList.RSP_SUCCESS);
                            return successResponse;
                        } else {
                            throw new ErrorException("Syllabus item type not found", VarList.RSP_NO_DATA_FOUND);
                        }
                    } else {
                        throw new ErrorException("Course section not found", VarList.RSP_NO_DATA_FOUND);
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
    public List<GetLectureResponse> getAllLectures(String courseSectionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2 || profile.getGupType().getId() == 3) {
                    CourseSection courseSection = courseSectionRepository.getCourseSectionById(Integer.parseInt(courseSectionId));
                    if (courseSection != null) {
                        CurriculumItemType curriculumItemType = curriculumItemTypeRepository.getCurriculumItemTypeById(1);
                        List<SectionCurriculumItem> item = sectionCurriculumItemRepository.getSectionCurriculumItemsByCourseSectionAndCurriculumItemType(courseSection, curriculumItemType);
                        List<GetLectureResponse> getLectureResponses = new ArrayList<>();
                        if (item.size() > 0) {
                            for (SectionCurriculumItem sectionCurriculumItem : item) {
                                GetLectureResponse getLectureResponse = new GetLectureResponse();
                                getLectureResponse.setId(sectionCurriculumItem.getId());
                                getLectureResponse.setTitle(sectionCurriculumItem.getTitle());
                                getLectureResponse.setCourseSection(sectionCurriculumItem.getCourseSection().getSectionName());

                                getLectureResponses.add(getLectureResponse);
                            }
                            return getLectureResponses;
                        } else {
                            throw new ErrorException("Lecture not found", VarList.RSP_NO_DATA_FOUND);
                        }
                    } else {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    }
                } else {
                    throw new ErrorException("You are not a instructor or admin to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse addQuiz(AddQuizRequest addQuizRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    String title = addQuizRequest.getTitle();
                    String description = addQuizRequest.getDescription();
                    int CourseSectionID = addQuizRequest.getCourseSectionId();
                    if (title == null || title.isEmpty() || CourseSectionID == 0) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    }
                    CourseSection courseSection = courseSectionRepository.getCourseSectionById(addQuizRequest.getCourseSectionId());
                    if (courseSection != null) {
                        CurriculumItemType curriculumItemType = curriculumItemTypeRepository.getCurriculumItemTypeById(2);
                        if (curriculumItemType != null) {
                            SectionCurriculumItem sectionCurriculumItem = sectionCurriculumItemRepository.getSectionCurriculumItemByCourseSectionAndTitleAndCurriculumItemType(courseSection, title, curriculumItemType);
                            if (sectionCurriculumItem == null) {
                                sectionCurriculumItem = new SectionCurriculumItem();
                                sectionCurriculumItem.setArticle("N/A");
                                sectionCurriculumItem.setDescription((description == null || description.isEmpty()) ? "" : description);
                                sectionCurriculumItem.setTitle(title);
                                sectionCurriculumItem.setCourseSection(courseSection);
                                sectionCurriculumItem.setCurriculumItemType(curriculumItemType);
                                sectionCurriculumItemRepository.save(sectionCurriculumItem);
                                updateCourseProgress(courseSection.getCourse());
                                successResponse.setMessage("Quiz added successfully");
                                successResponse.setVariable(VarList.RSP_SUCCESS);
                                return successResponse;
                            } else {
                                throw new ErrorException("The quiz has already been added", VarList.RSP_NO_DATA_FOUND);
                            }
                        } else {
                            throw new ErrorException("Syllabus item type not found", VarList.RSP_NO_DATA_FOUND);
                        }
                    } else {
                        throw new ErrorException("Course section not found", VarList.RSP_NO_DATA_FOUND);
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

    public SuccessResponse addCourseDefaultPrice(AddDefaultPrice addDefaultPrice) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    final String globalListPrice = addDefaultPrice.getGlobalListPrice();
                    final String courseCode = addDefaultPrice.getCourseCode();
                    final int discountTypeId = addDefaultPrice.getDiscountType();
                    final String discountAmount = addDefaultPrice.getDiscountAmount();
                    final String globalNetPrice = addDefaultPrice.getGlobalNetPrice();

                    if (courseCode == null || courseCode.isEmpty() || globalListPrice == null || globalListPrice.isEmpty() || discountTypeId == 0 || discountAmount == null || discountAmount.isEmpty() || globalNetPrice == null || globalNetPrice.isEmpty()) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    }
                    Course course = courseRepository.getCourseByCode(courseCode);
                    if (course == null) {
                        throw new ErrorException("Course not found", VarList.RSP_NO_DATA_FOUND);
                    }
                    Country country = countryRepository.getCountryById(30);
                    CoursePrice coursePrice = coursePriceRepository.getCoursePriceByCourseAndCountry(course, country);
                    PriceSetup priceSetup = priceSetupRepository.getPriceSetupByCountryId(30);
                    Double globalNetPriceVal = Double.parseDouble(globalNetPrice);

//                    if (!(globalNetPriceVal >= priceSetup.getMinPrice() && globalNetPriceVal <= priceSetup.getMaxPrice())) {
                    if (!(globalNetPriceVal > 0 && globalNetPriceVal <= priceSetup.getMaxPrice())) {
                        throw new ErrorException("The global net price is not in the price range", VarList.RSP_NO_DATA_FOUND);
                    }
                    DiscountType discountType = discountTypeRepository.getDiscountTypeById(discountTypeId);
                    if (coursePrice == null) {
                        coursePrice = new CoursePrice();
                        coursePrice.setDiscountValue(Double.parseDouble(discountAmount));
                        coursePrice.setValue(Double.parseDouble(globalListPrice));
                        coursePrice.setCountry(country);
                        coursePrice.setCourse(course);
                        coursePrice.setCurrency(priceSetup.getCurrency());
                        coursePrice.setDiscountType(discountType);
                        coursePriceRepository.save(coursePrice);
                        successResponse.setMessage("Course default price added successfully");
                        successResponse.setVariable(VarList.RSP_SUCCESS);
                        return successResponse;
                    } else {
                        coursePrice.setDiscountValue(Double.parseDouble(discountAmount));
                        coursePrice.setValue(Double.parseDouble(globalListPrice));
                        coursePrice.setCountry(country);
                        coursePrice.setCourse(course);
                        coursePrice.setCurrency(priceSetup.getCurrency());
                        coursePrice.setDiscountType(discountType);
                        coursePriceRepository.save(coursePrice);

                        CourseComplete courseComplete = courseCompleteRepository.getCourseCompleteByCourse(course);

                        if (courseComplete == null) {
                            courseComplete = new CourseComplete();
                            courseComplete.setCourse(course);
                        }
                        courseComplete.setPricing((byte) 1);

                        courseCompleteRepository.save(courseComplete);

                        successResponse.setMessage("Course default price updated successfully");
                        successResponse.setVariable(VarList.RSP_SUCCESS);
                        return successResponse;
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

    public SuccessResponse addQuestionAndAnswers(AddQuestionAndAnswersRequest addQuestionAndAnswersRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);

        if (profile == null || profile.getIsActive() != 1) {
            throw new ErrorException("User not active or not found", VarList.RSP_NO_DATA_FOUND);
        }

        if (profile.getGupType().getId() != 2) {
            throw new ErrorException("You are not an instructor to perform this operation", VarList.RSP_NO_DATA_FOUND);
        }

        final String question = addQuestionAndAnswersRequest.getQuestion();
        final String sectionCurriculumItemId = addQuestionAndAnswersRequest.getSectionCurriculumItemId();
        final String[] answers = {
                addQuestionAndAnswersRequest.getAnswer1(),
                addQuestionAndAnswersRequest.getAnswer2(),
                addQuestionAndAnswersRequest.getAnswer3(),
                addQuestionAndAnswersRequest.getAnswer4(),
                addQuestionAndAnswersRequest.getAnswer5()
        };
        final String[] explanations = {
                addQuestionAndAnswersRequest.getExplanation1(),
                addQuestionAndAnswersRequest.getExplanation2(),
                addQuestionAndAnswersRequest.getExplanation3(),
                addQuestionAndAnswersRequest.getExplanation4(),
                addQuestionAndAnswersRequest.getExplanation5()
        };
        final Integer correctAnswer = addQuestionAndAnswersRequest.getCorrectAnswer();

        if (question == null || question.isEmpty() || sectionCurriculumItemId == null || sectionCurriculumItemId.isEmpty()) {
            throw new ErrorException("Invalid request details", VarList.RSP_NO_DATA_FOUND);
        }

        if (correctAnswer == null || correctAnswer.toString().isEmpty() || answers.length == 0)
            throw new ErrorException("Please add a correct answer", VarList.RSP_NO_DATA_FOUND);

        SectionCurriculumItem sectionCurriculumItem = sectionCurriculumItemRepository.getSectionCurriculumItemById(Integer.parseInt(sectionCurriculumItemId));
        if (sectionCurriculumItem == null)
            throw new ErrorException("Invalid section curriculum item id", VarList.RSP_NO_DATA_FOUND);

        if (!sectionCurriculumItem.getCourseSection().getCourse().getInstructorId().getGeneralUserProfile().equals(profile))
            throw new ErrorException("You cannot perform this operation because this course is not yours", VarList.RSP_NO_DATA_FOUND);


        Quiz quiz = new Quiz();
        quiz.setQuestion(question);
        quiz.setSectionCurriculumItem(sectionCurriculumItem);
        quiz.setIsDelete((byte) 0);
        quizRepository.save(quiz);

        for (int i = 0; i < answers.length; i++) {
            if (answers[i] != null && !answers[i].isEmpty()) {
                Answer answer = new Answer();
                answer.setName(answers[i]);
                answer.setExplanation((explanations[i] != null && !explanations[i].isEmpty()) ? explanations[i] : "");
                answer.setCorrectAnswer(correctAnswer != null && correctAnswer == (i + 1));
                answer.setQuiz(quiz);
                answerRepository.save(answer);
            }
        }
        successResponse.setMessage("Question and answers added successfully");
        successResponse.setVariable(VarList.RSP_SUCCESS);
        return successResponse;
    }


    @Override
    public CoursePricingResponse getDefaultCoursePricing(String code) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);

        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2 || profile.getGupType().getId() == 3) {

                    CoursePrice coursePrice = coursePriceRepository.getCoursePriceByCourseCodeAndCountryIdAndCurrencyId(code, 30, 30);
                    if (coursePrice != null) {
                        PriceSetup priceSetup = priceSetupRepository.getPriceSetupByCountryIdAndCurrencyId(30, 30);
                        if (priceSetup != null) {
                            CoursePricingResponse response = new CoursePricingResponse();
                            response.setValue(coursePrice.getValue());
                            response.setDiscountValue(coursePrice.getDiscountValue());
                            response.setCurrency(coursePrice.getCurrency().getName());
                            response.setCountry(coursePrice.getCountry().getName());
                            response.setDiscountType(coursePrice.getDiscountType().getName());
                            response.setDiscountTypeId(coursePrice.getDiscountType().getId());
                            response.setMinPrice(priceSetup.getMinPrice());
                            response.setMaxPrice(priceSetup.getMaxPrice());
                            response.setTip(priceSetup.getTip());
                            response.setMinimumPrice(priceSetup.getMinimumPrice());

                            return response;
                        } else {
                            throw new ErrorException("Not available price setup for country and currency", VarList.RSP_NO_DATA_FOUND);
                        }


                    } else {
                        PriceSetup priceSetup = priceSetupRepository.getPriceSetupByCountryIdAndCurrencyId(30, 30);
                        if (priceSetup != null) {
                            CoursePricingResponse response = new CoursePricingResponse();
                            response.setValue(0);
                            response.setDiscountValue(0);
                            response.setCurrency(priceSetup.getCurrency().getName());
                            response.setCountry(priceSetup.getCountry().getName());
                            response.setDiscountType("N/A");
                            response.setDiscountTypeId(1);
                            response.setMinPrice(priceSetup.getMinPrice());
                            response.setMaxPrice(priceSetup.getMaxPrice());
                            response.setTip(priceSetup.getTip());
                            response.setMinimumPrice(priceSetup.getMinimumPrice());
                            return response;
                        } else {
                            throw new ErrorException("Not available price setup for country and currency", VarList.RSP_NO_DATA_FOUND);
                        }
                    }


                } else {
                    throw new ErrorException("You are not a instructor or admin to this operation", VarList.RSP_NO_DATA_FOUND);

                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }


    @Override
    public GetDefaultPriceResponse getCoursePricing(String code) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);

        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2 || profile.getGupType().getId() == 3) {

                    List<GetCoursePricingResponse> responseList = new ArrayList<>();
                    List<CoursePrice> coursePriceList = coursePriceRepository.getCoursePriceByCourseCode(code);
                    GetDefaultPriceResponse response = new GetDefaultPriceResponse();
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
                                PriceSetup priceSetup = priceSetupRepository.getPriceSetupByCountryId(coursePrice.getCountry().getId());
                                getCoursePricingResponse.setMinPrice(priceSetup.getMinPrice());
                                getCoursePricingResponse.setMaxPrice(priceSetup.getMaxPrice());
                                getCoursePricingResponse.setCurrency(priceSetup.getCurrency().getName());
                                responseList.add(getCoursePricingResponse);
                                response.setPrices(responseList);
                            }
                        }
                    }
                    List<PriceSetup> priceSetups = priceSetupRepository.findAll();
                    List<GetPriceRangeResponse> getPriceRangeResponses = new ArrayList<>();
                    for (PriceSetup priceSetup : priceSetups) {

                        GetPriceRangeResponse getPriceRangeResponse = new GetPriceRangeResponse();
                        getPriceRangeResponse.setCountryName(priceSetup.getCountry().getName());
                        getPriceRangeResponse.setMinPrice(priceSetup.getMinPrice());
                        getPriceRangeResponse.setMaxPrice(priceSetup.getMaxPrice());
                        getPriceRangeResponse.setMinimumValue(priceSetup.getMinimumPrice());
                        getPriceRangeResponse.setTip(priceSetup.getTip());
                        getPriceRangeResponses.add(getPriceRangeResponse);

                    }
                    response.setPriceRange(getPriceRangeResponses);
                    return response;

                } else {
                    throw new ErrorException("You are not a instructor or admin to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse addFreeCourse(String code) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    if (code == null || code.isEmpty()) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    }
                    Course course = courseRepository.getCourseByCode(code);
                    if (course == null) {
                        throw new ErrorException("Course not found", VarList.RSP_NO_DATA_FOUND);
                    }

                    if (!course.getInstructorId().getGeneralUserProfile().getUserCode().equals(profile.getUserCode())) {
                        throw new ErrorException("You cannot access this course because it is not yours", VarList.RSP_NO_DATA_FOUND);
                    }

                    course.setIsPaid(1);
                    courseRepository.save(course);
                    List<CoursePrice> coursePriceList = coursePriceRepository.getCoursePriceByCourse(course);
                    if (coursePriceList.size() > 0) {
                        for (CoursePrice coursePrice : coursePriceList) {
                            coursePrice.setDiscountValue((double) 0);
                            coursePrice.setValue((double) 0);
                            DiscountType discountType = discountTypeRepository.getDiscountTypeById(1);
                            coursePrice.setDiscountType(discountType);
                            coursePrice.setDiscount((double) 0);
                            coursePrice.setNetPrice((double) 0);
                            coursePriceRepository.save(coursePrice);
                        }
                        successResponse.setMessage("Changed to a free course successfully");
                    } else {
                        CoursePrice coursePrice = new CoursePrice();
                        coursePrice.setDiscountValue((double) 0);
                        coursePrice.setValue((double) 0);
                        Country country = countryRepository.getCountryById(30);
                        coursePrice.setCountry(country);
                        coursePrice.setCourse(course);
                        PriceSetup priceSetup = priceSetupRepository.getPriceSetupByCountryId(30);
                        coursePrice.setCurrency(priceSetup.getCurrency());
                        DiscountType discountType = discountTypeRepository.getDiscountTypeById(1);
                        coursePrice.setDiscountType(discountType);
                        coursePrice.setDiscount((double) 0);
                        coursePrice.setNetPrice((double) 0);
                        coursePriceRepository.save(coursePrice);
                        successResponse.setMessage("New free course price created successfully");
                    }

                    CourseComplete courseComplete = courseCompleteRepository.getCourseCompleteByCourse(course);
                    if (courseComplete == null) {
                        courseComplete = new CourseComplete();
                    }
                    courseComplete.setPricing((byte) 1);
                    courseComplete.setCourse(course);
                    courseCompleteRepository.save(courseComplete);

                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;

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
    public SuccessResponse getFreeCourse(String code) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2 || profile.getGupType().getId() == 3) {
                    Course course = courseRepository.getCourseByCode(code);
                    if (code == null || code.isEmpty()) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    } else {
                        if (course.getIsPaid() == 1) {
                            successResponse.setMessage("1");
                        } else {
                            successResponse.setMessage("2");
                        }
                        successResponse.setVariable(VarList.RSP_SUCCESS);
                        return successResponse;
                    }
                } else {
                    throw new ErrorException("You are not a instructor or admin to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse ownThisCourse(String code) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    if (code == null || code.isEmpty()) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    } else {
                        Course course = courseRepository.getCourseByCode(code);
                        if (course == null) {
                            throw new ErrorException("Invalid course", VarList.RSP_NO_DATA_FOUND);
                        } else {
                            InstructorProfile instructorId = course.getInstructorId();
                            InstructorProfile instructorProfileByGeneralUserProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(profile);
                            if (instructorProfileByGeneralUserProfile == instructorId) {
                                if (course.getIsOwned() == 1) {
                                    throw new ErrorException("You have already claimed your ownership for this cause", VarList.RSP_NO_DATA_FOUND);
                                } else {
                                    course.setIsOwned((byte) 1);
                                    courseRepository.save(course);
                                    successResponse.setMessage("Your ownership to this Course has been confirmed");
                                    successResponse.setVariable(VarList.RSP_SUCCESS);
                                    return successResponse;
                                }
                            } else {
                                throw new ErrorException("You don't own this course to do this operation", VarList.RSP_NO_DATA_FOUND);
                            }
                        }
                    }
                } else {
                    throw new ErrorException("You are not a Instructor to do this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse addDownloadableFile(AddDownloadableFileRequest addDownloadableFileRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    final String curriculumItemId = addDownloadableFileRequest.getCurriculumItemId();
                    final String downloadableFileGeneratedName = addDownloadableFileRequest.getDownloadableFileGeneratedName();
                    final String downloadableFileOriginalName = addDownloadableFileRequest.getDownloadableFileOriginalName();
                    if (curriculumItemId == null || curriculumItemId.isEmpty()) {
                        throw new ErrorException("Please add a curriculum Item Id", VarList.RSP_NO_DATA_FOUND);
                    } else if (downloadableFileGeneratedName == null || downloadableFileGeneratedName.isEmpty() || downloadableFileOriginalName == null || downloadableFileOriginalName.isEmpty()) {
                        throw new ErrorException("Please add a downloadable file", VarList.RSP_NO_DATA_FOUND);
                    }
                    SectionCurriculumItem sectionCurriculumItem = sectionCurriculumItemRepository.getSectionCurriculumItemById(Integer.parseInt(curriculumItemId));
                    if (sectionCurriculumItem == null) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    }
                    CurriculumItemFileType curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(1);
                    if (curriculumItemFileType == null) {
                        throw new ErrorException("Curriculum item file type not available", VarList.RSP_NO_DATA_FOUND);
                    }

                    String path = resolveFilePath(curriculumItemFileType.getId());

                    CurriculumItemFile curriculumItemFile = new CurriculumItemFile();
                    curriculumItemFile.setCurriculumItemFileTypes(curriculumItemFileType);
                    curriculumItemFile.setSectionCurriculumItem(sectionCurriculumItem);

                    curriculumItemFile.setUrl(path + downloadableFileGeneratedName);
                    curriculumItemFile.setTitle(downloadableFileOriginalName);
                    curriculumItemFileRepository.save(curriculumItemFile);

                    successResponse.setMessage("Downloadable file added successfully");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;
                } else {
                    throw new ErrorException("You are not a instructor to do this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse addExternalResource(AddExternalResourceRequest addExternalResourceRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    final String curriculumItemId = addExternalResourceRequest.getCurriculumItemId();
                    final String title = addExternalResourceRequest.getTitle();
                    final String url = addExternalResourceRequest.getUrl();
                    if (curriculumItemId == null || curriculumItemId.isEmpty()) {
                        throw new ErrorException("Please add a curriculum item id", VarList.RSP_NO_DATA_FOUND);
                    } else if (title == null || title.isEmpty()) {
                        throw new ErrorException("Please add a title", VarList.RSP_NO_DATA_FOUND);
                    } else if (url == null || url.isEmpty()) {
                        throw new ErrorException("Please add a url", VarList.RSP_NO_DATA_FOUND);
                    }
                    SectionCurriculumItem sectionCurriculumItem = sectionCurriculumItemRepository.getSectionCurriculumItemById(Integer.parseInt(curriculumItemId));
                    if (sectionCurriculumItem == null) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    }
                    CurriculumItemFileType curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(4);
                    if (curriculumItemFileType == null) {
                        throw new ErrorException("Curriculum item file type not available", VarList.RSP_NO_DATA_FOUND);
                    }

                    CurriculumItemFile curriculumItemFile = new CurriculumItemFile();
                    curriculumItemFile.setTitle(title);
                    curriculumItemFile.setUrl(url);
                    curriculumItemFile.setCurriculumItemFileTypes(curriculumItemFileType);
                    curriculumItemFile.setSectionCurriculumItem(sectionCurriculumItem);

                    curriculumItemFileRepository.save(curriculumItemFile);
                    successResponse.setMessage("External resource added successfully");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;
                } else {
                    throw new ErrorException("You are not a instructor to do this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse addSourceCode(AddSourceCodeRequest addSourceCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    final String curriculumItemId = addSourceCode.getCurriculumItemId();
                    final String sourceCodeGeneratedName = addSourceCode.getSourceCodeGeneratedName();
                    final String sourceCodeOriginalName = addSourceCode.getSourceCodeOriginalName();
                    if (curriculumItemId == null || curriculumItemId.isEmpty()) {
                        throw new ErrorException("Please add a curriculum item id", VarList.RSP_NO_DATA_FOUND);
                    } else if (sourceCodeGeneratedName == null || sourceCodeGeneratedName.isEmpty() || sourceCodeOriginalName == null || sourceCodeOriginalName.isEmpty()) {
                        throw new ErrorException("Please add a source code", VarList.RSP_NO_DATA_FOUND);
                    }
                    SectionCurriculumItem sectionCurriculumItem = sectionCurriculumItemRepository.getSectionCurriculumItemById(Integer.parseInt(curriculumItemId));
                    if (sectionCurriculumItem == null) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    }
                    CurriculumItemFileType curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(2);
                    if (curriculumItemFileType == null) {
                        throw new ErrorException("Curriculum item file type not available", VarList.RSP_NO_DATA_FOUND);
                    }

                    AddCurriculumDownloadableFileRequest sourceCodeRequest = new AddCurriculumDownloadableFileRequest();
                    sourceCodeRequest.setCurriculumItem(sectionCurriculumItem);
                    sourceCodeRequest.setCurriculumItemFileType(curriculumItemFileType);
                    sourceCodeRequest.setDownloadableFileGeneratedName(sourceCodeGeneratedName);
                    sourceCodeRequest.setDownloadableFileOriginalName(sourceCodeOriginalName);
                    addCurriculumDownloadableFile(sourceCodeRequest);

                    successResponse.setMessage("Source code added successfully");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;

                } else {
                    throw new ErrorException("You are not a instructor to do this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse addVideo(AddVideoRequest addVideoRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    final String curriculumItemId = addVideoRequest.getCurriculumItemId();
                    final String video = addVideoRequest.getVideo();
                    final String originalFileName = addVideoRequest.getOriginalFileName();
                    final Double videoLength = addVideoRequest.getVideoLength();
                    if (curriculumItemId == null || curriculumItemId.isEmpty()) {
                        throw new ErrorException("Please add a curriculum item id", VarList.RSP_NO_DATA_FOUND);
                    } else if (video == null || video.isEmpty()) {
                        throw new ErrorException("Please add a video", VarList.RSP_NO_DATA_FOUND);
                    } else if (videoLength == null || videoLength.toString().isEmpty()) {
                        throw new ErrorException("Please add a video length", VarList.RSP_NO_DATA_FOUND);
                    } else if (originalFileName == null || originalFileName.isEmpty()) {
                        throw new ErrorException("Please add a video original file name", VarList.RSP_NO_DATA_FOUND);
                    }
                    SectionCurriculumItem sectionCurriculumItem = sectionCurriculumItemRepository.getSectionCurriculumItemById(Integer.parseInt(curriculumItemId));
                    if (sectionCurriculumItem == null) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    }

                    if (sectionCurriculumItem.getCourseSection().getCourse().getInstructorId().getGeneralUserProfile() != profile) {
                        throw new ErrorException("You cannot do this process because it is not yours", VarList.RSP_NO_DATA_FOUND);
                    }

                    CurriculumItemFileType curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(3);
                    if (curriculumItemFileType == null) {
                        throw new ErrorException("Curriculum item file type not available", VarList.RSP_NO_DATA_FOUND);
                    }
                    CurriculumItemFile curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(sectionCurriculumItem, curriculumItemFileType);
                    try {
                        if (curriculumItemFile != null && curriculumItemFile.getUrl() != null && !curriculumItemFile.getUrl().isEmpty()) {
                            Files.delete(Paths.get(Config.UPLOAD_URL + curriculumItemFile.getUrl()));
                            successResponse.setMessage("Video updated successfully");
                        } else {
                            curriculumItemFile = new CurriculumItemFile();
                            curriculumItemFile.setCurriculumItemFileTypes(curriculumItemFileType);
                            curriculumItemFile.setSectionCurriculumItem(sectionCurriculumItem);
                            curriculumItemFile.setIsPreviewVideo((byte) 0);
                            successResponse.setMessage("Video added successfully");
                        }
                        curriculumItemFile.setTitle(originalFileName);
                        curriculumItemFile.setUrl(Config.LESSON_VIDEO_UPLOAD_URL + video);
                        curriculumItemFile.setVideoLength(videoLength);
                        curriculumItemFileRepository.save(curriculumItemFile);

                        double courseLength = 0;
                        Course course = sectionCurriculumItem.getCourseSection().getCourse();
                        List<CourseSection> courseSections = courseSectionRepository.getCourseSectionByCourse(course);
                        for (CourseSection courseSectionObj : courseSections) {
                            List<SectionCurriculumItem> sectionCurriculumItems = sectionCurriculumItemRepository.getSectionCurriculumItemsByCourseSection(courseSectionObj);
                            for (SectionCurriculumItem sectionCurriculumItemObj : sectionCurriculumItems) {
                                List<CurriculumItemFile> curriculumItemFiles = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItem(sectionCurriculumItemObj);
                                for (CurriculumItemFile curriculumItemFileObj : curriculumItemFiles) {
                                    if (curriculumItemFileObj.getCurriculumItemFileTypes().getId() == 3) {
                                        courseLength += (curriculumItemFileObj.getVideoLength() == null ? 0 : curriculumItemFileObj.getVideoLength());
                                    }
                                }
                            }
                        }

                        if (courseLength != 0) {
                            course.setCourseLength(courseLength);
                            courseRepository.save(course);
                        }

                        updateCourseProgress(course);

                        successResponse.setVariable(VarList.RSP_SUCCESS);
                        return successResponse;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    throw new ErrorException("You are not a instructor to do this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse addArticle(AddArticleRequest addArticleRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    final String curriculumItemId = addArticleRequest.getCurriculumItemId();
                    final String article = addArticleRequest.getArticle();
                    if (curriculumItemId == null || curriculumItemId.isEmpty()) {
                        throw new ErrorException("Please add a curriculum item id", VarList.RSP_NO_DATA_FOUND);
                    } else if (article == null || article.isEmpty()) {
                        throw new ErrorException("Please add a text-based lecture", VarList.RSP_NO_DATA_FOUND);
                    }
                    SectionCurriculumItem sectionCurriculumItem = sectionCurriculumItemRepository.getSectionCurriculumItemById(Integer.parseInt(curriculumItemId));
                    if (sectionCurriculumItem == null) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    }
                    sectionCurriculumItem.setArticle(article);
                    sectionCurriculumItemRepository.save(sectionCurriculumItem);
                    updateCourseProgress(sectionCurriculumItem.getCourseSection().getCourse());
                    successResponse.setMessage("Text based lecture added successfully");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;
                } else {
                    throw new ErrorException("You are not a instructor to do this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse addDescription(AddDescriptionRequest addDescriptionRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    final String curriculumItemId = addDescriptionRequest.getCurriculumItemId();
                    final String description = addDescriptionRequest.getDescription();
                    if (curriculumItemId == null || curriculumItemId.isEmpty()) {
                        throw new ErrorException("Please add a curriculum item id", VarList.RSP_NO_DATA_FOUND);
                    } else if (description == null || description.isEmpty()) {
                        throw new ErrorException("Please add a description", VarList.RSP_NO_DATA_FOUND);
                    }
                    SectionCurriculumItem sectionCurriculumItem = sectionCurriculumItemRepository.getSectionCurriculumItemById(Integer.parseInt(curriculumItemId));
                    if (sectionCurriculumItem == null) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    }
                    sectionCurriculumItem.setDescription(description);
                    sectionCurriculumItemRepository.save(sectionCurriculumItem);
                    successResponse.setMessage("Description added successfully");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;
                } else {
                    throw new ErrorException("You are not a instructor to do this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse setUnPublishCourse(String code) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {

                    if (code == null || code.isEmpty()) {
                        throw new ErrorException("Please add a course code", VarList.RSP_NO_DATA_FOUND);
                    }
                    Course course = courseRepository.getCourseByCode(code);
                    if (course == null) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    }

                    course.setApprovalType(approvalTypeRepository.getApprovalTypeById(6));
                    courseRepository.save(course);

                    successResponse.setMessage("Course approval type updated successfully");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;


                } else {
                    throw new ErrorException("You are not a instructor to do this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public List<GetCoursesDataResponse> getCoursesUsingLinkName(String linkName) {
        if (linkName == null || linkName.isEmpty()) {
            throw new ErrorException("Please add a link name", VarList.RSP_NO_DATA_FOUND);
        }
        List<Course> courses = courseRepository.getCourseByCourseCategory(courseCategoryRepository.getCourseCategoryByLinkName(linkName));
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
                getCoursesDataResponse.setImg(courseobj.getImg());
                getCoursesDataResponse.setDuration(Double.toString(courseobj.getCourseLength()));
                getCoursesDataResponse.setIsPaid(courseobj.getIsPaid() == 2);
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
                    getCoursesDataResponse.setCurriculum_desc("Curriculum description is not added to the course");


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
    public List<GetCoursesDataResponse> getCoursesUsingSubLinkName(String subLinkName) {
        if (subLinkName == null || subLinkName.isEmpty()) {
            throw new ErrorException("Please add a link name", VarList.RSP_NO_DATA_FOUND);
        }
        List<CourseLandingPage> courseLandingPageList = courseLandingPageRepository.getCourseLandingPageBySubcategory(courseSubCategoryRepository.getCourseSubCategoryBySubLinkName(subLinkName));
        List<CourseLandingPageResponse> courseLandingPageResponses = new ArrayList<>();
        if (courseLandingPageList.size() == 0) {
            throw new ErrorException("There are no courses with that sub link name", VarList.RSP_NO_DATA_FOUND);
        }
        List<GetCoursesDataResponse> responseLists = new ArrayList<>();
        for (CourseLandingPage courseLandingPage : courseLandingPageList) {
            Course course = courseLandingPage.getCourse();

            if (course.getApprovalType().getId() == 5) {

                GetCoursesDataResponse getCoursesDataResponse = new GetCoursesDataResponse();
                getCoursesDataResponse.setCourse_code(course.getCode());
                getCoursesDataResponse.setCreated_date(course.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter));
                getCoursesDataResponse.setId(course.getId());
                getCoursesDataResponse.setImg(course.getImg());
                getCoursesDataResponse.setDuration(Double.toString(course.getCourseLength()));
                getCoursesDataResponse.setIsPaid(course.getIsPaid() == 2);
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
    public List<GetCoursesDataResponse> getCoursesUsingTopicLinkName(String topicLinkName) {
        if (topicLinkName == null || topicLinkName.isEmpty()) {
            throw new ErrorException("Please add topic link name", VarList.RSP_NO_DATA_FOUND);
        }
        List<Topic> topics = topicRepository.getTopicsByLinkName(topicLinkName);
        List<GetCoursesDataResponse> responseLists = new ArrayList<>();
        for (Topic topic : topics) {

            List<CourseLandingPage> courseLandingPageList = courseLandingPageRepository.getCourseLandingPagesByTopic(topic);
            if (courseLandingPageList.size() != 0) {

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
                        getCoursesDataResponse.setCurriculum_desc("Curriculum description is not added to the course");


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
            }
        }

        return responseLists;
    }

    @Override
    public SuccessResponse deleteCurriculumItemFile(DeleteCurriculumItemFileRequest deleteCurriculumItemFileRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {

                    String url = deleteCurriculumItemFileRequest.getUrl();

                    if (url == null || url.isEmpty()) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    }

                    CurriculumItemFile curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileByUrl(url);

                    if (curriculumItemFile == null) {
                        throw new ErrorException("Not found curriculum item file", VarList.RSP_NO_DATA_FOUND);
                    }

                    if (!curriculumItemFile.getSectionCurriculumItem().getCourseSection().getCourse().getInstructorId().getGeneralUserProfile().getUserCode().equals(profile.getUserCode())) {
                        throw new ErrorException("You cannot delete this curriculum item file, it is not yours", VarList.RSP_NO_DATA_FOUND);
                    }
                    Course course = curriculumItemFile.getSectionCurriculumItem().getCourseSection().getCourse();
                    try {
                        if (curriculumItemFile.getCurriculumItemFileTypes().getId() == 3) {
                            double newCourseLength = course.getCourseLength() - curriculumItemFile.getVideoLength();
                            course.setCourseLength(newCourseLength);
                            courseRepository.save(course);
                        }
                        Files.delete(Paths.get(Config.UPLOAD_URL + curriculumItemFile.getUrl()));

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    curriculumItemFileRepository.delete(curriculumItemFile);

                    updateCourseProgress(course);

                    successResponse.setMessage("Syllabus item file deleted successfully");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;
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
    public SuccessResponse addAssignment(AddAssignmentRequest addAssignmentRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    final Integer courseSectionId = addAssignmentRequest.getCourseSectionId();
                    final CurriculumItemType curriculumItemType = curriculumItemTypeRepository.getCurriculumItemTypeById(3);
                    final String assignmentCode = addAssignmentRequest.getAssignmentCode();
                    final String title = addAssignmentRequest.getTitle();
                    final String description = addAssignmentRequest.getDescription();
                    final String duration = addAssignmentRequest.getDuration();
                    final String instruction = addAssignmentRequest.getInstructions();
                    final String assignmentVideoGeneratedName = addAssignmentRequest.getAssignmentVideoGeneratedName();
                    final String assignmentVideoOriginalName = addAssignmentRequest.getAssignmentVideoOriginalName();
                    final String assignmentResourceGeneratedName = addAssignmentRequest.getAssignmentResourceGeneratedName();
                    final String assignmentResourceOriginalName = addAssignmentRequest.getAssignmentResourceOriginalName();
                    final String externalLink = addAssignmentRequest.getExternalLink();
                    final String questions = addAssignmentRequest.getQuestions();
                    final String assignmentQuestionSheetGeneratedName = addAssignmentRequest.getAssignmentQuestionSheetGeneratedName();
                    final String assignmentQuestionSheetOriginalName = addAssignmentRequest.getAssignmentQuestionSheetOriginalName();
                    final String questionLink = addAssignmentRequest.getQuestionLink();
                    final String solution = addAssignmentRequest.getSolution();
                    final String assignmentSolutionVideoGeneratedName = addAssignmentRequest.getAssignmentSolutionVideoGeneratedName();
                    final String assignmentSolutionVideoOriginalName = addAssignmentRequest.getAssignmentSolutionVideoOriginalName();
                    final String assignmentSolutionSheetGeneratedName = addAssignmentRequest.getAssignmentSolutionSheetGeneratedName();
                    final String assignmentSolutionSheetOriginalName = addAssignmentRequest.getAssignmentSolutionSheetOriginalName();
                    final String solutionLink = addAssignmentRequest.getSolutionLink();


                    if (courseSectionId == null || courseSectionId.toString().isEmpty() || title == null || title.isEmpty()
                            || duration == null || duration.toString().isEmpty() || instruction == null || instruction.isEmpty()) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    }

                    CourseSection courseSection = courseSectionRepository.getCourseSectionById(courseSectionId);

                    if (courseSection == null) {
                        throw new ErrorException("Invalid course section id", VarList.RSP_NO_DATA_FOUND);
                    }

                    if (courseSection.getCourse().getInstructorId().getGeneralUserProfile().getUserCode() != profile.getUserCode()) {
                        throw new ErrorException("You cannot access this course because it is not yours", VarList.RSP_NO_DATA_FOUND);
                    }

                    Assignment assignment = null;

                    SectionCurriculumItem sectionCurriculumItem = null;


                    if (assignmentCode != null && !assignmentCode.isEmpty()) {

                        assignment = assignmentRepository.getAssignmentByAssignmentCode(assignmentCode);
                        if (assignment == null) {
                            throw new ErrorException("The assignment for the assignment code was not found", VarList.RSP_NO_DATA_FOUND);
                        }

                        sectionCurriculumItem = assignment.getSectionCurriculumItem();

                    } else {

                        sectionCurriculumItem = sectionCurriculumItemRepository.getSectionCurriculumItemByCourseSectionAndTitleAndCurriculumItemType(courseSection, title, curriculumItemType);

                        if (sectionCurriculumItem == null) {
                            sectionCurriculumItem = new SectionCurriculumItem();
                        }
                    }

                    sectionCurriculumItem.setArticle("N/A");
                    sectionCurriculumItem.setTitle(title);
                    sectionCurriculumItem.setDescription((description == null || description.isEmpty()) ? "" : description);
                    sectionCurriculumItem.setCourseSection(courseSection);
                    sectionCurriculumItem.setCurriculumItemType(curriculumItemType);
                    sectionCurriculumItemRepository.save(sectionCurriculumItem);

                    if (assignment == null) {
                        assignment = assignmentRepository.getAssignmentBySectionCurriculumItem(sectionCurriculumItem);
                        if (assignment == null) {
                            assignment = new Assignment();
                            assignment.setAssignmentCode(UUID.randomUUID().toString());
                        }
                    }

                    assignment.setDuration(duration);
                    assignment.setInstructions(instruction);
                    if (externalLink != null && !externalLink.isEmpty()) {
                        assignment.setExternalLink(externalLink);
                    }
                    if (questions != null && !questions.isEmpty()) {
                        assignment.setQuestions(questions);
                    }
                    if (questionLink != null && !questionLink.isEmpty()) {
                        assignment.setQuestionsExternalLink(questionLink);
                    }
                    if (solution != null && !solution.isEmpty()) {
                        assignment.setSolutions(solution);
                    }
                    if (solutionLink != null && !solutionLink.isEmpty()) {
                        assignment.setSolutionsExternalLink(solutionLink);
                    }
                    assignment.setSectionCurriculumItem(sectionCurriculumItem);
                    assignmentRepository.save(assignment);

                    if (assignmentVideoGeneratedName != null && !assignmentVideoGeneratedName.isEmpty() && assignmentVideoOriginalName != null && !assignmentVideoOriginalName.isEmpty()) {
                        AddCurriculumVideoRequest assignmentVideo = new AddCurriculumVideoRequest();
                        assignmentVideo.setCurriculumItem(sectionCurriculumItem);
                        CurriculumItemFileType curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(5);
                        assignmentVideo.setCurriculumItemFileType(curriculumItemFileType);
                        assignmentVideo.setDownloadableFileGeneratedName(assignmentVideoGeneratedName);
                        assignmentVideo.setDownloadableFileOriginalName(assignmentVideoOriginalName);
                        addCurriculumVideo(assignmentVideo);
                    }

                    if (assignmentResourceGeneratedName != null && !assignmentResourceGeneratedName.isEmpty() && assignmentResourceOriginalName != null && !assignmentResourceGeneratedName.isEmpty()) {
                        AddCurriculumDownloadableFileRequest resourceRequest = new AddCurriculumDownloadableFileRequest();
                        resourceRequest.setCurriculumItem(sectionCurriculumItem);
                        CurriculumItemFileType curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(6);
                        resourceRequest.setCurriculumItemFileType(curriculumItemFileType);
                        resourceRequest.setDownloadableFileGeneratedName(assignmentResourceGeneratedName);
                        resourceRequest.setDownloadableFileOriginalName(assignmentResourceOriginalName);
                        addCurriculumDownloadableFile(resourceRequest);
                    }


                    if (assignmentQuestionSheetGeneratedName != null && !assignmentQuestionSheetGeneratedName.isEmpty() && assignmentQuestionSheetOriginalName != null && !assignmentQuestionSheetOriginalName.isEmpty()) {
                        AddCurriculumDownloadableFileRequest questionSheetRequest = new AddCurriculumDownloadableFileRequest();
                        questionSheetRequest.setCurriculumItem(sectionCurriculumItem);
                        CurriculumItemFileType curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(7);
                        questionSheetRequest.setCurriculumItemFileType(curriculumItemFileType);
                        questionSheetRequest.setDownloadableFileGeneratedName(assignmentQuestionSheetGeneratedName);
                        questionSheetRequest.setDownloadableFileOriginalName(assignmentQuestionSheetOriginalName);
                        addCurriculumDownloadableFile(questionSheetRequest);
                    }

                    if (assignmentSolutionVideoGeneratedName != null && !assignmentSolutionVideoGeneratedName.isEmpty() && assignmentSolutionVideoOriginalName != null && !assignmentSolutionVideoOriginalName.isEmpty()) {
                        AddCurriculumVideoRequest solutionVideoRequest = new AddCurriculumVideoRequest();
                        solutionVideoRequest.setCurriculumItem(sectionCurriculumItem);
                        CurriculumItemFileType curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(8);
                        solutionVideoRequest.setCurriculumItemFileType(curriculumItemFileType);
                        solutionVideoRequest.setDownloadableFileGeneratedName(assignmentSolutionVideoGeneratedName);
                        solutionVideoRequest.setDownloadableFileOriginalName(assignmentSolutionVideoOriginalName);
                        addCurriculumVideo(solutionVideoRequest);
                    }
                    if (assignmentSolutionSheetGeneratedName != null && !assignmentSolutionSheetGeneratedName.isEmpty() && assignmentSolutionSheetOriginalName != null && !assignmentSolutionSheetOriginalName.isEmpty()) {
                        AddCurriculumDownloadableFileRequest solutionSheetRequest = new AddCurriculumDownloadableFileRequest();
                        solutionSheetRequest.setCurriculumItem(sectionCurriculumItem);
                        CurriculumItemFileType curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(9);
                        solutionSheetRequest.setCurriculumItemFileType(curriculumItemFileType);
                        solutionSheetRequest.setDownloadableFileGeneratedName(assignmentSolutionSheetGeneratedName);
                        solutionSheetRequest.setDownloadableFileOriginalName(assignmentSolutionSheetOriginalName);
                        addCurriculumDownloadableFile(solutionSheetRequest);
                    }

                    updateCourseProgress(courseSection.getCourse());
                    successResponse.setMessage("Assignment added successfully");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;

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
    public SuccessResponse addCurriculumVideo(AddCurriculumVideoRequest addCurriculumVideoRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    final SectionCurriculumItem sectionCurriculumItem = addCurriculumVideoRequest.getCurriculumItem();
                    final CurriculumItemFileType curriculumItemFileType = addCurriculumVideoRequest.getCurriculumItemFileType();
                    final String assignmentVideoGeneratedName = addCurriculumVideoRequest.getDownloadableFileGeneratedName();
                    final String assignmentVideoOriginalName = addCurriculumVideoRequest.getDownloadableFileOriginalName();

                    if (sectionCurriculumItem == null || curriculumItemFileType == null) {
                        throw new ErrorException("Curriculum item file type or section curriculum item not available", VarList.RSP_NO_DATA_FOUND);
                    }

                    CurriculumItemFile curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(sectionCurriculumItem, curriculumItemFileType);
                    try {
                        String path = resolveFilePath(curriculumItemFileType.getId());
                        if (curriculumItemFile != null) {
                            Files.delete(Paths.get(Config.UPLOAD_URL + curriculumItemFile.getUrl()));
                        } else {
                            curriculumItemFile = new CurriculumItemFile();
                            curriculumItemFile.setCurriculumItemFileTypes(curriculumItemFileType);
                            curriculumItemFile.setSectionCurriculumItem(sectionCurriculumItem);
                        }
                        curriculumItemFile.setUrl(path + assignmentVideoGeneratedName);
                        curriculumItemFile.setTitle(assignmentVideoOriginalName);
                        curriculumItemFileRepository.save(curriculumItemFile);
                        successResponse.setMessage("Video added successfully");
                        successResponse.setVariable(VarList.RSP_SUCCESS);
                        return successResponse;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    throw new ErrorException("You are not a instructor to do this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse addCurriculumDownloadableFile(AddCurriculumDownloadableFileRequest addCurriculumDownloadableFileRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    final SectionCurriculumItem sectionCurriculumItem = addCurriculumDownloadableFileRequest.getCurriculumItem();
                    final CurriculumItemFileType curriculumItemFileType = addCurriculumDownloadableFileRequest.getCurriculumItemFileType();
                    final String downloadableFileGeneratedName = addCurriculumDownloadableFileRequest.getDownloadableFileGeneratedName();
                    final String downloadableFileOriginalName = addCurriculumDownloadableFileRequest.getDownloadableFileOriginalName();
                    if (sectionCurriculumItem == null || curriculumItemFileType == null) {
                        throw new ErrorException("Curriculum item file type or section curriculum item not available", VarList.RSP_NO_DATA_FOUND);
                    } else if (downloadableFileGeneratedName == null || downloadableFileGeneratedName.isEmpty() || downloadableFileOriginalName == null || downloadableFileOriginalName.isEmpty()) {
                        throw new ErrorException("Please add a downloadable file", VarList.RSP_NO_DATA_FOUND);
                    }

                    String path = resolveFilePath(curriculumItemFileType.getId());

                    CurriculumItemFile curriculumItemFile = new CurriculumItemFile();
                    curriculumItemFile.setCurriculumItemFileTypes(curriculumItemFileType);
                    curriculumItemFile.setSectionCurriculumItem(sectionCurriculumItem);

                    curriculumItemFile.setUrl(path + downloadableFileGeneratedName);
                    curriculumItemFile.setTitle(downloadableFileOriginalName);
                    curriculumItemFileRepository.save(curriculumItemFile);

                    successResponse.setMessage("Downloadable file added successfully");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;

                } else {
                    throw new ErrorException("You are not a instructor to do this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    private String resolveFilePath(int fileTypeId) {
        Map<Integer, String> fileTypePathMap = new HashMap<>();

        fileTypePathMap.put(1, Config.DOWNLOADABLE_FILE_UPLOAD_URL);
        fileTypePathMap.put(2, Config.SOURCE_CODE_UPLOAD_URL);
        fileTypePathMap.put(5, Config.ASSIGNMENT_VIDEO_UPLOAD_URL);
        fileTypePathMap.put(6, Config.ASSIGNMENT_RESOURCES_DOCUMENTS_UPLOAD_URL);
        fileTypePathMap.put(7, Config.ASSIGNMENTS_QUESTION_SHEET_DOCUMENTS_UPLOAD_URL);
        fileTypePathMap.put(8, Config.ASSIGNMENT_SOLUTION_VIDEO_UPLOAD_URL);
        fileTypePathMap.put(9, Config.ASSIGNMENTS_SOLUTION_SHEET_DOCUMENTS_UPLOAD_URL);
        fileTypePathMap.put(10, Config.CODING_EXERCISE_CODING_VIDEO_UPLOAD_URL);
        fileTypePathMap.put(11, Config.CODING_EXERCISE_CODING_RESOURCES_DOCUMENTS_UPLOAD_URL);
        fileTypePathMap.put(12, Config.CODING_EXERCISE_SHEET_DOCUMENTS_UPLOAD_URL);
        fileTypePathMap.put(13, Config.CODING_EXERCISE_VIDEO_UPLOAD_URL);
        fileTypePathMap.put(14, Config.CODING_EXERCISE_SOLUTION_VIDEO_UPLOAD_URL);
        fileTypePathMap.put(15, Config.CODING_EXERCISE_CODING_SOLUTION_SHEET_DOCUMENTS_UPLOAD_URL);
        fileTypePathMap.put(16, Config.PRACTICE_TEST_QUESTION_SHEET_DOCUMENTS_UPLOAD_URL);
        fileTypePathMap.put(17, Config.PRACTICE_TEST_SOLUTION_SHEET_DOCUMENTS_UPLOAD_URL);

        return fileTypePathMap.get(fileTypeId);
    }


    @Override
    public SuccessResponse addCodingExercise(AddCodingExerciseRequest addCodingExerciseRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    final Integer courseSectionId = addCodingExerciseRequest.getCourseSectionId();
                    final CurriculumItemType curriculumItemType = curriculumItemTypeRepository.getCurriculumItemTypeById(4);
                    final String codingExerciseCode = addCodingExerciseRequest.getCodingExerciseCode();
                    final String title = addCodingExerciseRequest.getTitle();
                    final String description = addCodingExerciseRequest.getDescription();
                    final String instruction = addCodingExerciseRequest.getInstructions();
                    final String videoGeneratedName = addCodingExerciseRequest.getVideoGeneratedName();
                    final String videoOriginalName = addCodingExerciseRequest.getVideoOriginalName();
                    final String codingExerciseResourceGeneratedName = addCodingExerciseRequest.getCodingExerciseResourceGeneratedName();
                    final String codingExerciseResourceOriginalName = addCodingExerciseRequest.getCodingExerciseResourceOriginalName();
                    final String externalLink = addCodingExerciseRequest.getExternalLink();
                    final String codingVideoGeneratedName = addCodingExerciseRequest.getCodingVideoGeneratedName();
                    final String codingVideoOriginalName = addCodingExerciseRequest.getCodingVideoOriginalName();
                    final String codingFilesGeneratedName = addCodingExerciseRequest.getCodingFilesGeneratedName();
                    final String codingFilesOriginalName = addCodingExerciseRequest.getCodingFilesOriginalName();
                    final String codingLink = addCodingExerciseRequest.getCodingLink();
                    final String solutionVideoGeneratedName = addCodingExerciseRequest.getSolutionVideoGeneratedName();
                    final String solutionVideoOriginalName = addCodingExerciseRequest.getSolutionVideoOriginalName();
                    final String solutionSheetGeneratedName = addCodingExerciseRequest.getSolutionSheetGeneratedName();
                    final String solutionSheetOriginalName = addCodingExerciseRequest.getSolutionSheetOriginalName();
                    final String solutionLink = addCodingExerciseRequest.getSolutionLink();

                    if (courseSectionId == null || courseSectionId.toString().isEmpty() || title == null || title.isEmpty() || description == null || description.isEmpty()
                            || instruction == null || instruction.isEmpty()) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    }

                    CourseSection courseSection = courseSectionRepository.getCourseSectionById(courseSectionId);

                    if (courseSection == null) {
                        throw new ErrorException("Invalid course section id", VarList.RSP_NO_DATA_FOUND);
                    }

                    if (courseSection.getCourse().getInstructorId().getGeneralUserProfile().getUserCode() != profile.getUserCode()) {
                        throw new ErrorException("You cannot access this course because it is not yours", VarList.RSP_NO_DATA_FOUND);
                    }

                    CodingExercise codingExercise = null;

                    SectionCurriculumItem sectionCurriculumItem = null;

                    if (codingExerciseCode != null && !codingExerciseCode.isEmpty()) {
                        codingExercise = codingExerciseRepository.getCodingExerciseByCodingExerciseCode(codingExerciseCode);
                        if (codingExercise == null) {
                            throw new ErrorException("The coding exercise  was not found", VarList.RSP_NO_DATA_FOUND);
                        }
                        sectionCurriculumItem = codingExercise.getSectionCurriculumItem();
                    } else {

                        sectionCurriculumItem = sectionCurriculumItemRepository.getSectionCurriculumItemByCourseSectionAndTitleAndCurriculumItemType(courseSection, title, curriculumItemType);

                        if (sectionCurriculumItem == null) {
                            sectionCurriculumItem = new SectionCurriculumItem();
                        }
                    }
                    sectionCurriculumItem.setArticle("N/A");
                    sectionCurriculumItem.setTitle(title);
                    sectionCurriculumItem.setDescription(description);
                    sectionCurriculumItem.setCourseSection(courseSection);
                    sectionCurriculumItem.setCurriculumItemType(curriculumItemType);
                    sectionCurriculumItem.setIsDelete((byte) 0);
                    sectionCurriculumItemRepository.save(sectionCurriculumItem);

                    if (codingExercise == null) {
                        codingExercise = codingExerciseRepository.getCodingExerciseBySectionCurriculumItem(sectionCurriculumItem);
                        if (codingExercise == null) {
                            codingExercise = new CodingExercise();
                            codingExercise.setCodingExerciseCode(UUID.randomUUID().toString());
                        }
                    }


                    codingExercise.setInstructions(instruction);
                    if (externalLink != null && !externalLink.isEmpty()) {
                        codingExercise.setExternalLink(externalLink);
                    }
                    if (codingLink != null && !codingLink.isEmpty()) {
                        codingExercise.setCodingLink(codingLink);
                    }
                    if (solutionLink != null && !solutionLink.isEmpty()) {
                        codingExercise.setSolutionLink(solutionLink);
                    }
                    codingExercise.setSectionCurriculumItem(sectionCurriculumItem);
                    codingExerciseRepository.save(codingExercise);

                    if (videoGeneratedName != null && !videoGeneratedName.isEmpty() && videoOriginalName != null && !videoOriginalName.isEmpty()) {
                        AddCurriculumVideoRequest codingVideoRequest = new AddCurriculumVideoRequest();
                        codingVideoRequest.setCurriculumItem(sectionCurriculumItem);
                        CurriculumItemFileType curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(10);
                        codingVideoRequest.setCurriculumItemFileType(curriculumItemFileType);
                        codingVideoRequest.setDownloadableFileGeneratedName(videoGeneratedName);
                        codingVideoRequest.setDownloadableFileOriginalName(videoOriginalName);
                        addCurriculumVideo(codingVideoRequest);
                    }
                    if (codingExerciseResourceGeneratedName != null && !codingExerciseResourceGeneratedName.isEmpty() && codingExerciseResourceOriginalName != null && !codingExerciseResourceOriginalName.isEmpty()) {
                        AddCurriculumDownloadableFileRequest resourceRequest = new AddCurriculumDownloadableFileRequest();
                        resourceRequest.setCurriculumItem(sectionCurriculumItem);
                        CurriculumItemFileType curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(11);
                        resourceRequest.setCurriculumItemFileType(curriculumItemFileType);
                        resourceRequest.setDownloadableFileGeneratedName(codingExerciseResourceGeneratedName);
                        resourceRequest.setDownloadableFileOriginalName(codingExerciseResourceOriginalName);
                        addCurriculumDownloadableFile(resourceRequest);
                    }

                    if (codingFilesGeneratedName != null && !codingFilesGeneratedName.isEmpty() && codingFilesOriginalName != null && !codingFilesOriginalName.isEmpty()) {
                        AddCurriculumDownloadableFileRequest codingFilesRequest = new AddCurriculumDownloadableFileRequest();
                        codingFilesRequest.setCurriculumItem(sectionCurriculumItem);
                        CurriculumItemFileType curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(12);
                        codingFilesRequest.setCurriculumItemFileType(curriculumItemFileType);
                        codingFilesRequest.setDownloadableFileGeneratedName(codingFilesGeneratedName);
                        codingFilesRequest.setDownloadableFileOriginalName(codingFilesOriginalName);
                        addCurriculumDownloadableFile(codingFilesRequest);
                    }
                    if (codingVideoGeneratedName != null && !codingVideoGeneratedName.isEmpty() && codingVideoOriginalName != null && !codingVideoOriginalName.isEmpty()) {
                        AddCurriculumVideoRequest codingVideoRequest = new AddCurriculumVideoRequest();
                        codingVideoRequest.setCurriculumItem(sectionCurriculumItem);
                        CurriculumItemFileType curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(13);
                        codingVideoRequest.setCurriculumItemFileType(curriculumItemFileType);
                        codingVideoRequest.setDownloadableFileGeneratedName(codingVideoGeneratedName);
                        codingVideoRequest.setDownloadableFileOriginalName(codingVideoOriginalName);
                        addCurriculumVideo(codingVideoRequest);
                    }

                    if (solutionVideoGeneratedName != null && !solutionVideoGeneratedName.isEmpty() && solutionVideoOriginalName != null && !solutionVideoOriginalName.isEmpty()) {
                        AddCurriculumVideoRequest solutionVideoRequest = new AddCurriculumVideoRequest();
                        solutionVideoRequest.setCurriculumItem(sectionCurriculumItem);
                        CurriculumItemFileType curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(14);
                        solutionVideoRequest.setCurriculumItemFileType(curriculumItemFileType);
                        solutionVideoRequest.setDownloadableFileGeneratedName(solutionVideoGeneratedName);
                        solutionVideoRequest.setDownloadableFileOriginalName(solutionVideoOriginalName);
                        addCurriculumVideo(solutionVideoRequest);
                    }
                    if (solutionSheetGeneratedName != null && !solutionSheetGeneratedName.isEmpty() && solutionSheetOriginalName != null && !solutionSheetOriginalName.isEmpty()) {
                        AddCurriculumDownloadableFileRequest solutionSheetRequest = new AddCurriculumDownloadableFileRequest();
                        solutionSheetRequest.setCurriculumItem(sectionCurriculumItem);
                        CurriculumItemFileType curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(15);
                        solutionSheetRequest.setCurriculumItemFileType(curriculumItemFileType);
                        solutionSheetRequest.setDownloadableFileGeneratedName(solutionSheetGeneratedName);
                        solutionSheetRequest.setDownloadableFileOriginalName(solutionSheetOriginalName);
                        addCurriculumDownloadableFile(solutionSheetRequest);
                    }
                    updateCourseProgress(courseSection.getCourse());
                    successResponse.setMessage("Coding exercise added successfully");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;

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
    public SuccessResponse addPracticeTest(AddPracticeTestRequest addPracticeTestRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    final Integer courseSectionId = addPracticeTestRequest.getCourseSectionId();
                    final CurriculumItemType curriculumItemType = curriculumItemTypeRepository.getCurriculumItemTypeById(5);
                    final String practiceTestCode = addPracticeTestRequest.getPracticeTestCode();
                    final String title = addPracticeTestRequest.getTitle();
                    final String description = addPracticeTestRequest.getDescription();
                    final String duration = addPracticeTestRequest.getDuration();
                    final Double minimumPassMark = addPracticeTestRequest.getMinimumPassMark();
                    final String instruction = addPracticeTestRequest.getInstructions();
                    final String externalLink = addPracticeTestRequest.getExternalLink();

                    final String generatedQuestionSheetName = addPracticeTestRequest.getGeneratedQuestionSheetName();
                    final String originalQuestionSheetName = addPracticeTestRequest.getOriginalQuestionSheetName();
                    final String generatedSolutionSheetName = addPracticeTestRequest.getGeneratedSolutionSheetName();
                    final String originalSolutionSheetName = addPracticeTestRequest.getOriginalSolutionSheetName();

                    final String questionLink = addPracticeTestRequest.getQuestionLink();
                    final String solutionLink = addPracticeTestRequest.getSolutionLink();

                    if (courseSectionId == null || courseSectionId.toString().isEmpty() || title == null || title.isEmpty() || description == null || description.isEmpty()
                            || duration == null || duration.toString().isEmpty() || minimumPassMark == null || minimumPassMark.toString().isEmpty() || instruction == null || instruction.isEmpty()) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    }

                    CourseSection courseSection = courseSectionRepository.getCourseSectionById(courseSectionId);

                    if (courseSection == null) {
                        throw new ErrorException("Invalid course section id", VarList.RSP_NO_DATA_FOUND);
                    }

                    if (courseSection.getCourse().getInstructorId().getGeneralUserProfile().getUserCode() != profile.getUserCode()) {
                        throw new ErrorException("You cannot access this course because it is not yours", VarList.RSP_NO_DATA_FOUND);
                    }

                    PracticeTest practiceTest = null;

                    SectionCurriculumItem sectionCurriculumItem = null;

                    if (practiceTestCode != null && !practiceTestCode.isEmpty()) {
                        practiceTest = practiceTestRepository.getPracticeTestByPracticeTestCode(practiceTestCode);
                        if (practiceTest == null) {
                            throw new ErrorException("The assignment for the assignment code was not found", VarList.RSP_NO_DATA_FOUND);
                        }
                        sectionCurriculumItem = practiceTest.getSectionCurriculumItem();
                    } else {

                        sectionCurriculumItem = sectionCurriculumItemRepository.getSectionCurriculumItemByCourseSectionAndTitleAndCurriculumItemType(courseSection, title, curriculumItemType);

                        if (sectionCurriculumItem == null) {
                            sectionCurriculumItem = new SectionCurriculumItem();
                        }
                    }
                    sectionCurriculumItem.setArticle("N/A");
//                    sectionCurriculumItem.setIsDelete((byte) 0);
                    sectionCurriculumItem.setTitle(title);
                    sectionCurriculumItem.setDescription(description);
                    sectionCurriculumItem.setCourseSection(courseSection);
                    sectionCurriculumItem.setCurriculumItemType(curriculumItemType);
                    sectionCurriculumItemRepository.save(sectionCurriculumItem);

                    if (practiceTest == null) {
                        practiceTest = practiceTestRepository.getPracticeTestBySectionCurriculumItem(sectionCurriculumItem);
                        if (practiceTest == null) {
                            practiceTest = new PracticeTest();
                            practiceTest.setPracticeTestCode(UUID.randomUUID().toString());
                        }
                    }

                    practiceTest.setDuration(duration);
                    practiceTest.setMinimumPassMark(minimumPassMark);
                    practiceTest.setInstructions(instruction);
                    if (externalLink != null && !externalLink.isEmpty()) {
                        practiceTest.setExternalLink(externalLink);
                    }
                    if (questionLink != null && !questionLink.isEmpty()) {
                        practiceTest.setQuestionLink(questionLink);
                    }
                    if (solutionLink != null && !solutionLink.isEmpty()) {
                        practiceTest.setSolutionLink(solutionLink);
                    }
                    practiceTest.setSectionCurriculumItem(sectionCurriculumItem);
                    practiceTestRepository.save(practiceTest);

                    if (generatedQuestionSheetName != null && !generatedQuestionSheetName.isEmpty() && originalQuestionSheetName != null && !originalQuestionSheetName.isEmpty()) {
                        AddCurriculumDownloadableFileRequest questionSheetRequest = new AddCurriculumDownloadableFileRequest();
                        questionSheetRequest.setCurriculumItem(sectionCurriculumItem);
                        CurriculumItemFileType curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(16);
                        questionSheetRequest.setCurriculumItemFileType(curriculumItemFileType);
                        questionSheetRequest.setDownloadableFileGeneratedName(generatedQuestionSheetName);
                        questionSheetRequest.setDownloadableFileOriginalName(originalQuestionSheetName);
                        addCurriculumDownloadableFile(questionSheetRequest);
                    }
                    if (generatedSolutionSheetName != null && !generatedSolutionSheetName.isEmpty() && originalSolutionSheetName != null && !originalSolutionSheetName.isEmpty()) {
                        AddCurriculumDownloadableFileRequest solutionSheetRequest = new AddCurriculumDownloadableFileRequest();
                        solutionSheetRequest.setCurriculumItem(sectionCurriculumItem);
                        CurriculumItemFileType curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(17);
                        solutionSheetRequest.setCurriculumItemFileType(curriculumItemFileType);
                        solutionSheetRequest.setDownloadableFileGeneratedName(generatedSolutionSheetName);
                        solutionSheetRequest.setDownloadableFileOriginalName(originalSolutionSheetName);
                        addCurriculumDownloadableFile(solutionSheetRequest);
                    }
                    updateCourseProgress(courseSection.getCourse());
                    successResponse.setMessage("Practice test added successfully");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;

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
    public SuccessResponse deleteAssignment(String assignmentCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    Assignment assignment = assignmentRepository.getAssignmentByAssignmentCode(assignmentCode);
                    if (assignment == null) {
                        throw new ErrorException("Invalid assignment code", VarList.RSP_NO_DATA_FOUND);
                    }
                    SectionCurriculumItem sectionCurriculumItem = assignment.getSectionCurriculumItem();
                    if (sectionCurriculumItem.getCourseSection().getCourse().getInstructorId().getGeneralUserProfile().getUserCode() != profile.getUserCode()) {
                        throw new ErrorException("You cannot access this course because it is not yours", VarList.RSP_NO_DATA_FOUND);
                    }
                    List<CurriculumItemFile> curriculumItemFiles = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItem(sectionCurriculumItem);
                    for (CurriculumItemFile curriculumItemFile : curriculumItemFiles) {
                        curriculumItemFile.setIsDelete((byte) 1);
                        curriculumItemFileRepository.save(curriculumItemFile);
                    }
                    assignment.setIsDelete((byte) 1);
                    assignmentRepository.save(assignment);
                    sectionCurriculumItem.setIsDelete((byte) 1);
                    sectionCurriculumItemRepository.save(sectionCurriculumItem);

                    updateCourseProgress(sectionCurriculumItem.getCourseSection().getCourse());
                    successResponse.setMessage("The assignment has been successfully deleted");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;

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
    public SuccessResponse deletePracticeTest(String practiceTestCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    PracticeTest practiceTest = practiceTestRepository.getPracticeTestByPracticeTestCode(practiceTestCode);
                    if (practiceTest == null) {
                        throw new ErrorException("invalid practice test code", VarList.RSP_NO_DATA_FOUND);
                    }
                    SectionCurriculumItem sectionCurriculumItem = practiceTest.getSectionCurriculumItem();
                    if (sectionCurriculumItem.getCourseSection().getCourse().getInstructorId().getGeneralUserProfile().getUserCode() != profile.getUserCode()) {
                        throw new ErrorException("You cannot access this course because it is not yours", VarList.RSP_NO_DATA_FOUND);
                    }
                    List<CurriculumItemFile> curriculumItemFiles = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItem(sectionCurriculumItem);
                    for (CurriculumItemFile curriculumItemFile : curriculumItemFiles) {
                        curriculumItemFile.setIsDelete((byte) 1);
                        curriculumItemFileRepository.save(curriculumItemFile);
                    }
                    practiceTest.setIsDelete((byte) 1);
                    practiceTestRepository.save(practiceTest);
                    sectionCurriculumItem.setIsDelete((byte) 1);
                    sectionCurriculumItemRepository.save(sectionCurriculumItem);

                    updateCourseProgress(sectionCurriculumItem.getCourseSection().getCourse());
                    successResponse.setMessage("The practice test has been successfully deleted");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;

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
    public SuccessResponse deleteCodingExercise(String codingExerciseCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    CodingExercise codingExercise = codingExerciseRepository.getCodingExerciseByCodingExerciseCode(codingExerciseCode);
                    if (codingExercise == null) {
                        throw new ErrorException("Invalid coding exercise code", VarList.RSP_NO_DATA_FOUND);
                    }
                    SectionCurriculumItem sectionCurriculumItem = codingExercise.getSectionCurriculumItem();
                    if (sectionCurriculumItem.getCourseSection().getCourse().getInstructorId().getGeneralUserProfile().getUserCode() != profile.getUserCode()) {
                        throw new ErrorException("You cannot access this course because it is not yours", VarList.RSP_NO_DATA_FOUND);
                    }
                    List<CurriculumItemFile> curriculumItemFiles = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItem(sectionCurriculumItem);
                    for (CurriculumItemFile curriculumItemFile : curriculumItemFiles) {
                        curriculumItemFile.setIsDelete((byte) 1);
                        curriculumItemFileRepository.save(curriculumItemFile);
                    }
                    codingExercise.setIsDelete((byte) 1);
                    codingExerciseRepository.save(codingExercise);
                    sectionCurriculumItem.setIsDelete((byte) 1);
                    sectionCurriculumItemRepository.save(sectionCurriculumItem);

                    updateCourseProgress(sectionCurriculumItem.getCourseSection().getCourse());
                    successResponse.setMessage("Coding exercise has been successfully deleted");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;

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
    public SuccessResponse deletequiz(Integer curriculumItemId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    SectionCurriculumItem sectionCurriculumItem = sectionCurriculumItemRepository.getSectionCurriculumItemById(curriculumItemId);
                    if (sectionCurriculumItem == null) {
                        throw new ErrorException("Invalid curriculum id", VarList.RSP_NO_DATA_FOUND);
                    }
                    if (sectionCurriculumItem.getCourseSection().getCourse().getInstructorId().getGeneralUserProfile().getUserCode() != profile.getUserCode()) {
                        throw new ErrorException("You cannot access this course because it is not yours", VarList.RSP_NO_DATA_FOUND);
                    }
                    List<ReadCurriculumItem> readCurriculumItems = readCurriculumItemRepository.getReadCurriculumItemBySectionCurriculumItem(sectionCurriculumItem);
                    if (readCurriculumItems.size() > 0)
                        readCurriculumItemRepository.deleteAll(readCurriculumItems);
                    List<CurriculumItemFile> curriculumItemFiles = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItem(sectionCurriculumItem);
                    for (CurriculumItemFile curriculumItemFile : curriculumItemFiles) {
                        curriculumItemFileRepository.delete(curriculumItemFile);
                    }

                    List<Quiz> quizs = quizRepository.getQuizBySectionCurriculumItem(sectionCurriculumItem);


                    for (Quiz quiz : quizs) {
                        List<Answer> answerList = answerRepository.getAnswerByQuiz(quiz);
                        answerRepository.deleteAll(answerList);
                    }

                    if (quizs.size() > 0)
                        quizRepository.deleteAll(quizs);

                    sectionCurriculumItemRepository.delete(sectionCurriculumItem);

                    updateCourseProgress(sectionCurriculumItem.getCourseSection().getCourse());
                    successResponse.setMessage("The quiz has been successfully deleted");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;

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
    public SuccessResponse deletelecture(Integer curriculumItemId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {

                    SectionCurriculumItem sectionCurriculumItem = sectionCurriculumItemRepository.getSectionCurriculumItemById(curriculumItemId);

                    if (sectionCurriculumItem == null) {
                        throw new ErrorException("Invalid curriculum item id", VarList.RSP_NO_DATA_FOUND);
                    }

                    Course course = sectionCurriculumItem.getCourseSection().getCourse();

                    if (!course.getInstructorId().getGeneralUserProfile().equals(profile)) {
                        throw new ErrorException("You cannot access this course because it is not yours", VarList.RSP_NO_DATA_FOUND);
                    }
                    List<CurriculumItemFile> curriculumItemFiles = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItem(sectionCurriculumItem);
                    double updateCourseLength = course.getCourseLength();
                    for (CurriculumItemFile curriculumItemFile : curriculumItemFiles) {
                        curriculumItemFile.setIsDelete((byte) 1);
                        curriculumItemFileRepository.save(curriculumItemFile);
                        if (curriculumItemFile.getCurriculumItemFileTypes().getId() == 3) {
                            updateCourseLength = updateCourseLength - curriculumItemFile.getVideoLength();
                        }
                    }
                    course.setCourseLength(updateCourseLength);
                    courseRepository.save(course);

                    sectionCurriculumItem.setIsDelete((byte) 1);
                    sectionCurriculumItemRepository.save(sectionCurriculumItem);
                    updateCourseProgress(sectionCurriculumItem.getCourseSection().getCourse());
                    successResponse.setMessage("The lecture has been successfully deleted");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;

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
    public SuccessResponse deletecourseSection(Integer courseSectionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {

                    CourseSection courseSection = courseSectionRepository.getCourseSectionById(courseSectionId);

                    if (courseSection == null) {
                        throw new ErrorException("Invalid course section id", VarList.RSP_NO_DATA_FOUND);
                    }

                    if (courseSection.getCourse().getInstructorId().getGeneralUserProfile().getUserCode() != profile.getUserCode()) {
                        throw new ErrorException("You cannot access this course because it is not yours", VarList.RSP_NO_DATA_FOUND);
                    }
                    List<SectionCurriculumItem> sectionCurriculumItems = sectionCurriculumItemRepository.getSectionCurriculumItemsByCourseSection(courseSection);
                    Course course = courseSection.getCourse();
                    double updateCourseLength = course.getCourseLength();
                    for (SectionCurriculumItem sectionCurriculumItem : sectionCurriculumItems) {

                        List<PreviousView> previousViews = previousViewRepository.getPreviousViewBySectionCurriculumItem(sectionCurriculumItem);

                        if (!previousViews.isEmpty()) {
                            previousViewRepository.deleteAll(previousViews);
                        }

                        List<CurriculumItemFile> curriculumItemFiles = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItem(sectionCurriculumItem);
                        for (CurriculumItemFile curriculumItemFile : curriculumItemFiles) {
                            if (curriculumItemFile.getCurriculumItemFileTypes().getId() == 3) {
                                updateCourseLength = updateCourseLength - curriculumItemFile.getVideoLength();
                            }
                            curriculumItemFileRepository.delete(curriculumItemFile);
                        }
                        Assignment assignment = assignmentRepository.getAssignmentBySectionCurriculumItem(sectionCurriculumItem);
                        if (assignment != null) {
                            assignmentRepository.delete(assignment);
                        }
                        PracticeTest practiceTest = practiceTestRepository.getPracticeTestBySectionCurriculumItem(sectionCurriculumItem);
                        if (practiceTest != null) {
                            practiceTestRepository.delete(practiceTest);
                        }
                        CodingExercise codingExercise = codingExerciseRepository.getCodingExerciseBySectionCurriculumItem(sectionCurriculumItem);
                        if (codingExercise != null) {
                            codingExerciseRepository.delete(codingExercise);
                        }
                        List<Quiz> quizzes = quizRepository.getQuizBySectionCurriculumItem(sectionCurriculumItem);
                        for (Quiz quiz : quizzes) {
                            List<Answer> answers = answerRepository.getAnswerByQuiz(quiz);
                            if (answers != null && !answers.isEmpty()) {
                                answerRepository.deleteAll(answers);
                            }
                            quizRepository.delete(quiz);
                        }
                        List<ReadCurriculumItem> readCurriculumItems = readCurriculumItemRepository.getReadCurriculumItemBySectionCurriculumItem(sectionCurriculumItem);
                        if (readCurriculumItems != null && !readCurriculumItems.isEmpty()) {
                            readCurriculumItemRepository.deleteAll(readCurriculumItems);
                        }
                        sectionCurriculumItemRepository.delete(sectionCurriculumItem);
                    }
                    course.setCourseLength(updateCourseLength);
                    courseRepository.save(course);
                    courseSectionRepository.delete(courseSection);
                    updateCourseProgress(courseSection.getCourse());
                    successResponse.setMessage("The course section has been successfully deleted");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;

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

    private void updateCourseProgress(Course course) {
        List<OrderHasCourse> orderHasCourses = orderHasCourseRepository.getOrderHasCoursesByCourse(course);
        for (OrderHasCourse orderHasCourse : orderHasCourses) {
            if (orderHasCourse.getProgress() != 0) {
                List<ReadCurriculumItem> readCurriculumItems = readCurriculumItemRepository.getReadCurriculumItemByOrderHasCourse(orderHasCourse);
                List<CourseSection> courseSections = courseSectionRepository.getCourseSectionByCourse(course);
                int allSectionCurriculumItemsCount = 0;

                // Initialize completed sections once outside the loop
                StringBuilder newCompletedSections = new StringBuilder("");

                for (CourseSection courseSection : courseSections) {
                    boolean isComplete = true;
                    List<SectionCurriculumItem> sectionCurriculumItems = sectionCurriculumItemRepository.getSectionCurriculumItemByCourseSection(courseSection);
                    allSectionCurriculumItemsCount += sectionCurriculumItems.size();

                    for (SectionCurriculumItem sectionCurriculumItem : sectionCurriculumItems) {
                        ReadCurriculumItem readCurriculumItem = readCurriculumItemRepository.getReadCurriculumItemByOrderHasCourseAndSectionCurriculumItem(orderHasCourse, sectionCurriculumItem);
                        if (readCurriculumItem == null) {
                            isComplete = false;
                            break; // No need to check further if one item is not complete
                        }
                    }

                    if (isComplete) {
                        if (!newCompletedSections.toString().contains(String.valueOf(courseSection.getId()))) {
                            if (newCompletedSections.length() > 0) {
                                newCompletedSections.append(", ");
                            }
                            newCompletedSections.append(courseSection.getId());
                        }
                    }
                }

                orderHasCourse.setCompletedSections(newCompletedSections.toString());

                // Calculate progress as a percentage
                double progress = ((double) readCurriculumItems.size() / allSectionCurriculumItemsCount) * 100;

                // Debugging outputs (remove these if not needed)
                System.out.println("Test/////readCurriculumItems Count: " + readCurriculumItems.size());
                System.out.println("Test///////allSectionCurriculumItemsCount: " + allSectionCurriculumItemsCount);
                System.out.println("PROGRESS//// " + progress);

                orderHasCourse.setProgress(progress);
                orderHasCourseRepository.save(orderHasCourse);
            }
        }
    }


    @Override
    public SuccessResponse deleteIntendedLearners(DeleteIntendedLearnersRequest deleteIntendedLearnersRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {

                    final String courseCode = deleteIntendedLearnersRequest.getCourseCode();
                    final String intentedLearner = deleteIntendedLearnersRequest.getIntendedLearner();
                    final Integer intendedLearnerTypeId = deleteIntendedLearnersRequest.getIntendedLearnerTypeId();

                    if (courseCode == null || courseCode.isEmpty() || intentedLearner == null || intentedLearner.isEmpty() || intendedLearnerTypeId == null || intendedLearnerTypeId.toString().isEmpty()) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    }
                    Course course = courseRepository.getCourseByCode(courseCode);
                    if (course == null) {
                        throw new ErrorException("Invalid courseCode", VarList.RSP_NO_DATA_FOUND);
                    }

                    IntendedLearnerType intendedLearnerType = intendedLearnerTypeRepository.findById((int) intendedLearnerTypeId);
                    if (intendedLearnerType == null) {
                        throw new ErrorException("Invalid target audience type id", VarList.RSP_NO_DATA_FOUND);
                    }

                    if (course.getInstructorId().getGeneralUserProfile().getUserCode() != profile.getUserCode()) {
                        throw new ErrorException("You cannot access this course because it is not yours", VarList.RSP_NO_DATA_FOUND);
                    }

                    List<CourseIntentedLearner> courseIntentedLearners = courseIntentedLearnerRepository.getCourseIntentedLearnerByCourseAndNameAndIntendedLearnerType(course, intentedLearner, intendedLearnerType);
                    if (courseIntentedLearners == null || courseIntentedLearners.isEmpty()) {
                        throw new ErrorException("Invalid target audience", VarList.RSP_NO_DATA_FOUND);
                    }

                    courseIntentedLearnerRepository.delete(courseIntentedLearners.get(courseIntentedLearners.size() - 1));

                    successResponse.setMessage("The course target audience has been successfully deleted");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;

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
    public SuccessResponse updateSectionName(UpdateSectionNameRequest updateSectionNameRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {

                    final String courseCode = updateSectionNameRequest.getCourseCode();
                    final Integer sectionId = updateSectionNameRequest.getSectionId();
                    final String sectionName = updateSectionNameRequest.getSectionName();

                    if (courseCode == null || courseCode.isEmpty() || sectionId == null || sectionId.toString().isEmpty() || sectionName == null || sectionName.isEmpty()) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    }
                    Course course = courseRepository.getCourseByCode(courseCode);
                    if (course == null) {
                        throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
                    }

                    if (course.getInstructorId().getGeneralUserProfile().getUserCode() != profile.getUserCode()) {
                        throw new ErrorException("You cannot access this course because it is not yours", VarList.RSP_NO_DATA_FOUND);
                    }

                    CourseSection courseSection = courseSectionRepository.getCourseSectionByCourseAndId(course, sectionId);
                    if (courseSection == null) {
                        throw new ErrorException("Course section not found", VarList.RSP_NO_DATA_FOUND);
                    }
                    courseSection.setSectionName(sectionName);
                    courseSectionRepository.save(courseSection);

                    successResponse.setMessage("Successfully updated course section name");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;

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
    public SuccessResponse updateCurriculumItemName(UpdateCurriculumItemNameRequest updateCurriculumItemNameRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    final Integer sectionId = updateCurriculumItemNameRequest.getCourseSectionId();
                    final Integer sectionCurriculumItemId = updateCurriculumItemNameRequest.getSectionCurriculumItemId();
                    final Integer curriculumItemTypeId = updateCurriculumItemNameRequest.getCurriculumItemTypeId();
                    final String name = updateCurriculumItemNameRequest.getName();
                    final String description = updateCurriculumItemNameRequest.getDescription();

                    if (sectionId == null || sectionId.toString().isEmpty() || sectionCurriculumItemId == null || sectionCurriculumItemId.toString().isEmpty() ||
                            curriculumItemTypeId == null || curriculumItemTypeId.toString().isEmpty() || name == null || name.isEmpty()) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    }

                    CourseSection courseSection = courseSectionRepository.getCourseSectionById(sectionId);
                    if (courseSection == null) {
                        throw new ErrorException("Invalid course section id", VarList.RSP_NO_DATA_FOUND);
                    }
                    if (courseSection.getCourse().getInstructorId().getGeneralUserProfile().getUserCode() != profile.getUserCode()) {
                        throw new ErrorException("You cannot access this course because it is not yours", VarList.RSP_NO_DATA_FOUND);
                    }
                    CurriculumItemType curriculumItemType = curriculumItemTypeRepository.getCurriculumItemTypeById(curriculumItemTypeId);
                    if (curriculumItemType == null) {
                        throw new ErrorException("Invalid curriculum item type id", VarList.RSP_NO_DATA_FOUND);
                    }
                    SectionCurriculumItem sectionCurriculumItem = sectionCurriculumItemRepository.getSectionCurriculumItemsByCourseSectionAndCurriculumItemTypeAndId(courseSection, curriculumItemType, sectionCurriculumItemId);

                    if (sectionCurriculumItem == null) {
                        throw new ErrorException("Section curriculum item not found", VarList.RSP_NO_DATA_FOUND);
                    }
                    sectionCurriculumItem.setTitle(name);
                    if (description != null && !description.isEmpty()) {
                        sectionCurriculumItem.setDescription(description);
                    }
                    sectionCurriculumItemRepository.save(sectionCurriculumItem);

                    successResponse.setMessage("Successfully updated the name of the course curriculum item");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;

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
    public SuccessResponse deleteExternalResources(Integer curriculumItemFileId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {

                    if (curriculumItemFileId == null || curriculumItemFileId.toString().isEmpty()) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    }

                    CurriculumItemFile curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileById(curriculumItemFileId);

                    if (curriculumItemFile == null) {
                        throw new ErrorException("Not found external resource", VarList.RSP_NO_DATA_FOUND);
                    }

                    if (curriculumItemFile.getCurriculumItemFileTypes().getId() != 4) {
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                    }

                    if (!curriculumItemFile.getSectionCurriculumItem().getCourseSection().getCourse().getInstructorId().getGeneralUserProfile().getUserCode().equals(profile.getUserCode())) {
                        throw new ErrorException("You cannot delete this external resource, it is not yours", VarList.RSP_NO_DATA_FOUND);
                    }

                    curriculumItemFileRepository.delete(curriculumItemFile);

                    successResponse.setMessage("External resource delete successfully");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;
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
    public SuccessResponse setPreviewVideo(SetPreviewVideoRequest setPreviewVideoRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    final String videoFileName = setPreviewVideoRequest.getVideoFileName();
                    final boolean isPreviewVideo = setPreviewVideoRequest.isPreviewVideo();

                    CurriculumItemFile curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileByUrl(videoFileName);
                    if (curriculumItemFile == null) {
                        throw new ErrorException("Invalid video file name", VarList.RSP_NO_DATA_FOUND);
                    }

                    if (!curriculumItemFile.getSectionCurriculumItem().getCourseSection().getCourse().getInstructorId().getGeneralUserProfile().getUserCode().equals(profile.getUserCode())) {
                        throw new ErrorException("You cannot access this course because it is not yours", VarList.RSP_NO_DATA_FOUND);
                    }

                    if (isPreviewVideo) {
                        curriculumItemFile.setIsPreviewVideo((byte) 1);
                        successResponse.setMessage("Preview video added successfully");
                    } else {
                        curriculumItemFile.setIsPreviewVideo((byte) 0);
                        successResponse.setMessage("The preview was successfully removed from the video");
                    }
                    curriculumItemFileRepository.save(curriculumItemFile);


                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;
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
    public SuccessResponse updateQuestionAndAnswers(UpdateQuestionAndAnswersRequest updateQuestionAndAnswersRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);

        if (profile == null || profile.getIsActive() != 1) {
            throw new ErrorException("User not active or not found", VarList.RSP_NO_DATA_FOUND);
        }

        if (profile.getGupType().getId() != 2) {
            throw new ErrorException("You are not an instructor to perform this operation", VarList.RSP_NO_DATA_FOUND);
        }

        final String question = updateQuestionAndAnswersRequest.getQuestion();
        final Integer quizId = updateQuestionAndAnswersRequest.getQuizId();
        final String[] answers = {
                updateQuestionAndAnswersRequest.getAnswer1(),
                updateQuestionAndAnswersRequest.getAnswer2(),
                updateQuestionAndAnswersRequest.getAnswer3(),
                updateQuestionAndAnswersRequest.getAnswer4(),
                updateQuestionAndAnswersRequest.getAnswer5()
        };
        final String[] explanations = {
                updateQuestionAndAnswersRequest.getExplanation1(),
                updateQuestionAndAnswersRequest.getExplanation2(),
                updateQuestionAndAnswersRequest.getExplanation3(),
                updateQuestionAndAnswersRequest.getExplanation4(),
                updateQuestionAndAnswersRequest.getExplanation5()
        };
        final Integer correctAnswer = updateQuestionAndAnswersRequest.getCorrectAnswer();

        if (quizId == null || quizId.toString().isEmpty()) {
            throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
        }

        if (correctAnswer == null || correctAnswer.toString().isEmpty() || answers.length == 0)
            throw new ErrorException("Please add a correct answer", VarList.RSP_NO_DATA_FOUND);

        Quiz quiz = quizRepository.getQuizById(quizId);
        if (quiz == null) {
            throw new ErrorException("Invalid quiz id", VarList.RSP_NO_DATA_FOUND);
        }

        if (!quiz.getSectionCurriculumItem().getCourseSection().getCourse().getInstructorId().getGeneralUserProfile().equals(profile))
            throw new ErrorException("You cannot perform this operation because this course is not yours", VarList.RSP_NO_DATA_FOUND);


        if (question != null && !question.isEmpty()) {
            quiz.setQuestion(question);
            quizRepository.save(quiz);
        }

        if (answers.length > 0) {
            List<Answer> answerList = answerRepository.getAnswerByQuiz(quiz);
            answerRepository.deleteAll(answerList);
        }

        for (int i = 0; i < answers.length; i++) {
            if (answers[i] != null && !answers[i].isEmpty()) {
                Answer answer = new Answer();
                answer.setName(answers[i]);
                answer.setExplanation((explanations[i] != null && !explanations[i].isEmpty()) ? explanations[i] : "");
                answer.setCorrectAnswer(correctAnswer != null && correctAnswer == (i + 1));
                answer.setQuiz(quiz);
                answerRepository.save(answer);
            }
        }
        successResponse.setMessage("Question and answers updated successfully");
        successResponse.setVariable(VarList.RSP_SUCCESS);
        return successResponse;
    }

    @Override
    public SuccessResponse deleteQuestionAndAnswers(int quizId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);

        if (profile == null || profile.getIsActive() != 1) {
            throw new ErrorException("User not active or not found", VarList.RSP_NO_DATA_FOUND);
        }

        if (profile.getGupType().getId() != 2) {
            throw new ErrorException("You are not an instructor to perform this operation", VarList.RSP_NO_DATA_FOUND);
        }

        Quiz quiz = quizRepository.getQuizById(quizId);
        if (quiz == null) {
            throw new ErrorException("Invalid quiz id", VarList.RSP_NO_DATA_FOUND);
        }

        if (!quiz.getSectionCurriculumItem().getCourseSection().getCourse().getInstructorId().getGeneralUserProfile().equals(profile))
            throw new ErrorException("You cannot perform this operation because this course is not yours", VarList.RSP_NO_DATA_FOUND);

        List<Answer> answerList = answerRepository.getAnswerByQuiz(quiz);

        if (answerList.size() > 0) {
            answerRepository.deleteAll(answerList);
        }

        quizRepository.delete(quiz);

        successResponse.setMessage("Questions and answers have been successfully deleted");
        successResponse.setVariable(VarList.RSP_SUCCESS);
        return successResponse;
    }

    @Override
    public SuccessResponse updateSectionCurriculumItemOrder(UpdateSectionCurriculumItemOrderRequest updateSectionCurriculumItemOrderRequest) {
        // Authentication and authorization checks
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile == null) {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
        if (profile.getIsActive() != 1) {
            throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
        }
        if (profile.getGupType().getId() != 2) {
            throw new ErrorException("You are not an instructor to perform this operation", VarList.RSP_NO_DATA_FOUND);
        }

        // Input validation
        final String courseCode = updateSectionCurriculumItemOrderRequest.getCourseCode();
        final Integer sectionId = updateSectionCurriculumItemOrderRequest.getSectionId();
        final Integer curriculumItemId = updateSectionCurriculumItemOrderRequest.getCurriculumItemId();
        final Integer arrangedNo = updateSectionCurriculumItemOrderRequest.getArrangedNo();

        if (courseCode == null || courseCode.isEmpty() ||
                sectionId == null || sectionId.toString().isEmpty() ||
                curriculumItemId == null || curriculumItemId.toString().isEmpty() ||
                arrangedNo == null || arrangedNo.toString().isEmpty()) {
            throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
        }

        // Retrieve course and validate
        Course course = courseRepository.getCourseByCode(courseCode);
        if (course == null) {
            throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
        }
        if (!course.getInstructorId().getGeneralUserProfile().equals(profile)) {
            throw new ErrorException("You cannot perform this operation because this course is not yours", VarList.RSP_NO_DATA_FOUND);
        }

        // Retrieve course section and validate
        CourseSection courseSection = courseSectionRepository.getCourseSectionById(sectionId);
        if (courseSection == null) {
            throw new ErrorException("Invalid section id", VarList.RSP_NO_DATA_FOUND);
        }

        // Retrieve curriculum item and validate
        SectionCurriculumItem sectionCurriculumItem = sectionCurriculumItemRepository.getSectionCurriculumItemById(curriculumItemId);
        if (sectionCurriculumItem == null) {
            throw new ErrorException("Invalid curriculum item id", VarList.RSP_NO_DATA_FOUND);
        }
        if (!sectionCurriculumItem.getCourseSection().getCourse().equals(course)) {
            throw new ErrorException("Invalid request, section curriculum item not in course", VarList.RSP_NO_DATA_FOUND);
        }

        // Update course section if necessary
        if (!sectionCurriculumItem.getCourseSection().equals(courseSection)) {
            sectionCurriculumItem.setCourseSection(courseSection);
            sectionCurriculumItemRepository.save(sectionCurriculumItem);
        }

        // Retrieve all curriculum items for the section
        List<SectionCurriculumItem> sectionCurriculumItems = sectionCurriculumItemRepository.findByCourseSectionOrderByArrangedNoAsc(courseSection);

        if (sectionCurriculumItems == null || sectionCurriculumItems.isEmpty()) {
            throw new ErrorException("No curriculum items found for the section", VarList.RSP_NO_DATA_FOUND);
        }

        // Validate arrangedNo
        if (arrangedNo < 1 || arrangedNo > sectionCurriculumItems.size()) {
            throw new ErrorException("The number in the order is invalid because the number is out of range", VarList.RSP_NO_DATA_FOUND);
        }

        // Ensure the item to rearrange exists in the list
        if (!sectionCurriculumItems.contains(sectionCurriculumItem)) {
            throw new ErrorException("Curriculum item not found in the section", VarList.RSP_NO_DATA_FOUND);
        }

        // Create a new list to avoid modifying the original during iteration
        List<SectionCurriculumItem> updatedItems = new ArrayList<>(sectionCurriculumItems);
        updatedItems.remove(sectionCurriculumItem); // Remove the item
        updatedItems.add(arrangedNo - 1, sectionCurriculumItem); // Insert at the new position

        // Update arranged_no for all items
        int index = 1;
        for (SectionCurriculumItem item : updatedItems) {
            item.setArrangedNo(index++);
        }

        // Save all items in a single transaction
        try {
            sectionCurriculumItemRepository.saveAll(updatedItems);
        } catch (Exception e) {
            throw new ErrorException("Failed to update curriculum item order: " + e.getMessage(), VarList.RSP_ERROR);
        }

        // Set success response
        successResponse.setMessage("The sequence of curriculum items was successfully updated");
        successResponse.setVariable(VarList.RSP_SUCCESS);
        return successResponse;
    }

    @Override
    public SuccessResponse updateQuizOrder(UpdateQuizOrderRequest updateQuizOrderRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {

                    final Integer sectionCurriculumItemId = updateQuizOrderRequest.getSectionCurriculumItemId();
                    final Integer[] quizOrder = updateQuizOrderRequest.getQuizOrder();

                    if (sectionCurriculumItemId == null || sectionCurriculumItemId.toString().isEmpty() || quizOrder == null || quizOrder.length == 0)
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);

                    SectionCurriculumItem sectionCurriculumItem = sectionCurriculumItemRepository.getSectionCurriculumItemById(sectionCurriculumItemId);
                    if (sectionCurriculumItem == null)
                        throw new ErrorException("Invalid section curriculum item id", VarList.RSP_NO_DATA_FOUND);

                    if (!sectionCurriculumItem.getCourseSection().getCourse().getInstructorId().getGeneralUserProfile().equals(profile))
                        throw new ErrorException("You cannot access this quiz because this course is not yours", VarList.RSP_NO_DATA_FOUND);

                    List<Quiz> quizList = quizRepository.getQuizBySectionCurriculumItem(sectionCurriculumItem);
                    Answer updateAnswer;

                    for (int i = 0; i < quizOrder.length; i++) {
                        for (Quiz quiz : quizList) {
                            if (quiz.getId() == quizOrder[i]) {
                                Quiz updateQuiz = new Quiz();
                                updateQuiz.setQuestion(quiz.getQuestion());
                                updateQuiz.setSectionCurriculumItem(quiz.getSectionCurriculumItem());
                                updateQuiz.setIsDelete((byte) 0);
                                quizRepository.save(updateQuiz);
                                List<Answer> answerList = answerRepository.getAnswerByQuiz(quiz);
                                for (Answer answer : answerList) {
                                    updateAnswer = new Answer();
                                    updateAnswer.setCorrectAnswer(answer.getCorrectAnswer());
                                    updateAnswer.setExplanation(answer.getExplanation());
                                    updateAnswer.setName(answer.getName());
                                    updateAnswer.setQuiz(updateQuiz);
                                    answerRepository.save(updateAnswer);
                                    answerRepository.delete(answer);
                                }
                                quizRepository.delete(quiz);
                            }
                        }
                    }

                    successResponse.setMessage("Quiz order updated successfully");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;
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
    public SuccessResponse updateCourseSectionOrder(UpdateCourseSectionOrderRequest updateCourseSectionOrderRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {

                    final String courseCode = updateCourseSectionOrderRequest.getCourseCode();
                    final Integer[] courseSectionOrder = updateCourseSectionOrderRequest.getCourseSectionOrder();

                    if (courseCode == null || courseCode.isEmpty() || courseSectionOrder == null || courseSectionOrder.length == 0)
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);

                    Course course = courseRepository.getCourseByCode(courseCode);
                    if (course == null)
                        throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);

                    if (!course.getInstructorId().getGeneralUserProfile().equals(profile))
                        throw new ErrorException("You cannot access this course section because this course is not yours", VarList.RSP_NO_DATA_FOUND);

                    List<CourseSection> courseSections = courseSectionRepository.getCourseSectionByCourse(course);

                    if (courseSections.size() != courseSectionOrder.length)
                        throw new ErrorException("Course section order does not depend on the number of course sections", VarList.RSP_NO_DATA_FOUND);

                    for (int i = 0; i < courseSectionOrder.length; i++) {
                        Integer courseSectionId = courseSectionOrder[i];
                        CourseSection courseSection = courseSectionRepository.getCourseSectionById(courseSectionId);

                        if (courseSection == null)
                            throw new ErrorException("Invalid course section id: " + courseSectionId, VarList.RSP_NO_DATA_FOUND);
                        courseSection.setArrangedNo(i + 1);
                        courseSectionRepository.save(courseSection);

                    }

                    successResponse.setMessage("Successfully updated course section order");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;
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
    public SuccessResponse setPublishCourse(String code) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {

                    Course course = courseRepository.getCourseByCode(code);
                    if (!course.getInstructorId().getGeneralUserProfile().equals(profile))
                        throw new ErrorException("You cannot access this course because this course is not yours", VarList.RSP_NO_DATA_FOUND);

                    ApprovalType approvalType = approvalTypeRepository.getApprovalTypeById(5);
                    course.setApprovalType(approvalType);

                    courseRepository.save(course);

                    successResponse.setMessage("Your course has been successfully published");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;
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

    private void quizArrangement(SectionCurriculumItem sectionCurriculumItem, CourseSection updateCourseSection) {
        SectionCurriculumItem updateSectionCurriculumItem;
        Quiz updateQuiz;
        Answer updateAnswer;
        updateSectionCurriculumItem = new SectionCurriculumItem();
        updateSectionCurriculumItem.setArticle(sectionCurriculumItem.getArticle());
        updateSectionCurriculumItem.setDescription(sectionCurriculumItem.getDescription());
        updateSectionCurriculumItem.setTitle(sectionCurriculumItem.getTitle());
        updateSectionCurriculumItem.setCourseSection(updateCourseSection);
        updateSectionCurriculumItem.setCurriculumItemType(sectionCurriculumItem.getCurriculumItemType());
        updateSectionCurriculumItem.setIsDelete((byte) 0);
        sectionCurriculumItemRepository.save(updateSectionCurriculumItem);
        if (sectionCurriculumItem.getArrangedNo() != null && !sectionCurriculumItem.getArrangedNo().toString().isEmpty())
            updateSectionCurriculumItem.setArrangedNo(sectionCurriculumItem.getArrangedNo());
        List<Quiz> quizList = quizRepository.getQuizBySectionCurriculumItem(sectionCurriculumItem);
        for (Quiz quiz : quizList) {
            updateQuiz = new Quiz();
            updateQuiz.setQuestion(quiz.getQuestion());
            updateQuiz.setSectionCurriculumItem(updateSectionCurriculumItem);
            updateQuiz.setIsDelete((byte) 0);
            quizRepository.save(updateQuiz);
            List<Answer> answerList = answerRepository.getAnswerByQuiz(quiz);
            for (Answer answer : answerList) {
                updateAnswer = new Answer();
                updateAnswer.setCorrectAnswer(answer.getCorrectAnswer());
                updateAnswer.setExplanation(answer.getExplanation());
                updateAnswer.setName(answer.getName());
                updateAnswer.setQuiz(updateQuiz);
                answerRepository.save(updateAnswer);
                answerRepository.delete(answer);
            }
            quizRepository.delete(quiz);
        }
        sectionCurriculumItemRepository.delete(sectionCurriculumItem);
    }
}