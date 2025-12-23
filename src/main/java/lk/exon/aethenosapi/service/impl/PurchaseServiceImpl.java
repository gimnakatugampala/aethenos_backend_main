package lk.exon.aethenosapi.service.impl;

import lk.exon.aethenosapi.config.Config;
import lk.exon.aethenosapi.config.EmailConfig;
import lk.exon.aethenosapi.entity.*;
import lk.exon.aethenosapi.entity.Currency;
import lk.exon.aethenosapi.exception.ErrorException;
import lk.exon.aethenosapi.payload.request.*;
import lk.exon.aethenosapi.entity.Coupon;
import lk.exon.aethenosapi.entity.CouponPrice;
import lk.exon.aethenosapi.entity.GeneralUserProfile;
import lk.exon.aethenosapi.payload.response.*;
import lk.exon.aethenosapi.repository.*;
import lk.exon.aethenosapi.service.PurchaseService;
import lk.exon.aethenosapi.utils.EmailSender;
import lk.exon.aethenosapi.utils.PdfGenerater;
import lk.exon.aethenosapi.utils.VarList;
import org.hibernate.NonUniqueResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    @Autowired
    private UserProfileServiceImpl userProfileServiceImpl;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private CourseRepository courseRepository;
    private SuccessResponse successResponse = new SuccessResponse();
    @Autowired
    private CouponPriceRepository couponPriceRepository;
    @Autowired
    private CoursePriceRepository coursePriceRepository;
    @Autowired
    private CourseSectionRepository courseSectionRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderHasCourseRepository orderHasCourseRepository;
    @Autowired
    private ReplyToReviewRepository replyToReviewRepository;
    @Autowired
    private CourseLandingPageRepository courseLandingPageRepository;
    @Autowired
    private SectionCurriculumItemRepository sectionCurriculumItemRepository;
    @Autowired
    private CurriculumItemFileRepository curriculumItemFileRepository;
    @Autowired
    private CourseIntentedLearnerRepository courseIntentedLearnerRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private VatRepository vatRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private ReadCurriculumItemRepository readCurriculumItemRepository;
    @Autowired
    private RefundsRepository refundsRepository;
    @Autowired
    private RefundStatusRepository refundStatusRepository;
    @Autowired
    private CurriculumItemFileTypeRepository curriculumItemFileTypeRepository;
    @Autowired
    private AssignmentRepository assignmentRepository;
    @Autowired
    private PracticeTestRepository practiceTestRepository;
    @Autowired
    private CodingExerciseRepository codingExerciseRepository;
    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private SpringTemplateEngine springTemplateEngine;
    @Autowired
    private PdfGenerater pdfGenerater;
    @Autowired
    private InstructorCourseRevenueRepository instructorCourseRevenueRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ApprovedRefundsRepository approvedRefundsRepository;
    @Autowired
    private PreviousViewRepository previousViewRepository;
    @Autowired
    private CoursePurchaseTypeRepository coursePurchaseTypeRepository;
    @Autowired
    private RevenueSplitRepository revenueSplitRepository;
    @Autowired
    private RevenueSplitTypeRepository revenueSplitTypeRepository;
    @Autowired
    private InstructorProfileRepository instructorProfileRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private CourseKeywordRepository courseKeywordRepository;
    @Autowired
    private RevenueSplitHistoryRepository revenueSplitHistoryRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private EuroCountryRepository euroCountryRepository;
    @Autowired
    private CompanyRevenueRepository companyRevenueRepository;
    @Autowired
    private RevenueRepository revenueRepository;
    @Autowired
    private GeneralUserProfileRepository generalUserProfileRepository;
    @Autowired
    private GupTypeRepository gupTypeRepository;
    @Autowired
    private StudentBuyCouponCourseRepository studentBuyCouponCourseRepository;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter updatedFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    //    DecimalFormat decimalFormat = new DecimalFormat("0.00");
    DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

    private final String API_URL = "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/{currencyCode}.json";

    @Override
    public CouponValidationResponse getCouponValidationByCode(String code) {

        Coupon coupon = couponRepository.getCouponByCode(code);
        if (coupon != null) {

            if (coupon.getIsActive() == 1) {

                Date currentDate = new Date();

                Date databaseDate = coupon.getEndDate();

                CouponValidationResponse couponValidationResponse = new CouponValidationResponse();

                if (currentDate.after(databaseDate)) {
                    couponValidationResponse.setValidation("This coupon has expired");
                } else {

                    if (coupon.getCouponType().getId() == 1) {
                        List<StudentBuyCouponCourse> studentBuyCouponCourses = studentBuyCouponCourseRepository.getStudentBuyCouponCourseByCoupon(coupon);
                        if (studentBuyCouponCourses.size() >= 1000) {
                            throw new ErrorException("More than 1000 have been used", VarList.RSP_NO_DATA_FOUND);
                        }
                    }


                    couponValidationResponse.setValidation("This coupon is valid");
                    couponValidationResponse.setCouponType(coupon.getCouponType().getName());
                    couponValidationResponse.setCouponTypeId(coupon.getCouponType().getId());
                    couponValidationResponse.setCourse_Id(coupon.getCourse().getId());
                    couponValidationResponse.setCourse_code(coupon.getCourse().getCode());
                    couponValidationResponse.setStart_date(coupon.getStartDate());
                    couponValidationResponse.setEnd_date(coupon.getEndDate());

                    if (coupon.getCouponType().getId() == 2) {

                        couponValidationResponse.setGlobal_list_price(coupon.getGlobal_list_price());
                        couponValidationResponse.setGlobal_discount_price(coupon.getGlobal_discount_price());
                        couponValidationResponse.setGlobal_discount_percentage(coupon.getGlobal_discount_percentage());
                        couponValidationResponse.setGlobal_discount(coupon.getGlobal_discount());

                        List<CouponPrice> couponPriceList = couponPriceRepository.getCouponPricesByCoupon(coupon);

                        List<CouponPricingResponse> couponPricingResponses = new ArrayList<>();

                        for (CouponPrice couponPrice : couponPriceList) {
                            CouponPricingResponse couponPricingResponse = new CouponPricingResponse();
                            couponPricingResponse.setDiscount(couponPrice.getDiscount());
                            couponPricingResponse.setDiscountAmount(couponPrice.getDiscountAmount());
                            couponPricingResponse.setDiscountPrice(couponPrice.getDiscountPrice());
                            couponPricingResponse.setListPrice(couponPrice.getListPrice());
                            couponPricingResponse.setCountryName(couponPrice.getCountry().getName());
                            couponPricingResponse.setCountryId(couponPrice.getCountry().getId());
                            couponPricingResponses.add(couponPricingResponse);
                        }
                        couponValidationResponse.setCourse_prices(couponPricingResponses);
                    }

                }
                return couponValidationResponse;
            } else {
                throw new ErrorException("Coupon is deactivated", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("Invalid coupon", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public List<GetCoursesDataResponse> getCoursesPurchasedByTheStudent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                List<Order> orders = orderRepository.getOrdersByGeneralUserProfile(profile);
                List<GetCoursesDataResponse> responseLists = new ArrayList<>();
                for (Order order : orders) {
                    List<OrderHasCourse> orderHasCourses = orderHasCourseRepository.getOrderHasCoursesByOrder(order);
                    for (OrderHasCourse orderHasCourse : orderHasCourses) {
                        if (orderHasCourse.getIsDelete() == null || orderHasCourse.getIsDelete() == 0) {
                            CourseLandingPage courseLandingPage = courseLandingPageRepository.getCourseLandingPagesByCourse(orderHasCourse.getCourse());
                            if (courseLandingPage != null) {
                                Course course = orderHasCourse.getCourse();

                                GetCoursesDataResponse getCoursesDataResponse = new GetCoursesDataResponse();
                                getCoursesDataResponse.setProgressValue(orderHasCourse.getProgress());
                                getCoursesDataResponse.setItem_code(orderHasCourse.getItemCode());
                                getCoursesDataResponse.setPurchasedDate(orderHasCourse.getOrder().getBuyDate());
                                getCoursesDataResponse.setCourse_code(course.getCode());
                                getCoursesDataResponse.setTotalVideoLength(course.getCourseLength());
                                getCoursesDataResponse.setCreated_date(course.getCreatedDate().toString());
                                getCoursesDataResponse.setId(course.getId());
                                getCoursesDataResponse.setImg(course.getImg());
                                getCoursesDataResponse.setDuration(Double.toString(course.getCourseLength()));
                                getCoursesDataResponse.setIsPaid(course.getIsPaid() == 2);
                                getCoursesDataResponse.setCourse_outline("OutLine is not added to the course");
                                getCoursesDataResponse.setLevel((courseLandingPage != null) ? courseLandingPage.getCourseLevel().getName() : null);
                                getCoursesDataResponse.setTitle(course.getCourseTitle());
                                List<Review> reviews = reviewRepository.getReviewsByCourse(orderHasCourse.getCourse());
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
                                    getRatingResponses.add(getRatingResponse);
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
                                getCoursesDataResponse.setReviews(getRatingResponses);

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
                                getCoursesDataResponse.setStudent(0);
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
                                    getCourseContentResponse.setArrangedNo(courseSectionObj.getArrangedNo());
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
                                            getCurriculumItemFilesResponse.setVideoLength(curriculumItemFile.getVideoLength() == null ? 0 : curriculumItemFile.getVideoLength());
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
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }

    }

    @Override
    public SuccessResponse addToStudentsPurchasedCourses(AddPurchasedCoursesRequest addPurchasedCoursesRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {

                String paymentMethodID = addPurchasedCoursesRequest.getPaymentMethod();
                List<GetCourseDetailsToBuyRequest> getCourseDetailsToBuyRequests = addPurchasedCoursesRequest.getCourses();
                Integer courseType = addPurchasedCoursesRequest.getCourseType();

                if (paymentMethodID.isEmpty() || paymentMethodID == null) {
                    throw new ErrorException("Payment method id not included", VarList.RSP_NO_DATA_FOUND);
                }
                if (addPurchasedCoursesRequest.getCountry() == null || addPurchasedCoursesRequest.getCountry().isEmpty()) {
                    throw new ErrorException("Country not included", VarList.RSP_NO_DATA_FOUND);
                }
                PaymentMethod paymentMethod = paymentMethodRepository.getPaymentMethodById(Integer.parseInt(paymentMethodID));
                if (paymentMethod == null) {
                    throw new ErrorException("Invalid payment method id", VarList.RSP_NO_DATA_FOUND);
                }

                List<CourseWithPrice> courseWithPrices;

                List<Order> orderList = orderRepository.getOrdersByGeneralUserProfile(profile);

                //Validate Order List
                validateOrderList(orderList, getCourseDetailsToBuyRequests);

                //Create CourseWithPrices List
                courseWithPrices = createCourseWithPricesList(getCourseDetailsToBuyRequests);

                Properties properties;
                if (courseWithPrices.size() > 0) {
                    Order order = new Order();

                    try {
                        order.setBuyDate(new Date());
                        order.setGeneralUserProfile(profile);
                        order.setCurrency(addPurchasedCoursesRequest.getCurrency().toUpperCase());
                        order.setPaymentMethod(paymentMethod);
                        order.setDiscount(addPurchasedCoursesRequest.getDiscount());
                        order.setTotal(addPurchasedCoursesRequest.getTotalPrice());
                        orderRepository.save(order);

                    } catch (NullPointerException exception) {
                        throw new ErrorException("Please set discount, if not discount, please add 0", VarList.RSP_NO_DATA_FOUND);
                    }

                    List<String> courseNames = new ArrayList<>();
                    List<String> listPrices = new ArrayList<>();
                    List<String> yourPrices = new ArrayList<>();
                    Set<InstructorProfile> instructorProfiles = new HashSet<>();
//                    double vatAmount = 0.0;
                    double vatPercentage = 0.0;

                    Vat vat = vatRepository.getVatBycountry(addPurchasedCoursesRequest.getCountry());
                    if (vat != null) {
                        vatPercentage = vat.getVat();
                    }

                    CoursePurchaseType coursePurchaseType;
                    if (courseType == null || courseType.toString().isEmpty() || courseType == 1) {
                        coursePurchaseType = coursePurchaseTypeRepository.getCoursePurchaseTypeById(1);
                    } else {
                        coursePurchaseType = coursePurchaseTypeRepository.getCoursePurchaseTypeById(courseType);
                    }

                    String currency = order.getCurrency().toLowerCase();
                    double exchangeRateToUsd = 1;
                    if (!currency.equals("usd")) {
                        Map<String, Double> exchangeRates = getExchangeRates(currency);
                        exchangeRateToUsd = exchangeRates.getOrDefault("usd", 1.0);
                    }

                    PaymentProcessingFeeResult processingFeeResult = paymentProcessingFeeCalculate(paymentMethod, addPurchasedCoursesRequest.getProcessingFee(), exchangeRateToUsd, addPurchasedCoursesRequest.getStripe_pf_currency());

                    Country country = countryRepository.getCountryByName(addPurchasedCoursesRequest.getCountry());
                    if (country == null) {
                        EuroCountry euroCountry = euroCountryRepository.getEuroCountryByName(addPurchasedCoursesRequest.getCountry());
                        if (euroCountry == null) {
                            country = countryRepository.getCountryById(30);
                        } else {
                            country = countryRepository.getCountryById(8);
                        }
                    }


                    Transaction transaction = addTransaction(country, order, processingFeeResult, courseWithPrices, vatPercentage, vat, exchangeRateToUsd);

                    for (CourseWithPrice courseWithPrice : courseWithPrices) {
                        OrderHasCourse orderHasCourse = new OrderHasCourse();
                        orderHasCourse.setOrder(order);
                        orderHasCourse.setCurrrency(courseWithPrice.getCurrency().getName().toUpperCase());
                        orderHasCourse.setItemCode(UUID.randomUUID().toString());
                        orderHasCourse.setItemPrice(courseWithPrice.getItemPrice());
                        orderHasCourse.setListPrice(courseWithPrice.getListPrice());
                        orderHasCourse.setCourse(courseWithPrice.getCourse());
                        orderHasCourse.setCoursePurchaseType(coursePurchaseType);
                        orderHasCourseRepository.save(orderHasCourse);

                        if (courseWithPrice.getCoupon() != null) {
                            StudentBuyCouponCourse studentBuyCouponCourse = new StudentBuyCouponCourse();
                            studentBuyCouponCourse.setCoupon(courseWithPrice.getCoupon());
                            studentBuyCouponCourse.setOrderHasCourse(orderHasCourse);
                            studentBuyCouponCourseRepository.save(studentBuyCouponCourse);
                        }

                        courseNames.add(courseWithPrice.getCourse().getCourseTitle());

                        listPrices.add(courseWithPrice.getCurrency().getName().toUpperCase() + " " + decimalFormat.format(courseWithPrice.getListPrice()));

                        yourPrices.add(courseWithPrice.getCurrency().getName().toUpperCase() + " " + decimalFormat.format(courseWithPrice.getItemPrice()));

                        instructorProfiles.add(courseWithPrice.getCourse().getInstructorId());

                        Notification notification = new Notification();
                        notification.setNotificationCode(UUID.randomUUID().toString());
                        String message = "Student " + profile.getFirstName() + " " + profile.getLastName() + " has purchased your course \"" + courseWithPrice.getCourse().getCourseTitle() + "\"";
                        notification.setNotification(message);
                        notification.setNotificationTime(new Date());
                        notification.setGeneralUserProfile(courseWithPrice.getCourse().getInstructorId().getGeneralUserProfile());
                        notification.setRead(false);
                        notificationRepository.save(notification);

                        //instructor revenue update
                        InstructorRevenueCalculate(orderHasCourse, transaction, vatPercentage, processingFeeResult.getProcessingFee(), transaction.getAmount());
                    }

                    try {
                        EmailSender emailSender = new EmailSender();

                        properties = EmailConfig.getEmailProperties(order.getBuyDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(updatedFormatter),
                                transaction.getTransactionCode(), courseNames, order.getCurrency() + " " + decimalFormat.format(transaction.getAmount()), String.format("%.2f%%", transaction.getVatPercentage()), order.getCurrency().toUpperCase() + " " + decimalFormat.format(transaction.getVatAmount()), order.getCurrency().toUpperCase() + " " + decimalFormat.format(transaction.getAmount()), profile.getFirstName() + " " + profile.getLastName(), paymentMethod.getId() == 1 ? "credit or debit card" : paymentMethod.getMethod());
                        properties.put("listPrices", listPrices);
                        properties.put("yourPrices", yourPrices);
                        emailSender.sendEmail("PurchasedReciept",
                                profile.getEmail(),
                                (String) properties.get("from"),
                                (String) properties.get("subject"),
                                properties
                        );
                        List<Course> courses = null;
                        for (InstructorProfile instructorProfile : instructorProfiles) {
                            courses = new ArrayList<>();
                            for (int i = 0; courseNames.size() > i; i++) {
                                Course course = courseRepository.getCourseByCourseTitle(courseNames.get(i));
                                if (course.getInstructorId().getId() == instructorProfile.getId()) {
                                    courses.add(course);
                                }
                            }
                            properties = EmailConfig.getEmailProperties(instructorProfile.getGeneralUserProfile().getFirstName() + " " + instructorProfile.getGeneralUserProfile().getLastName(), profile.getFirstName() + " " + profile.getLastName(), courses, "New Student Enrollment Notification");

                            emailSender = new EmailSender();
                            emailSender.sendEmail("NotifyInstructorStudentPurchases", instructorProfile.getGeneralUserProfile().getEmail(), (String) properties.getProperty("from"), (String) properties.get("subject"), properties);

                        }

                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }

                    successResponse.setMessage("Purchased successfully");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                }

                return successResponse;

            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    private PaymentProcessingFeeResult paymentProcessingFeeCalculate(PaymentMethod paymentMethod, Double processingFee, double exchangeRateToUsd, String stripe_pf_currency) {
        double stripe_pf_exchange_rate = 1.0;
        if (processingFee == null || processingFee.toString().isEmpty() || processingFee == 0) {
            return new PaymentProcessingFeeResult(0.0, stripe_pf_exchange_rate);
        }

        if (paymentMethod.getId() == 1) {
            if (stripe_pf_currency == null || stripe_pf_currency.isEmpty())
                throw new ErrorException("Please add a stripe_pf_currency", VarList.RSP_NO_DATA_FOUND);
            processingFee /= exchangeRateToUsd;

            Map<String, Double> exchangeRates = getExchangeRates(stripe_pf_currency);

            if (exchangeRates != null && exchangeRates.containsKey("usd")) {
                stripe_pf_exchange_rate = exchangeRates.get("usd");
            }
        }
        return new PaymentProcessingFeeResult(processingFee, stripe_pf_exchange_rate, stripe_pf_currency);
    }

    private List<CourseWithPrice> createCourseWithPricesList(List<GetCourseDetailsToBuyRequest> getCourseDetailsToBuyRequests) {
        List<CourseWithPrice> courseWithPrices = new ArrayList<>();
        for (GetCourseDetailsToBuyRequest getCourseDetailsToBuyRequest : getCourseDetailsToBuyRequests) {
            Course course = courseRepository.getCourseByCode(getCourseDetailsToBuyRequest.getCourseCode());
            if (course == null) {
                throw new ErrorException("Invalid course code: " + getCourseDetailsToBuyRequest.getCourseCode(), VarList.RSP_NO_DATA_FOUND);
            }
            Currency currency = null;
            try {
                currency = currencyRepository.getCurrencyByName(getCourseDetailsToBuyRequest.getCurrency().toUpperCase());

                if (currency == null) {
                    throw new ErrorException("Invalid currency", VarList.RSP_NO_DATA_FOUND);
                }
            } catch (NonUniqueResultException e) {
                currency = currencyRepository.getCurrencyById(1);
            } catch (IncorrectResultSizeDataAccessException e) {
                currency = currencyRepository.getCurrencyById(1);
            }
            CoursePrice coursePrice = coursePriceRepository.getCoursePriceByCourseAndCurrency(course, currency);
            if (coursePrice == null) {
                throw new ErrorException("There are no prices for this course, please check it out", VarList.RSP_NO_DATA_FOUND);
            }
            String couponCode = getCourseDetailsToBuyRequest.getCouponCode();
            CourseWithPrice courseWithPrice = new CourseWithPrice();
            if (couponCode != null && !couponCode.isEmpty()) {
                Coupon coupon = couponRepository.getCouponByCodeAndCourse(couponCode, course);
                if (coupon != null) {
                    if (coupon.getCouponType().getId() == 1) {
                        List<StudentBuyCouponCourse> studentBuyCouponCourses = studentBuyCouponCourseRepository.getStudentBuyCouponCourseByCoupon(coupon);
                        if (studentBuyCouponCourses.size() >= 1000) {
                            throw new ErrorException("More than 1000 have been used", VarList.RSP_NO_DATA_FOUND);
                        }
                    }
                    courseWithPrice.setCoupon(coupon);
                }
            }

            courseWithPrice.setItemPrice((getCourseDetailsToBuyRequest.getItemPrice() == null || getCourseDetailsToBuyRequest.getItemPrice().toString().isEmpty()) ? 0 : getCourseDetailsToBuyRequest.getItemPrice());
            courseWithPrice.setListPrice((getCourseDetailsToBuyRequest.getListPrice() == null || getCourseDetailsToBuyRequest.getListPrice().toString().isEmpty()) ? 0 : getCourseDetailsToBuyRequest.getListPrice());
            courseWithPrice.setCourse(course);
            courseWithPrice.setCurrency(currency);
            courseWithPrice.setCoursePrice(coursePrice);
            courseWithPrices.add(courseWithPrice);

        }
        return courseWithPrices;
    }

    private void validateOrderList(List<Order> orderList, List<GetCourseDetailsToBuyRequest> getCourseDetailsToBuyRequests) {
        for (Order orderObj : orderList) {
            List<OrderHasCourse> orderHasCourses = orderHasCourseRepository.getOrderHasCoursesByOrder(orderObj);

            for (OrderHasCourse orderHasCourse : orderHasCourses) {
                for (GetCourseDetailsToBuyRequest getCourseDetailsToBuyRequest : getCourseDetailsToBuyRequests) {
                    Course course = courseRepository.getCourseByCode(getCourseDetailsToBuyRequest.getCourseCode());


                    if (course == null) {
                        throw new ErrorException("Invalid course code: " + getCourseDetailsToBuyRequest.getCourseCode(), VarList.RSP_NO_DATA_FOUND);
                    }
                    if (orderHasCourse.getCourse() == course) {
                        throw new ErrorException("User purchased this course: " + course.getCourseTitle(), VarList.RSP_NO_DATA_FOUND);
                    }
                    if (getCourseDetailsToBuyRequest.getCouponCode() != null && !getCourseDetailsToBuyRequest.getCouponCode().isEmpty()) {
                        Coupon coupon = couponRepository.getCouponByCodeAndCourse(getCourseDetailsToBuyRequest.getCouponCode(), course);
                        if (coupon == null)
                            throw new ErrorException("Invalid coupon code: " + getCourseDetailsToBuyRequest.getCouponCode(), VarList.RSP_NO_DATA_FOUND);
                        if ((!(coupon.getStartDate().after(new Date()) || coupon.getStartDate().equals(new Date())) ||
                                !(coupon.getEndDate().before(new Date()) || coupon.getEndDate().equals(new Date()))) &&
                                coupon.getIsActive() != 1) {
                            throw new ErrorException("Expired coupon code: " + getCourseDetailsToBuyRequest.getCouponCode(), VarList.RSP_NO_DATA_FOUND);
                        }
                    }

                }
            }
        }
    }

    private Transaction addTransaction(Country country, Order order, PaymentProcessingFeeResult processingFeeResult, List<CourseWithPrice> courseWithPrices, double vatPercentage, Vat vat, double exchangeRateToUsd) {
        RevenueSplitHistory revenueSplitHistory = revenueSplitHistoryRepository.findTopByOrderByChangedDateDesc();
        Transaction transaction = new Transaction();
        transaction.setTransactionCode(UUID.randomUUID().toString());
        transaction.setCreatedDate(order.getBuyDate());
        transaction.setOrder(order);
        transaction.setRevenueSplitHistory(revenueSplitHistory);
        transaction.setCountry(country);

        double subTotal = calculateSubTotal(courseWithPrices);
        double vatAmount = (subTotal / (1 + (vatPercentage / 100))) * (vatPercentage / 100);
        if (vat != null)
            transaction.setVat(vat);

        transaction.setAmount(subTotal);
        transaction.setVatPercentage(vatPercentage);
        transaction.setVatAmount(vatAmount);


        transaction.setUsdRate(exchangeRateToUsd);
        transaction.setPaymentProcessingFee(processingFeeResult.getProcessingFee());
        transaction.setStripe_pf_currency(processingFeeResult.getProcessingFeeCurrency());
        transaction.setStripe_pf_exchange_rate(processingFeeResult.getStripePfExchangeRate());
        transactionRepository.save(transaction);
        return transaction;
    }

    private double calculateSubTotal(List<CourseWithPrice> courseWithPrices) {
        double subTotal = 0;
        for (CourseWithPrice courseWithPrice : courseWithPrices) {
            subTotal += courseWithPrice.getItemPrice();
        }
        return subTotal;
    }

    private void InstructorRevenueCalculate(OrderHasCourse orderHasCourse, Transaction transaction, double vatPercentage, double processingFee, double totalSellingPrice) {
        System.out.println("/////////////vat percetange " + vatPercentage);
        double tax = (orderHasCourse.getItemPrice() / (1 + (vatPercentage / 100))) * (vatPercentage / 100);
        Revenue revenue = new Revenue();
        revenue.setOrderHasCourse(orderHasCourse);
        if (Double.isNaN(tax)) {
            tax = 0.0; // Or handle it with another fallback value
        }
        revenue.setTax(tax);
        double paymentProcessingFee = calculateProcesingFeeForCourse(processingFee, totalSellingPrice, orderHasCourse.getItemPrice());
        if (Double.isNaN(paymentProcessingFee)) {
            paymentProcessingFee = 0.0; // Or handle it accordingly
        }
        revenue.setProcessingFee(paymentProcessingFee);
        // Calculate net sale and validate it
        double netSale = orderHasCourse.getItemPrice() - tax - paymentProcessingFee;
        if (Double.isNaN(netSale) || netSale < 0) {
            netSale = 0.0; // Handle invalid net sale values
        }
        revenue.setNetSale(netSale);
        revenue.setTransaction(transaction);
        revenue.setCreatedDate(new Date());

        revenueRepository.save(revenue);

        double instructorRevenueSplitAmount = 0;
        double companyRevenueSplitAmount = 0;

        RevenueSplitType revenueSplitType;
        RevenueSplit revenueSplit;
        if (orderHasCourse.getCoursePurchaseType() == null || orderHasCourse.getCoursePurchaseType().getId() == 1) {
            revenueSplitType = revenueSplitTypeRepository.getRevenueSplitTypeById(1);
            if (revenueSplitType == null)
                throw new ErrorException("Revenue split type not found", VarList.RSP_NO_DATA_FOUND);
            revenueSplit = revenueSplitRepository.getRevenueSplitByRevenueSplitType(revenueSplitType);
            if (revenueSplit == null)
                throw new ErrorException("Revenue split not found", VarList.RSP_NO_DATA_FOUND);
            instructorRevenueSplitAmount = revenue.getNetSale() * (revenueSplit.getInstructorRevenue() / 100);
            companyRevenueSplitAmount = revenue.getNetSale() * (revenueSplit.getAethenosRevenue() / 100);
        } else if (orderHasCourse.getCoursePurchaseType().getId() == 3 || orderHasCourse.getCoursePurchaseType().getId() == 4) {
            revenueSplitType = revenueSplitTypeRepository.getRevenueSplitTypeById(2);
            if (revenueSplitType == null)
                throw new ErrorException("Revenue split type not found", VarList.RSP_NO_DATA_FOUND);
            revenueSplit = revenueSplitRepository.getRevenueSplitByRevenueSplitType(revenueSplitType);
            if (revenueSplit == null)
                throw new ErrorException("Revenue split not found", VarList.RSP_NO_DATA_FOUND);
            instructorRevenueSplitAmount = revenue.getNetSale() * (revenueSplit.getInstructorRevenue() / 100);
            companyRevenueSplitAmount = revenue.getNetSale() * (revenueSplit.getAethenosRevenue() / 100);
        }

        InstructorCourseRevenue instructorCourseRevenue = new InstructorCourseRevenue();
        instructorCourseRevenue.setRevenue(revenue);
        instructorCourseRevenue.setInstructorShare(instructorRevenueSplitAmount);
        instructorCourseRevenue.setInstructorProfile(orderHasCourse.getCourse().getInstructorId());
        instructorCourseRevenueRepository.save(instructorCourseRevenue);

        CompanyRevenue companyRevenue = new CompanyRevenue();
        companyRevenue.setRevenue(revenue);
        companyRevenue.setCompanyShare(companyRevenueSplitAmount);
        companyRevenueRepository.save(companyRevenue);

//        double totalGrossRevenue = 0.0;
//
//
//        LocalDate currentDate = LocalDate.now();
//        YearMonth currentYearMonth = YearMonth.from(currentDate);
//        InstructorCourseRevenue instructorCourseRevenue = instructorCourseRevenueRepository.getInstructorRevenueByInstructorProfileAndRevenueForMonthAndRevenueForYear(orderHasCourse.getCourse().getInstructorId(), currentYearMonth.getMonthValue(), currentYearMonth.getYear());
//        double amountInUsd;
//
//        System.out.println("orderHasCourse.getCurrrency().toLowerCase()//////////////" + orderHasCourse.getCurrrency().toLowerCase());
//        System.out.println("Ok///" + !orderHasCourse.getCurrrency().equalsIgnoreCase("usd"));
//        if (!orderHasCourse.getCurrrency().equalsIgnoreCase("usd")) {
//
//            Map<String, Double> exchangeRates = getExchangeRates(orderHasCourse.getCurrrency().toLowerCase()); // orderHasCourse.getCurrrency()
//            double exchangeRateToUsd = exchangeRates.getOrDefault("usd", 1.0);  // Default to 1.0 if rate not found
//
//            System.out.println("//////////////// exchangeRateToUsd " + exchangeRateToUsd);
//
//            // Assuming orderHasCourse.getItemPrice() is in LKR
//            amountInUsd = orderHasCourse.getItemPrice() * exchangeRateToUsd;
//        } else {
//            amountInUsd = orderHasCourse.getItemPrice();
//            System.out.println("//////////////// no exchangeRateToUsd " + amountInUsd);
//        }
//        if (instructorCourseRevenue == null) {
//            instructorCourseRevenue = new InstructorCourseRevenue();
//            instructorCourseRevenue.setRevenueForMonth(currentYearMonth.getMonthValue());
//            instructorCourseRevenue.setRevenueForYear(currentYearMonth.getYear());
//            instructorCourseRevenue.setInstructorProfile(orderHasCourse.getCourse().getInstructorId());
//            totalGrossRevenue = amountInUsd;
//        } else {
//            totalGrossRevenue = instructorCourseRevenue.getGrossRevenue() + amountInUsd;
//        }
//
//        instructorCourseRevenue.setGrossRevenue(totalGrossRevenue);
//
//
//        double paymentProcessingFees = 0.0;
//        double vatValue = 0.0;
//        double netRevenue = 0.0;
//        double athenosFee = 0.0;
//
//        Vat vat = vatRepository.getVatBycountry(country);
//        if (vat != null) {
//            vatValue = amountInUsd * (vat.getVat() / 100);
//        }
//        netRevenue = totalGrossRevenue - paymentProcessingFees - vatValue;
//        RevenueSplitType revenueSplitType;
//        RevenueSplit revenueSplit;
//        if (orderHasCourse.getCoursePurchaseType() == null || orderHasCourse.getCoursePurchaseType().getId() == 1) {
//            revenueSplitType = revenueSplitTypeRepository.getRevenueSplitTypeById(1);
//            if (revenueSplitType == null)
//                throw new ErrorException("Revenue split type not found", VarList.RSP_NO_DATA_FOUND);
//            revenueSplit = revenueSplitRepository.getRevenueSplitByRevenueSplitType(revenueSplitType);
//            if (revenueSplit == null)
//                throw new ErrorException("Revenue split not found", VarList.RSP_NO_DATA_FOUND);
//            athenosFee = netRevenue * (revenueSplit.getAethenosRevenue() / 100);
//        } else if (orderHasCourse.getCoursePurchaseType().getId() == 3 || orderHasCourse.getCoursePurchaseType().getId() == 4) {
//            revenueSplitType = revenueSplitTypeRepository.getRevenueSplitTypeById(2);
//            if (revenueSplitType == null)
//                throw new ErrorException("Revenue split type not found", VarList.RSP_NO_DATA_FOUND);
//            revenueSplit = revenueSplitRepository.getRevenueSplitByRevenueSplitType(revenueSplitType);
//            if (revenueSplit == null)
//                throw new ErrorException("Revenue split not found", VarList.RSP_NO_DATA_FOUND);
//            athenosFee = netRevenue * (revenueSplit.getAethenosRevenue() / 100);
//        }
//
//        netRevenue = netRevenue - athenosFee;
//
//        instructorCourseRevenue.setNetRevenue(netRevenue);
//        instructorCourseRevenue.setRevenueCalculateDate(LocalDate.now());
//        instructorCourseRevenueRepository.save(instructorCourseRevenue);

    }

    private double calculateProcesingFeeForCourse(double totalProcessingFee, double totalSellingPrice, double itemPrice) {
        return (totalProcessingFee / totalSellingPrice) * itemPrice;
    }

    private Map<String, Double> getExchangeRates(String currencyCode) {
        try {
            String url = API_URL.replace("{currencyCode}", currencyCode.toLowerCase());
            Map<String, Map<String, Double>> response = restTemplate.getForObject(url, Map.class);
            return response != null ? response.get(currencyCode.toLowerCase()) : new HashMap<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    private static LocalDate convertToLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        Instant instant = date.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @Override
    public GetCoursesDataResponse getPurchasedCourseDetailsByItemCode(String itemCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (itemCode.isEmpty() || itemCode == null) {
                    throw new ErrorException("Item code not found", VarList.RSP_NO_DATA_FOUND);
                } else {
                    OrderHasCourse orderHasCourse = orderHasCourseRepository.getOrderHasCourseByItemCode(itemCode);
                    if (orderHasCourse == null) {
                        throw new ErrorException("Course not found related to item code", VarList.RSP_NO_DATA_FOUND);
                    } else {
                        if (profile == orderHasCourse.getOrder().getGeneralUserProfile()) {
                            if (orderHasCourse.getIsDelete() != null && orderHasCourse.getIsDelete() == 1)
                                throw new ErrorException("This Course has already been refunded", VarList.RSP_NO_DATA_FOUND);

                            CourseLandingPage courseLandingPage = courseLandingPageRepository.getCourseLandingPageByCourse(orderHasCourse.getCourse());
                            if (courseLandingPage != null) {
                                Course course = orderHasCourse.getCourse();

                                GetCoursesDataResponse getCoursesDataResponse = new GetCoursesDataResponse();
                                getCoursesDataResponse.setItem_code(orderHasCourse.getItemCode());
                                getCoursesDataResponse.setProgressValue(orderHasCourse.getProgress());
                                getCoursesDataResponse.setCourse_code(course.getCode());
                                getCoursesDataResponse.setTotalVideoLength(course.getCourseLength());
                                getCoursesDataResponse.setCreated_date(course.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter));
                                getCoursesDataResponse.setId(course.getId());
                                getCoursesDataResponse.setImg(course.getImg());
                                getCoursesDataResponse.setDuration(Double.toString(course.getCourseLength()));
                                getCoursesDataResponse.setCourse_outline("OutLine is not added to the course");
                                getCoursesDataResponse.setLevel((courseLandingPage != null) ? courseLandingPage.getCourseLevel().getName() : null);
                                getCoursesDataResponse.setTitle(course.getCourseTitle());
                                getCoursesDataResponse.setIsPaid(course.getIsPaid() == 2);
                                List<Review> reviews = reviewRepository.getReviewsByCourse(orderHasCourse.getCourse());
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
                                int studentCount = 0;

                                List<OrderHasCourse> orderHasCourses = orderHasCourseRepository.getOrderHasCoursesByCourse(course);
                                Map<String, Integer> courseStudentCounts = new HashMap<>();
                                courseStudentCounts.put("1afabb42-d9d5-44a1-abf1-9bc4917e5e44", orderHasCourses.size() + 11039);
                                courseStudentCounts.put("90c1e353-358d-46fb-b6c4-8c57007245ea", orderHasCourses.size() + 574);
                                courseStudentCounts.put("e819dea8-cd7f-4eb4-b1b4-e8db6fc63c87", orderHasCourses.size() + 551);
                                courseStudentCounts.put("bf2f01d3-9ef7-4c91-973d-2debdd2e5aaa", orderHasCourses.size() + 548);
                                courseStudentCounts.put("dc4ffca5-41ae-4b35-a778-d530a4a68aa6", orderHasCourses.size() + 537);
                                courseStudentCounts.put("51bd9e0d-49b3-4004-8413-81f5fed53dd2", orderHasCourses.size() + 565);
                                courseStudentCounts.put("9d93acf7-5543-45fb-9f18-c3e57b5fbe69", orderHasCourses.size() + 11864);
                                courseStudentCounts.put("0769d057-797d-475c-84ba-c11fd6651c04", orderHasCourses.size() + 11755);

                                studentCount = courseStudentCounts.getOrDefault(course.getCode(), orderHasCourses.size());

                                getCoursesDataResponse.setStudent(studentCount);
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

                                                ReadCurriculumItem readCurriculumItem = readCurriculumItemRepository.getReadCurriculumItemByOrderHasCourseAndSectionCurriculumItem(orderHasCourse, item);

                                                if (readCurriculumItem != null) {
                                                    getSectionCurriculumItemResponse.setRead(true);
                                                    completedItemCount++;
                                                }

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
                                                        getCurriculumItemFilesResponse.setVideoLength(curriculumItemFile.getVideoLength() == null ? 0 : curriculumItemFile.getVideoLength());
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
                                getCoursesDataResponse.setEnrolled_count(studentCount);
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

                                return getCoursesDataResponse;

                            } else {
                                throw new ErrorException("Course landing page not found related to course", VarList.RSP_NO_DATA_FOUND);
                            }


                        } else {
                            throw new ErrorException("Order not matching with User", VarList.RSP_NO_DATA_FOUND);
                        }
                    }
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse submitReview(SubmitReviewRequest submitReviewRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {

                OrderHasCourse orderHasCourse = orderHasCourseRepository.getOrderHasCourseByItemCode(submitReviewRequest.getItem_code());
                if (orderHasCourse != null) {
                    Review review = reviewRepository.getReviewsByOrderHasCourse(orderHasCourse);
                    if (review == null) {
                        review = new Review();
                        successResponse.setMessage("Review added successfully");
                    } else {
                        successResponse.setMessage("Review updated successfully");
                    }
                    review.setCourse(orderHasCourse.getCourse());
                    review.setReviewCode(UUID.randomUUID().toString());
                    review.setDate(new Date());
                    review.setComment(submitReviewRequest.getComment());
                    review.setOrderHasCourse(orderHasCourse);
                    review.setGeneralUserProfile(profile);
                    review.setRating(submitReviewRequest.getRating());
                    reviewRepository.save(review);

                    try {
                        String noficationCode = UUID.randomUUID().toString();
                        Properties properties;

                        EmailSender emailSender = new EmailSender();

                        Notification notification = new Notification();
                        notification.setNotificationCode(noficationCode);
                        notification.setNotification("A new review has been submitted for the \"" + orderHasCourse.getCourse().getCourseTitle() + "\" course.");
                        notification.setNotificationTime(new Date());
                        notification.setGeneralUserProfile(orderHasCourse.getCourse().getInstructorId().getGeneralUserProfile());
                        notification.setRead(false);
                        notificationRepository.save(notification);

                        properties = EmailConfig.getEmailProperties(orderHasCourse.getCourse().getInstructorId().getGeneralUserProfile().getFirstName() + " " + orderHasCourse.getCourse().getInstructorId().getGeneralUserProfile().getLastName(), "New Course Review Submitted.");
                        properties.put("courseTitle", orderHasCourse.getCourse().getCourseTitle());
                        properties.put("studentName", profile.getFirstName() + " " + profile.getLastName());
                        properties.put("ratingCount", review.getRating());
                        properties.put("review", review.getComment());
                        emailSender.sendEmail("NewCourseReviewSubmittedMessage", orderHasCourse.getCourse().getInstructorId().getGeneralUserProfile().getEmail(), (String) properties.getProperty("from"), (String) properties.get("subject"), properties);

                    } catch (MessagingException e) {
                        throw new ErrorException(e.getMessage(), VarList.RSP_NO_DATA_FOUND);
                    }

                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;

                } else {
                    throw new ErrorException("Invalid item code", VarList.RSP_NO_DATA_FOUND);
                }

            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public List<ReviewResponse> getReviewsByItemCode(String itemCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {

                OrderHasCourse orderHasCourse = orderHasCourseRepository.getOrderHasCourseByItemCode(itemCode);
                if (orderHasCourse != null) {

                    List<Review> reviewList = reviewRepository.getReviewsByCourse(orderHasCourse.getCourse());
                    List<ReviewResponse> reviewResponseList = new ArrayList<>();

                    if (reviewList.size() > 0) {

                        for (Review review : reviewList) {
                            ReviewResponse reviewResponse = new ReviewResponse();
                            reviewResponse.setReviewCode(review.getReviewCode());
                            reviewResponse.setUserCode(review.getGeneralUserProfile().getUserCode());
                            reviewResponse.setFullName(review.getGeneralUserProfile().getFirstName() + " " + review.getGeneralUserProfile().getLastName());
                            reviewResponse.setUserProfile(review.getGeneralUserProfile().getProfileImg());
                            reviewResponse.setComment(review.getComment() == null ? "" : review.getComment());
                            reviewResponse.setRating(review.getRating());
                            reviewResponse.setDate(review.getDate());

                            List<RepliesToReviewResponse> replies = new ArrayList<>();
                            List<ReplyToReview> repliesToReview = replyToReviewRepository.getReplyToReviewByReview(review);
                            RepliesToReviewResponse repliesToReviewResponse;
                            for (ReplyToReview replyToReview : repliesToReview) {
                                repliesToReviewResponse = new RepliesToReviewResponse();
                                repliesToReviewResponse.setUserCode(replyToReview.getGeneralUserProfile().getUserCode());
                                repliesToReviewResponse.setProfileImg(review.getGeneralUserProfile().getProfileImg() == null ? "" : review.getGeneralUserProfile().getProfileImg());
                                repliesToReviewResponse.setComment(replyToReview.getComment());
                                repliesToReviewResponse.setName(replyToReview.getGeneralUserProfile().getFirstName() + " " + replyToReview.getGeneralUserProfile().getLastName());
                                repliesToReviewResponse.setUserType(replyToReview.getGeneralUserProfile().getGupType().getId());
                                repliesToReviewResponse.setCreatedDate(replyToReview.getDate());
                                replies.add(repliesToReviewResponse);
                            }
                            reviewResponse.setReplies(replies);
                            reviewResponseList.add(reviewResponse);
                        }
                    }
                    return reviewResponseList;

                } else {
                    throw new ErrorException("Invalid item code", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse addRespondToReview(AddRespondToReviewRequest addRespondToReviewRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (addRespondToReviewRequest.getReviewCode() == null || addRespondToReviewRequest.getReviewCode().isEmpty()) {
                    throw new ErrorException("Invalid request, please add a review code", VarList.RSP_NO_DATA_FOUND);
                }
                if (addRespondToReviewRequest.getComment() == null || addRespondToReviewRequest.getComment().isEmpty()) {
                    throw new ErrorException("Invalid request, please add a comment", VarList.RSP_NO_DATA_FOUND);
                }
                Review review = reviewRepository.getReviewByReviewCode(addRespondToReviewRequest.getReviewCode());
                if (review != null) {
                    ReplyToReview replyToReview = new ReplyToReview();
                    replyToReview.setReview(review);
                    replyToReview.setComment(addRespondToReviewRequest.getComment());
                    replyToReview.setDate(new Date());
                    replyToReview.setGeneralUserProfile(profile);
                    replyToReviewRepository.save(replyToReview);
                    successResponse.setMessage("Comment added successfully");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;

                } else {
                    throw new ErrorException("Invalid review code", VarList.RSP_NO_DATA_FOUND);
                }

            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public List<GetCourseWithReviewsResponse> getCourseWithReviewsByCourseCode(String courseCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    List<GetCourseWithReviewsResponse> getCourseWithReviewsResponses = new ArrayList<>();
                    if (courseCode.equals("all")) {
                        InstructorProfile instructorProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(profile);
                        if (instructorProfile == null)
                            throw new ErrorException("Your instructor profile is not found", VarList.RSP_NO_DATA_FOUND);
                        List<Course> courses = courseRepository.getCourseByInstructorId(instructorProfile);
                        for (Course course : courses) {
                            getCourseWithReviewsResponses.add(GenerateCourseWithReviewsResponse(course));
                        }

                    } else {
                        Course course = courseRepository.getCourseByCode(courseCode);
                        if (course == null)
                            throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
                        getCourseWithReviewsResponses.add(GenerateCourseWithReviewsResponse(course));
                    }
                    return getCourseWithReviewsResponses;
                } else {
                    throw new ErrorException("You are not an instructor for this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    private GetCourseWithReviewsResponse GenerateCourseWithReviewsResponse(Course course) {
        GetCourseWithReviewsResponse getCourseWithReviewsResponse = new GetCourseWithReviewsResponse();
        getCourseWithReviewsResponse.setCourseTitle(course.getCourseTitle());
        getCourseWithReviewsResponse.setCourseImg(course.getImg());
        double tot_rating = 0;
        int rating_count = 0;
        List<ReviewResponse> reviewResponseList = new ArrayList<>();

        List<Review> reviewList = reviewRepository.getReviewsByCourse(course);
        for (Review review : reviewList) {
            tot_rating = tot_rating + review.getRating();
            rating_count++;
            ReviewResponse reviewResponse = new ReviewResponse();
            reviewResponse.setReviewCode(review.getReviewCode());
            reviewResponse.setUserCode(review.getGeneralUserProfile().getUserCode());
            reviewResponse.setFullName(review.getGeneralUserProfile().getFirstName() + " " + review.getGeneralUserProfile().getLastName());
            reviewResponse.setUserProfile(review.getGeneralUserProfile().getProfileImg());
            reviewResponse.setComment(review.getComment());
            reviewResponse.setRating(review.getRating());
            reviewResponse.setDate(review.getDate());

            List<RepliesToReviewResponse> replies = new ArrayList<>();
            List<ReplyToReview> repliesToReview = replyToReviewRepository.getReplyToReviewByReview(review);
            RepliesToReviewResponse repliesToReviewResponse;
            for (ReplyToReview replyToReview : repliesToReview) {
                repliesToReviewResponse = new RepliesToReviewResponse();
                repliesToReviewResponse.setUserCode(replyToReview.getGeneralUserProfile().getUserCode());
                repliesToReviewResponse.setProfileImg(review.getGeneralUserProfile().getProfileImg() == null ? "" : review.getGeneralUserProfile().getProfileImg());
                repliesToReviewResponse.setComment(replyToReview.getComment());
                repliesToReviewResponse.setName(replyToReview.getGeneralUserProfile().getFirstName() + " " + replyToReview.getGeneralUserProfile().getLastName());
                repliesToReviewResponse.setUserType(replyToReview.getGeneralUserProfile().getGupType().getId());
                repliesToReviewResponse.setCreatedDate(replyToReview.getDate());
                replies.add(repliesToReviewResponse);
            }
            reviewResponse.setReplies(replies);
            reviewResponseList.add(reviewResponse);
        }
        getCourseWithReviewsResponse.setRating((rating_count > 0) ? (tot_rating / rating_count) : 0);
        getCourseWithReviewsResponse.setReviewResponses(reviewResponseList);
        return getCourseWithReviewsResponse;
    }

    @Override
    public SuccessResponse updateOrderHasCourseProgress(UpdateOrderHasCourseProgressRequest updateOrderHasCourseProgressRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
            if (profile != null && profile.getIsActive() == 1) {

                final String ItemCode = updateOrderHasCourseProgressRequest.getItemCode();
                final String sectionName = updateOrderHasCourseProgressRequest.getSectionName();

                if (ItemCode == null || ItemCode.isEmpty() || sectionName == null || sectionName.isEmpty()) {
                    throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                }

                OrderHasCourse orderHasCourse = orderHasCourseRepository.getOrderHasCourseByItemCode(ItemCode);
                if (orderHasCourse != null) {
                    List<CourseSection> courseSections = courseSectionRepository.getCourseSectionByCourse(orderHasCourse.getCourse());
                    boolean sectionPlaced = false;
                    String completedSections = orderHasCourse.getCompletedSections();
                    StringBuilder newCompletedSections = new StringBuilder(completedSections != null ? completedSections : "");
                    for (int i = 0; i < courseSections.size(); i++) {
                        CourseSection courseSection = courseSections.get(i);
                        if (courseSection.getSectionName().equals(sectionName)) {
                            sectionPlaced = true;

                            if (!newCompletedSections.toString().equals("")) {
                                String[] completedSectionsArray = newCompletedSections.toString().split(", ");
                                boolean isHere = false;
                                for (String sectionIndex : completedSectionsArray) {
                                    if (sectionIndex.equals(String.valueOf(courseSection.getId()))) {
                                        isHere = true;
                                        break;
                                    }
                                }
                                if (!isHere) {
                                    newCompletedSections.append(", ").append(courseSection.getId());
                                }
                            } else {
                                newCompletedSections.append(courseSection.getId());
                            }
                            break;
                        }
                    }

                    if (!sectionPlaced) {
                        throw new ErrorException("Invalid section name", VarList.RSP_NO_DATA_FOUND);
                    }

                    double progress = 0;

                    if (newCompletedSections.length() > 0) {
                        String[] completedSectionsArray = newCompletedSections.toString().split(", ");
                        int countCurriculumItem = 0;
                        for (int i = 0; i < completedSectionsArray.length; i++) {
                            CourseSection courseSection = courseSectionRepository.getCourseSectionById(Integer.parseInt(completedSectionsArray[i]));
                            countCurriculumItem += sectionCurriculumItemRepository.getSectionCurriculumItemsByCourseSection(courseSection).size();
                        }
                        int countAllCurriculumItem = 0;
                        for (CourseSection courseSection : courseSections) {
                            countAllCurriculumItem += sectionCurriculumItemRepository.getSectionCurriculumItemsByCourseSection(courseSection).size();
                        }
                        progress = (countCurriculumItem * 100) / (double) countAllCurriculumItem;
                    }

                    orderHasCourse.setProgress(progress);
                    orderHasCourse.setCompletedSections(newCompletedSections.toString());
                    orderHasCourseRepository.save(orderHasCourse);

                    CourseSection courseSection = courseSectionRepository.getCourseSectionByCourseAndSectionName(orderHasCourse.getCourse(), sectionName);

                    List<SectionCurriculumItem> sectionCurriculumItems = sectionCurriculumItemRepository.getSectionCurriculumItemsByCourseSection(courseSection);

                    for (SectionCurriculumItem sectionCurriculumItem : sectionCurriculumItems) {

                        ReadCurriculumItem readCurriculumItem = readCurriculumItemRepository.getReadCurriculumItemByOrderHasCourseAndSectionCurriculumItem(orderHasCourse, sectionCurriculumItem);

                        if (readCurriculumItem == null) {
                            readCurriculumItem = new ReadCurriculumItem();
                            readCurriculumItem.setOrderHasCourse(orderHasCourse);
                            readCurriculumItem.setSectionCurriculumItem(sectionCurriculumItem);
                            readCurriculumItemRepository.save(readCurriculumItem);
                        }
                    }


                    SuccessResponse successResponse = new SuccessResponse();
                    successResponse.setMessage("Progress updated successfully");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;
                } else {
                    throw new ErrorException("Invalid item code", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not authenticated", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse addReadCurriculumItem(AddReadCurriculumItemRequest addReadCurriculumItemRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
            if (profile != null && profile.getIsActive() == 1) {
                final String itemCode = addReadCurriculumItemRequest.getItemCode();
                final Integer curriculumItemId = addReadCurriculumItemRequest.getCurriculumItemId();

                Double totalVideoLength = 0.0;

                if (itemCode == null || itemCode.isEmpty() || curriculumItemId == null) {
                    throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                }
                SectionCurriculumItem sectionCurriculumItem = sectionCurriculumItemRepository.getSectionCurriculumItemById(curriculumItemId);
                if (sectionCurriculumItem == null) {
                    throw new ErrorException("section curriculum item not found", VarList.RSP_NO_DATA_FOUND);
                }
                OrderHasCourse orderHasCourse = orderHasCourseRepository.getOrderHasCourseByItemCode(itemCode);
                if (orderHasCourse == null) {
                    throw new ErrorException("Invalid item code", VarList.RSP_NO_DATA_FOUND);
                }

                if (!orderHasCourse.getOrder().getGeneralUserProfile().equals(profile)) {
                    throw new ErrorException("You cannot access this process because this course is not yours", VarList.RSP_NO_DATA_FOUND);
                }

                if (!orderHasCourse.getCourse().equals(sectionCurriculumItem.getCourseSection().getCourse())) {
                    throw new ErrorException("This is an invalid request due to an invalid curriculum item", VarList.RSP_NO_DATA_FOUND);
                }

                ReadCurriculumItem readCurriculumItem = readCurriculumItemRepository.getReadCurriculumItemByOrderHasCourseAndSectionCurriculumItem(orderHasCourse, sectionCurriculumItem);

                if (readCurriculumItem == null) {
                    readCurriculumItem = new ReadCurriculumItem();

                    readCurriculumItem.setSectionCurriculumItem(sectionCurriculumItem);
                    readCurriculumItem.setOrderHasCourse(orderHasCourse);
                    readCurriculumItemRepository.save(readCurriculumItem);
                }

                CourseSection courseSection = sectionCurriculumItem.getCourseSection();
                List<SectionCurriculumItem> sectionCurriculumItems = sectionCurriculumItemRepository.getSectionCurriculumItemsByCourseSection(courseSection);
                boolean isComplete = true;
                for (SectionCurriculumItem sectionCurriculumItemObj : sectionCurriculumItems) {
                    if (sectionCurriculumItemObj.getIsDelete() == null || sectionCurriculumItemObj.getIsDelete() == 0) {
                        readCurriculumItem = readCurriculumItemRepository.getReadCurriculumItemByOrderHasCourseAndSectionCurriculumItem(orderHasCourse, sectionCurriculumItemObj);

                        if (readCurriculumItem == null) {
                            isComplete = false;
                        }
                    }
                }

                String completedSections = orderHasCourse.getCompletedSections();
                StringBuilder newCompletedSections = new StringBuilder(completedSections != null ? completedSections : "");
                if (isComplete) {
                    if (!newCompletedSections.toString().equals("")) {
                        String[] completedSectionsArray = newCompletedSections.toString().split(", ");
                        boolean isHere = false;
                        for (String sectionIndex : completedSectionsArray) {
                            if (sectionIndex.equals(String.valueOf(courseSection.getId()))) {
                                isHere = true;
                                break;
                            }
                        }
                        if (!isHere) {
                            newCompletedSections.append(", ").append(courseSection.getId());
                        }
                    } else {
                        newCompletedSections.append(courseSection.getId());
                    }
                }

                int countCompleteCurriculumItem = 0;
                int countAllCurriculumItem = 0;
                if (!newCompletedSections.toString().equals("")) {
                    String[] completedSectionsArray = newCompletedSections.toString().split(", ");
                    for (String sectionIndex : completedSectionsArray) {
                        courseSection = courseSectionRepository.getCourseSectionById(Integer.parseInt(sectionIndex));
                        if (courseSection.getIsDelete() == null || courseSection.getIsDelete() == 0)
                            countCompleteCurriculumItem += sectionCurriculumItemRepository.getSectionCurriculumItemsByCourseSection(courseSection).size();
                    }
                }

                List<CourseSection> courseSections = courseSectionRepository.getCourseSectionByCourse(orderHasCourse.getCourse());
                CurriculumItemFileType curriculumItemFileType = curriculumItemFileTypeRepository.getCurriculumItemFileTypeById(3);
                if (curriculumItemFileType == null) {
                    throw new ErrorException("Invalid curriculum item file type id", VarList.RSP_NO_DATA_FOUND);
                }
                double countAllVideoCurriculumItem = 0;
                for (CourseSection courseSectionObj : courseSections) {
                    if (courseSectionObj.getIsDelete() == null || courseSectionObj.getIsDelete() == 0) {
                        sectionCurriculumItems = sectionCurriculumItemRepository.getSectionCurriculumItemsByCourseSection(courseSectionObj);
                        for (SectionCurriculumItem sectionCurriculumItemObj : sectionCurriculumItems) {
                            if (sectionCurriculumItemObj.getIsDelete() == null || sectionCurriculumItemObj.getIsDelete() == 0) {
                                CurriculumItemFile curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(sectionCurriculumItemObj, curriculumItemFileType);
                                if (curriculumItemFile != null) {
                                    if (curriculumItemFile.getVideoLength() != null) {
                                        totalVideoLength += curriculumItemFile.getVideoLength();
                                        countAllVideoCurriculumItem++;
                                    }
                                }
                                countAllCurriculumItem++;
                            }
                        }
                    }
                }

                double progress = 0;

                List<ReadCurriculumItem> readCurriculumItems = readCurriculumItemRepository.getReadCurriculumItemByOrderHasCourse(orderHasCourse);
                if (countAllVideoCurriculumItem > 0) {
                    double readVideoCurriculumItemCount = 0;

                    for (ReadCurriculumItem readCurriculumItemObj : readCurriculumItems) {
                        CurriculumItemFile curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileBySectionCurriculumItemAndCurriculumItemFileTypes(readCurriculumItemObj.getSectionCurriculumItem(), curriculumItemFileType);
                        if (curriculumItemFile != null)
                            readVideoCurriculumItemCount++;

                    }
                    progress = (readVideoCurriculumItemCount * 100) / countAllVideoCurriculumItem;

                }


                if (!newCompletedSections.toString().equals("")) {
                    if (readCurriculumItems.size() > 0) {
                        orderHasCourse.setCompletedSections(newCompletedSections.toString());
                    }
                }
                orderHasCourse.setProgress(progress);

                if (progress == 100 && (orderHasCourse.getIsComplete() == null || orderHasCourse.getIsComplete().toString().isEmpty() || orderHasCourse.getIsComplete() != 1)) {
                    try {

                        PdfGenerater pdfGenerater = new PdfGenerater();
                        EmailSender emailSender = new EmailSender();

                        String genCertificateCode = UUID.randomUUID().toString();

                        int hours = (int) (totalVideoLength / 3600);  // Calculate total hours
                        int minutes = (int) ((totalVideoLength % 3600) / 60); // Calculate remaining minutes

                        String LengthView = hours == 0 ? minutes + "mins" : hours + "hr " + minutes + "mins";
                        System.out.println("test Length/////////" + totalVideoLength);

                        boolean logo = false;

                        if (orderHasCourse.getCourse().getCode().equals("9d93acf7-5543-45fb-9f18-c3e57b5fbe69")) {
                            logo = true;
                        }

                        Context context = pdfGenerater.generateContext(profile.getFirstName() + " " + profile.getLastName(), orderHasCourse.getCourse().getCourseTitle(), orderHasCourse.getCourse().getInstructorId().getGeneralUserProfile().getFirstName() + " " +
                                orderHasCourse.getCourse().getInstructorId().getGeneralUserProfile().getLastName(), new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(updatedFormatter), LengthView, genCertificateCode, logo);

                        String finalHtml = springTemplateEngine.process("ViewCertificate", context);
                        byte[] pdfResource = pdfGenerater.htmlToPdf(finalHtml);

                        // Save the PDF to a file on the server
                        String fileName = genCertificateCode + ".pdf";

                        File filePath = new File(Config.UPLOAD_URL + Config.CERTIFICATE_DOCUMENTS_UPLOAD_URL);
                        if (!filePath.exists()) {
                            filePath.mkdirs();
                        }

                        File file = new File(filePath, fileName);

                        try (FileOutputStream fos = new FileOutputStream(file)) {
                            fos.write(pdfResource);
                            orderHasCourse.setCertificate(Config.CERTIFICATE_DOCUMENTS_UPLOAD_URL + fileName);
                            orderHasCourse.setIsComplete((byte) 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

//                        String base64Pdf = pdfGenerater.encodePdfToBase64(pdfResource);


                        Properties properties = EmailConfig.getEmailProperties(profile.getFirstName() + " " + profile.getLastName(), orderHasCourse.getCourse().getCourseTitle(), orderHasCourse.getCourse().getInstructorId().getGeneralUserProfile().getFirstName() + " " +
                                        orderHasCourse.getCourse().getInstructorId().getGeneralUserProfile().getLastName(), new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter),
                                totalVideoLength, finalHtml, "Certificate of completion");

                        emailSender.sendEmail("StudentCourseCompleteMessage",
                                profile.getEmail(),
                                (String) properties.get("from"),
                                (String) properties.get("subject"),
                                properties,
                                pdfResource
                        );
                        List<String> courses = null;
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                orderHasCourseRepository.save(orderHasCourse);
                SuccessResponse successResponse = new SuccessResponse();
                successResponse.setMessage("Read curriculum added successfully");
                successResponse.setVariable(VarList.RSP_SUCCESS);
                return successResponse;

            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not authenticated", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public GetReadCurriculumItemResponse getReadCurriculumItem(AddReadCurriculumItemRequest addReadCurriculumItemRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
            if (profile != null && profile.getIsActive() == 1) {
                final String itemCode = addReadCurriculumItemRequest.getItemCode();
                final Integer curriculumItemId = addReadCurriculumItemRequest.getCurriculumItemId();

                if (itemCode == null || itemCode.isEmpty() || curriculumItemId == null) {
                    throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                }
                SectionCurriculumItem sectionCurriculumItem = sectionCurriculumItemRepository.getSectionCurriculumItemById(curriculumItemId);
                if (sectionCurriculumItem == null) {
                    throw new ErrorException("Section curriculum item not found", VarList.RSP_NO_DATA_FOUND);
                }
                OrderHasCourse orderHasCourse = orderHasCourseRepository.getOrderHasCourseByItemCode(itemCode);
                if (orderHasCourse == null) {
                    throw new ErrorException("Invalid itemCode", VarList.RSP_NO_DATA_FOUND);
                }

                if (orderHasCourse.getCourse().getCode() != sectionCurriculumItem.getCourseSection().getCourse().getCode()) {
                    throw new ErrorException("This is an invalid request due to an invalid curriculum item", VarList.RSP_NO_DATA_FOUND);
                }

                ReadCurriculumItem readCurriculumItem = readCurriculumItemRepository.getReadCurriculumItemByOrderHasCourseAndSectionCurriculumItem(orderHasCourse, sectionCurriculumItem);

                GetReadCurriculumItemResponse getReadCurriculumItemResponse = new GetReadCurriculumItemResponse();

                if (readCurriculumItem != null) {
                    getReadCurriculumItemResponse.setRead(true);
                }
                return getReadCurriculumItemResponse;

            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not authenticated", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public GetTransactionDetailsResponse getTransactionDetailsByTransActionCode(String transActionCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
            if (profile != null && profile.getIsActive() == 1) {
                Transaction transaction = transactionRepository.getTransactionByTransactionCode(transActionCode);
                if (transaction == null || !transaction.getOrder().getGeneralUserProfile().getUserCode().equals(profile.getUserCode())) {
                    throw new ErrorException("Invalid transAction code", VarList.RSP_NO_DATA_FOUND);
                }
                GetTransactionDetailsResponse getTransactionDetailsResponse = new GetTransactionDetailsResponse();
                getTransactionDetailsResponse.setTransactionDate(transaction.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter));
                getTransactionDetailsResponse.setUserName(transaction.getOrder().getGeneralUserProfile().getFirstName() + " " + transaction.getOrder().getGeneralUserProfile().getLastName());
                getTransactionDetailsResponse.setVat(decimalFormat.format(transaction.getVatAmount()));
                getTransactionDetailsResponse.setVatPercentage(decimalFormat.format(transaction.getVatPercentage()) + "%");
                getTransactionDetailsResponse.setAmount(decimalFormat.format(transaction.getAmount()));
                getTransactionDetailsResponse.setTransActionCode(transaction.getTransactionCode());

                OrderDetailsResponse orderDetailsResponse = new OrderDetailsResponse();
                orderDetailsResponse.setCurrency(transaction.getOrder().getCurrency());
                orderDetailsResponse.setTotal(decimalFormat.format(transaction.getOrder().getTotal()));
                orderDetailsResponse.setDisCount(transaction.getOrder().getDiscount());
                orderDetailsResponse.setBuyDate(transaction.getOrder().getBuyDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter));
                orderDetailsResponse.setPaymentMethod(transaction.getOrder().getPaymentMethod().getMethod());

                List<OrderHasCourseResponse> orderHasCourseResponses = new ArrayList<>();
                List<OrderHasCourse> orderHasCourses = orderHasCourseRepository.getOrderHasCoursesByOrder(transaction.getOrder());
                if (orderHasCourses.size() == 0) {
                    throw new ErrorException("There are no courses for the given order", VarList.RSP_NO_DATA_FOUND);
                }
                for (OrderHasCourse orderHasCourse : orderHasCourses) {
                    OrderHasCourseResponse orderHasCourseResponse = new OrderHasCourseResponse();
                    orderHasCourseResponse.setCourseCode(orderHasCourse.getCourse().getCode());
                    orderHasCourseResponse.setCourseTitle(orderHasCourse.getCourse().getCourseTitle());
                    orderHasCourseResponse.setCurrency(orderHasCourse.getCurrrency());
                    orderHasCourseResponse.setItemCode(orderHasCourse.getItemCode());
                    orderHasCourseResponse.setListPrice(decimalFormat.format(orderHasCourse.getListPrice()));
                    orderHasCourseResponse.setItemPrice(decimalFormat.format(orderHasCourse.getItemPrice()));
                    orderHasCourseResponses.add(orderHasCourseResponse);
                }
                orderDetailsResponse.setOrderHasItems(orderHasCourseResponses);

                getTransactionDetailsResponse.setOrderDetails(orderDetailsResponse);
                return getTransactionDetailsResponse;
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not authenticated", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public List<GetPurchaseHistoryResponse> getPurchaseHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
            if (profile != null && profile.getIsActive() == 1) {

                List<Order> orders = orderRepository.getOrdersByGeneralUserProfile(profile);
                List<GetPurchaseHistoryResponse> getPurchaseHistoryResponses = new ArrayList<>();
                for (Order order : orders) {
                    Transaction transaction = transactionRepository.getTransactionByOrder(order);
                    if (transaction == null) {
                        throw new ErrorException("Transaction not found", VarList.RSP_NO_DATA_FOUND);
                    }
                    GetPurchaseHistoryResponse getPurchaseHistoryResponse = new GetPurchaseHistoryResponse();
                    getPurchaseHistoryResponse.setTransActionCode(transaction.getTransactionCode());
                    getPurchaseHistoryResponse.setAmount(decimalFormat.format(transaction.getAmount()));
                    getPurchaseHistoryResponse.setCurrency(transaction.getOrder().getCurrency());
                    getPurchaseHistoryResponse.setCreatedDate(transaction.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter));
                    getPurchaseHistoryResponse.setPaymentType(order.getPaymentMethod().getMethod());
                    List<OrderHasCourse> orderHasCourses = orderHasCourseRepository.getOrderHasCoursesByOrder(transaction.getOrder());
                    List<CourseDetailsForPurchaseHistoryResponse> courseDetailsForPurchaseHistoryResponses = new ArrayList<>();
                    for (OrderHasCourse orderHasCourse : orderHasCourses) {
                        CourseDetailsForPurchaseHistoryResponse courseDetailsForPurchaseHistoryResponse = new CourseDetailsForPurchaseHistoryResponse();
                        courseDetailsForPurchaseHistoryResponse.setItemCode(orderHasCourse.getItemCode());
                        courseDetailsForPurchaseHistoryResponse.setCourseCode(orderHasCourse.getCourse().getCode());
                        courseDetailsForPurchaseHistoryResponse.setCourseTitle(orderHasCourse.getCourse().getCourseTitle());
                        courseDetailsForPurchaseHistoryResponses.add(courseDetailsForPurchaseHistoryResponse);
                    }
                    getPurchaseHistoryResponse.setCourseDetails(courseDetailsForPurchaseHistoryResponses);
                    getPurchaseHistoryResponses.add(getPurchaseHistoryResponse);
                }
                return getPurchaseHistoryResponses;

            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not authenticated", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse addRefund(AddRefundRequest addRefundRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
            if (profile != null && profile.getIsActive() == 1) {
                final String itemCode = addRefundRequest.getItemCode();
                final String reason = addRefundRequest.getReason();

                if (itemCode == null || itemCode.isEmpty() || reason == null || reason.isEmpty()) {
                    throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                }

                OrderHasCourse orderHasCourse = orderHasCourseRepository.getOrderHasCourseByItemCode(itemCode);
                if (orderHasCourse == null) {
                    throw new ErrorException("Invalid Item code", VarList.RSP_NO_DATA_FOUND);
                }

                if (orderHasCourse.getCoursePurchaseType().getId() == 2) {
                    throw new ErrorException("You cannot get a refund for a free course", VarList.RSP_NO_DATA_FOUND);
                }
                if (!orderHasCourse.getOrder().getGeneralUserProfile().equals(profile)) {
                    throw new ErrorException("You cannot add this refund because you do not own this order", VarList.RSP_NO_DATA_FOUND);
                }

                Date createdDate = orderHasCourse.getOrder().getBuyDate();
                Date currentDate = new Date();

                long differenceInMillis = currentDate.getTime() - createdDate.getTime();

                long differenceInDays = differenceInMillis / (1000 * 60 * 60 * 24);

                if (differenceInDays > 30) {
                    throw new ErrorException("You cannot add a refund because the date has expired", VarList.RSP_NO_DATA_FOUND);
                }

                Refunds refunds = refundsRepository.getRefundsByOrderHasCourse(orderHasCourse);
                if (refunds != null) {
                    throw new ErrorException("Refund already added", VarList.RSP_NO_DATA_FOUND);
                }

                RefundStatus refundStatus = refundStatusRepository.getRefundStatusById(2);
                if (refundStatus == null)
                    throw new ErrorException("No data found in refundStatus", VarList.RSP_NO_DATA_FOUND);

                refunds = new Refunds();
                refunds.setRefundStatus(refundStatus);
                refunds.setRefundAmount(orderHasCourse.getItemPrice());
                refunds.setOrderHasCourse(orderHasCourse);
                refunds.setOrder(orderHasCourse.getOrder());
                refunds.setGeneralUserProfile(profile);
                refunds.setReason(reason);
                refunds.setRequestDate(new Date());
                refunds.setRefundCode(UUID.randomUUID().toString());
                refundsRepository.save(refunds);

                GupType gupType = gupTypeRepository.getGupTypeById(3);
                List<GeneralUserProfile> adminProfiles = generalUserProfileRepository.getGeneralUserProfileByGupType(gupType);
                Properties properties;
                for (GeneralUserProfile generalUserProfile : adminProfiles) {
                    properties = EmailConfig.getEmailProperties(generalUserProfile.getFirstName() + " " + generalUserProfile.getLastName(), "Refund Request for Course (" + orderHasCourse.getCourse().getCourseTitle() + ") -" + profile.getFirstName() + " " + profile.getLastName());
                    properties.put("courseName", orderHasCourse.getCourse().getCourseTitle());
                    properties.put("studentName", profile.getFirstName() + " " + profile.getLastName());
                    properties.put("adminName", generalUserProfile.getFirstName() + " " + generalUserProfile.getLastName());
                    properties.put("studentEmail", profile.getEmail());
                    properties.put("purchaseDate", orderHasCourse.getOrder().getBuyDate());
                    properties.put("reasonForRefundRequest", reason);
                    try {
                        EmailSender emailSender = new EmailSender();
                        emailSender.sendEmail("RefundRequestForCourseMessage", generalUserProfile.getEmail(), (String) properties.getProperty("from"), (String) properties.get("subject"), properties);

                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }

                successResponse.setMessage("Refund added successfully");
                successResponse.setVariable(VarList.RSP_SUCCESS);

                return successResponse;

            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not authenticated", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse updateRefundStatus(UpdateRefundStatusRequest updateRefundStatusRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
            if (profile != null && profile.getIsActive() == 1) {
                if (profile.getGupType().getId() != 3) {
                    throw new ErrorException("You are not an admin", VarList.RSP_NO_DATA_FOUND);
                }

                final String refundCode = updateRefundStatusRequest.getRefundCode();
                final Integer refundStatusId = updateRefundStatusRequest.getRefundStatusId();

                if (refundCode == null || refundCode.isEmpty() || refundStatusId == null || refundStatusId == 0)
                    throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);

                Refunds refunds = refundsRepository.getRefundsByRefundCode(refundCode);
                if (refunds == null)
                    throw new ErrorException("Invalid refund code", VarList.RSP_NO_DATA_FOUND);

                RefundStatus refundStatus = null;
                Properties properties;
                EmailSender emailSender;

                if (refundStatusId == 3) {
                    refundStatus = refundStatusRepository.getRefundStatusById(3);
                    if (refundStatus == null)
                        throw new ErrorException("Refund status not found", VarList.RSP_NO_DATA_FOUND);

                    String comment = updateRefundStatusRequest.getComment();

                    if (comment == null || comment.isEmpty())
                        throw new ErrorException("Please add a comment", VarList.RSP_NO_DATA_FOUND);

                    refunds.setRefundStatus(refundStatus);
                    refunds.setComment(comment);

                    refundsRepository.save(refunds);

                    try {
                        properties = EmailConfig.getEmailProperties(refunds.getGeneralUserProfile().getFirstName() + " " + refunds.getGeneralUserProfile().getLastName(), "Refund request rejected for course " + refunds.getOrderHasCourse().getCourse().getCourseTitle());
                        properties.put("courseName", refunds.getOrderHasCourse().getCourse().getCourseTitle());
                        properties.put("studentName", refunds.getGeneralUserProfile().getFirstName() + " " + refunds.getGeneralUserProfile().getLastName());
                        properties.put("reason", refunds.getComment());
                        emailSender = new EmailSender();
                        emailSender.sendEmail("RefundRequestRejectedForCourseMessage", refunds.getGeneralUserProfile().getEmail(), (String) properties.getProperty("from"), (String) properties.get("subject"), properties);

                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }

                } else if (refundStatusId == 4) {
                    refundStatus = refundStatusRepository.getRefundStatusById(4);
                    if (refundStatus == null)
                        throw new ErrorException("Refund status not found", VarList.RSP_NO_DATA_FOUND);

                    refunds.setRefundStatus(refundStatus);
                    refundsRepository.save(refunds);

                    properties = EmailConfig.getEmailProperties(refunds.getGeneralUserProfile().getFirstName() + " " + refunds.getGeneralUserProfile().getLastName(), "Refund Request for Course (" + refunds.getOrderHasCourse().getCourse().getCourseTitle() + ") - Request Approved");
                    properties.put("courseName", refunds.getOrderHasCourse().getCourse().getCourseTitle());
                    properties.put("refundAmount", refunds.getOrderHasCourse().getCurrrency() + " " + decimalFormat.format(refunds.getRefundAmount()));
                    try {
                        emailSender = new EmailSender();
                        emailSender.sendEmail("RefundApproveMessage", refunds.getGeneralUserProfile().getEmail(), (String) properties.getProperty("from"), (String) properties.get("subject"), properties);

                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }

                } else if (refundStatusId == 5) {

                    if (refunds.getRefundStatus().getId() != 4)
                        throw new ErrorException("The administrator has not approved this refund", VarList.RSP_NO_DATA_FOUND);

                    refundStatus = refundStatusRepository.getRefundStatusById(5);
                    if (refundStatus == null)
                        throw new ErrorException("Refund status not found", VarList.RSP_NO_DATA_FOUND);

                    refunds.setRefundStatus(refundStatus);
                    refundsRepository.save(refunds);

                    OrderHasCourse orderHasCourse = refunds.getOrderHasCourse();
                    orderHasCourse.setIsDelete((byte) 1);
                    orderHasCourseRepository.save(orderHasCourse);

                    ApprovedRefunds approvedRefunds = new ApprovedRefunds();
                    approvedRefunds.setRefundStatus(refundStatus);
                    approvedRefunds.setRefundAmount(refunds.getRefundAmount());
                    approvedRefunds.setPurchasedAmount(orderHasCourse.getItemPrice());
                    approvedRefunds.setPurchasedDate(orderHasCourse.getOrder().getBuyDate());
                    approvedRefunds.setCourse(orderHasCourse.getCourse());
                    approvedRefunds.setRefunds(refunds);
                    approvedRefunds.setTransferredDate(new Date());
                    approvedRefunds.setGeneralUserProfile(orderHasCourse.getOrder().getGeneralUserProfile());
                    approvedRefunds.setCurrency(orderHasCourse.getCurrrency());

                    approvedRefundsRepository.save(approvedRefunds);

                    Revenue revenue = revenueRepository.getRevenueByOrderHasCourse(orderHasCourse);
                    if (revenue == null)
                        throw new ErrorException("Revenue could not be found", VarList.RSP_NO_DATA_FOUND);
                    revenue.setRefunded(true);
                    revenueRepository.save(revenue);
                    try {
                        properties = EmailConfig.getEmailProperties(refunds.getGeneralUserProfile().getFirstName() + " " + refunds.getGeneralUserProfile().getLastName(), "Refund Processed for Your Course " + orderHasCourse.getCourse().getCourseTitle());
                        properties.put("courseName", orderHasCourse.getCourse().getCourseTitle());
                        properties.put("studentName", refunds.getGeneralUserProfile().getFirstName() + " " + refunds.getGeneralUserProfile().getLastName());
                        properties.put("refundAmount", refunds.getOrderHasCourse().getCurrrency() + " " + decimalFormat.format(refunds.getRefundAmount()));
                        properties.put("accountType", refunds.getGeneralUserProfile().getGupType().getName());

                        emailSender = new EmailSender();
                        emailSender.sendEmail("EmailToStudentFromAdminMessage", refunds.getGeneralUserProfile().getEmail(), (String) properties.getProperty("from"), (String) properties.get("subject"), properties);

                        properties = EmailConfig.getEmailProperties(orderHasCourse.getCourse().getInstructorId().getGeneralUserProfile().getFirstName() + " " + orderHasCourse.getCourse().getInstructorId().getGeneralUserProfile().getLastName(), "Refund Processed for Your Course " + orderHasCourse.getCourse().getCourseTitle());
                        properties.put("courseName", orderHasCourse.getCourse().getCourseTitle());
                        properties.put("instructorName", orderHasCourse.getCourse().getInstructorId().getGeneralUserProfile().getFirstName() + " " + orderHasCourse.getCourse().getInstructorId().getGeneralUserProfile().getLastName());

                        emailSender.sendEmail("EmailToInstructorFromAdminMessage", orderHasCourse.getCourse().getInstructorId().getGeneralUserProfile().getEmail(), (String) properties.getProperty("from"), (String) properties.get("subject"), properties);

                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }

                }

                successResponse.setMessage("Refund's status updated successfully");
                successResponse.setVariable(VarList.RSP_SUCCESS);
                return successResponse;


            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not authenticated", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public List<GetAllRefundsResponse> getAllRefunds() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
            if (profile != null && profile.getIsActive() == 1) {
                if (profile.getGupType().getId() != 3) {
                    throw new ErrorException("You are not an admin", VarList.RSP_NO_DATA_FOUND);
                }
                List<Refunds> refundsList = refundsRepository.findAll();
                List<GetAllRefundsResponse> getAllRefundsResponses = new ArrayList<>();
                for (Refunds refunds : refundsList) {
                    if (refunds.getRefundStatus().getId() == 2 || refunds.getRefundStatus().getId() == 4) {
                        GetAllRefundsResponse getAllRefundsResponse = new GetAllRefundsResponse();
                        getAllRefundsResponse.setRefundCode(refunds.getRefundCode());
                        getAllRefundsResponse.setRefundAmount(decimalFormat.format(refunds.getRefundAmount()));
                        getAllRefundsResponse.setPurchasedAmount(decimalFormat.format(refunds.getOrder().getTotal()));
                        getAllRefundsResponse.setCurrency(refunds.getOrder().getCurrency());

                        CourseDetailsResponse courseDetailsResponse = new CourseDetailsResponse();
                        courseDetailsResponse.setCourseTitle(refunds.getOrderHasCourse().getCourse().getCourseTitle());
                        courseDetailsResponse.setCourseProgress(refunds.getOrderHasCourse().getProgress());

                        List<CourseSection> courseSections = courseSectionRepository.getCourseSectionByCourse(refunds.getOrderHasCourse().getCourse());
                        if (courseSections == null) {
                            throw new ErrorException("course sections not found", VarList.RSP_NO_DATA_FOUND);
                        }
                        courseDetailsResponse.setAllSectionCount((courseSections != null && !courseSections.isEmpty()) ? courseSections.size() : 0);
                        String completeSection = refunds.getOrderHasCourse().getCompletedSections();
                        String[] splitCompleteSection = null;
                        if (completeSection != null) {
                            splitCompleteSection = completeSection.split(",");
                        }
                        courseDetailsResponse.setSectionCompleteCount(splitCompleteSection != null ? splitCompleteSection.length : 0);
                        courseDetailsResponse.setCourseCompletion(
                                (splitCompleteSection != null && courseSections != null && !courseSections.isEmpty())
                                        ? ((double) splitCompleteSection.length / courseSections.size()) * 100
                                        : 0
                        );

                        getAllRefundsResponse.setCourseDetailsResponse(courseDetailsResponse);
                        getAllRefundsResponse.setPurchasedDate(refunds.getOrder().getBuyDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(updatedFormatter));
                        UserDetails userDetails = new UserDetails();
                        userDetails.setUserCode(refunds.getGeneralUserProfile().getUserCode());
                        userDetails.setUserName(refunds.getGeneralUserProfile().getFirstName() + " " + refunds.getGeneralUserProfile().getLastName());
                        userDetails.setEmail(refunds.getGeneralUserProfile().getEmail());
                        userDetails.setProfileImg(refunds.getGeneralUserProfile().getProfileImg());
                        userDetails.setRegisteredDate(refunds.getGeneralUserProfile().getRegisteredDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(updatedFormatter));
                        getAllRefundsResponse.setUserDetails(userDetails);
                        getAllRefundsResponse.setReason(refunds.getReason());
                        List<GetOwnRefundsResponse> getOwnRefundsResponses = new ArrayList<>();
                        refundsList = refundsRepository.getRefundsByGeneralUserProfile(refunds.getGeneralUserProfile());

                        int noOfRefundRequest = 0;
                        int noOfRefundRejections = 0;
                        int noOfRefundGranted = 0;
                        int totalNumberOfRefunds = refundsList.size();
                        List<CourseDetailsResponse> courseDetailsResponses = new ArrayList<>();
                        for (Refunds refundsObj : refundsList) {
                            if (refundsObj.getRefundStatus().getId() == 2)
                                noOfRefundRequest++;
                            else if (refundsObj.getRefundStatus().getId() == 3)
                                noOfRefundRejections++;
                            else if (refundsObj.getRefundStatus().getId() == 4)
                                noOfRefundGranted++;

                            if (!refunds.getRefundCode().equals(refundsObj.getRefundCode())) {
                                GetOwnRefundsResponse getOwnRefundsResponse = new GetOwnRefundsResponse();
                                getOwnRefundsResponse.setRefundCode(refundsObj.getRefundCode());
                                getOwnRefundsResponse.setRefundAmount(decimalFormat.format(refundsObj.getRefundAmount()));
                                getOwnRefundsResponse.setReason(refundsObj.getReason());


                                CourseDetailsResponse courseDetailsResponseObj = new CourseDetailsResponse();
                                courseDetailsResponseObj.setCourseTitle(refundsObj.getOrderHasCourse().getCourse().getCourseTitle());
                                courseDetailsResponseObj.setCourseProgress(refundsObj.getOrderHasCourse().getProgress());
                                courseSections = courseSectionRepository.getCourseSectionByCourse(refundsObj.getOrderHasCourse().getCourse());
                                if (courseSections == null) {
                                    throw new ErrorException("course sections not found", VarList.RSP_NO_DATA_FOUND);
                                }
                                courseDetailsResponseObj.setAllSectionCount((courseSections != null && !courseSections.isEmpty()) ? courseSections.size() : 0);
                                completeSection = refunds.getOrderHasCourse().getCompletedSections();
                                splitCompleteSection = null;
                                if (completeSection != null) {
                                    splitCompleteSection = completeSection.split(",");
                                }
                                courseDetailsResponse.setSectionCompleteCount(splitCompleteSection != null ? splitCompleteSection.length : 0);
                                courseDetailsResponse.setCourseCompletion(
                                        (splitCompleteSection != null && courseSections != null && !courseSections.isEmpty())
                                                ? ((double) splitCompleteSection.length / courseSections.size()) * 100
                                                : 0
                                );


                                courseDetailsResponses.add(courseDetailsResponseObj);

                                getOwnRefundsResponse.setCourseDetailsResponses(courseDetailsResponses);
                                getOwnRefundsResponse.setAdminComment(refundsObj.getComment());
                                getOwnRefundsResponse.setRequestDate(refundsObj.getRequestDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(updatedFormatter));
                                getOwnRefundsResponse.setPurchasedAmount(decimalFormat.format(refundsObj.getOrder().getTotal()));
                                getOwnRefundsResponse.setCurrency(refundsObj.getOrder().getCurrency());
                                getOwnRefundsResponse.setPurchasedDate(refundsObj.getOrder().getBuyDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(updatedFormatter));
                                getOwnRefundsResponse.setAdminAction(refundsObj.getRefundStatus().getRefundStatus());
                                getOwnRefundsResponses.add(getOwnRefundsResponse);
                            }
                        }
                        userDetails.setNoOfRefundRequest(noOfRefundRequest);
                        userDetails.setNoOfRefundRejections(noOfRefundRejections);
                        userDetails.setNoOfRefundGranted(noOfRefundGranted);
                        userDetails.setTotalNumberOfRefunds(totalNumberOfRefunds);
                        getAllRefundsResponse.setGetOwnRefundsResponse(getOwnRefundsResponses);
                        getAllRefundsResponse.setStatus(refunds.getRefundStatus().getRefundStatus());
                        getAllRefundsResponses.add(getAllRefundsResponse);
                    }
                }
                return getAllRefundsResponses;
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not authenticated", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public List<CheckRefundStatusResponse> checkRefundStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
            if (profile != null && profile.getIsActive() == 1) {
                List<Refunds> refunds = refundsRepository.getRefundsByGeneralUserProfile(profile);
                List<CheckRefundStatusResponse> checkRefundStatusResponses = new ArrayList<>();
                for (Refunds refund : refunds) {
                    if (refund.getRefundStatus().getId() != 1) {
                        CheckRefundStatusResponse checkRefundStatusResponse = new CheckRefundStatusResponse();
                        checkRefundStatusResponse.setRefundCode(refund.getRefundCode());
                        checkRefundStatusResponse.setReason(refund.getReason());
                        checkRefundStatusResponse.setStatus(refund.getRefundStatus().getRefundStatus());
                        checkRefundStatusResponse.setRefundAmount(refund.getRefundAmount().toString());
                        checkRefundStatusResponse.setComment(refund.getComment() == null ? "" : refund.getComment());
                        checkRefundStatusResponses.add(checkRefundStatusResponse);
                    }
                }
                return checkRefundStatusResponses;
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not authenticated", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public List<ReviewResponse> getReviewsByCourseCode(String courseCode) {
        Course course = courseRepository.getCourseByCode(courseCode);
        if (course == null) {
            throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
        }
        List<ReviewResponse> reviewResponseList = new ArrayList<>();

        List<Review> reviewList = reviewRepository.getReviewsByCourse(course);

        for (Review review : reviewList) {
            ReviewResponse reviewResponse = new ReviewResponse();
            reviewResponse.setReviewCode(review.getReviewCode());
            reviewResponse.setUserCode(review.getGeneralUserProfile().getUserCode());
            reviewResponse.setFullName(review.getGeneralUserProfile().getFirstName() + " " + review.getGeneralUserProfile().getLastName());
            reviewResponse.setUserProfile(review.getGeneralUserProfile().getProfileImg());
            reviewResponse.setComment(review.getComment() == null ? "" : review.getComment());
            reviewResponse.setRating(review.getRating());
            reviewResponse.setDate(review.getDate());

            List<RepliesToReviewResponse> replies = new ArrayList<>();
            List<ReplyToReview> repliesToReview = replyToReviewRepository.getReplyToReviewByReview(review);
            RepliesToReviewResponse repliesToReviewResponse;
            for (ReplyToReview replyToReview : repliesToReview) {
                repliesToReviewResponse = new RepliesToReviewResponse();
                repliesToReviewResponse.setUserCode(replyToReview.getGeneralUserProfile().getUserCode());
                repliesToReviewResponse.setProfileImg(review.getGeneralUserProfile().getProfileImg() == null ? "" : review.getGeneralUserProfile().getProfileImg());
                repliesToReviewResponse.setComment(replyToReview.getComment());
                repliesToReviewResponse.setName(replyToReview.getGeneralUserProfile().getFirstName() + " " + replyToReview.getGeneralUserProfile().getLastName());
                repliesToReviewResponse.setUserType(replyToReview.getGeneralUserProfile().getGupType().getId());
                repliesToReviewResponse.setCreatedDate(replyToReview.getDate());
                replies.add(repliesToReviewResponse);
            }
            reviewResponse.setReplies(replies);
            reviewResponseList.add(reviewResponse);
        }

        return reviewResponseList;
    }

    @Override
    public List<GetOwnAllRefundsResponse> getOwnAllRefunds() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
            if (profile != null && profile.getIsActive() == 1) {
                List<Order> orders = orderRepository.getOrdersByGeneralUserProfile(profile);
                List<GetOwnAllRefundsResponse> getOwnAllRefundsResponses = new ArrayList<>();
                for (Order order : orders) {

                    List<OrderHasCourse> orderHasCourses = orderHasCourseRepository.getOrderHasCoursesByOrder(order);

                    for (OrderHasCourse orderHasCourse : orderHasCourses) {
                        Refunds refunds = refundsRepository.getRefundsByOrderHasCourse(orderHasCourse);
                        GetOwnAllRefundsResponse getOwnAllRefundsResponse = new GetOwnAllRefundsResponse();
                        if (refunds != null) {
                            getOwnAllRefundsResponse.setStatus(refunds.getRefundStatus().getId() == 5 ? "Processed" : refunds.getRefundStatus().getRefundStatus());
                        } else {
                            RefundStatus refundStatus = refundStatusRepository.getRefundStatusById(1);
                            if (refundStatus == null) {
                                throw new ErrorException("RefundStatus not found", VarList.RSP_NO_DATA_FOUND);
                            }
                            getOwnAllRefundsResponse.setStatus(refundStatus.getRefundStatus());
                        }

                        getOwnAllRefundsResponse.setRefundedTo(order.getPaymentMethod().getMethod());
                        getOwnAllRefundsResponse.setDate(order.getBuyDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter));
                        getOwnAllRefundsResponse.setCurrency(order.getCurrency());
                        getOwnAllRefundsResponse.setAmount(decimalFormat.format(orderHasCourse.getItemPrice()));
                        getOwnAllRefundsResponse.setOrderId(order.getId());
                        getOwnAllRefundsResponse.setItemCode(orderHasCourse.getItemCode());
                        getOwnAllRefundsResponse.setCourseTitle(orderHasCourse.getCourse().getCourseTitle());
                        Transaction transaction = transactionRepository.getTransactionByOrder(order);
                        getOwnAllRefundsResponse.setTransactionCode(transaction == null ? "" : transaction.getTransactionCode());
                        getOwnAllRefundsResponses.add(getOwnAllRefundsResponse);

                    }
                }
                return getOwnAllRefundsResponses;
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not authenticated", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public List<GetCompletedRefundsResponse> getCompletedRefunds() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
            if (profile != null && profile.getIsActive() == 1) {
                if (profile.getGupType().getId() != 3) {
                    throw new ErrorException("You are not an admin", VarList.RSP_NO_DATA_FOUND);
                }

                List<ApprovedRefunds> approvedRefunds = approvedRefundsRepository.findAll();
                List<GetCompletedRefundsResponse> getCompletedRefundsResponses = new ArrayList<>();
                for (ApprovedRefunds approvedRefundsObj : approvedRefunds) {
                    GetCompletedRefundsResponse getCompletedRefundsResponse = new GetCompletedRefundsResponse();
                    getCompletedRefundsResponse.setCourseTitle(approvedRefundsObj.getCourse().getCourseTitle());
                    getCompletedRefundsResponse.setPurchasedDate(approvedRefundsObj.getPurchasedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(updatedFormatter));
                    getCompletedRefundsResponse.setPurchasedAmount(approvedRefundsObj.getPurchasedAmount());
                    getCompletedRefundsResponse.setRefundAmount(approvedRefundsObj.getRefundAmount());
                    getCompletedRefundsResponse.setStudentName(approvedRefundsObj.getGeneralUserProfile().getFirstName() + " " + approvedRefundsObj.getGeneralUserProfile().getLastName());
                    getCompletedRefundsResponse.setTransferredDate(approvedRefundsObj.getTransferredDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(updatedFormatter));
                    getCompletedRefundsResponse.setCurrency(approvedRefundsObj.getCurrency() == null ? "" : approvedRefundsObj.getCurrency());
                    getCompletedRefundsResponses.add(getCompletedRefundsResponse);
                }
                return getCompletedRefundsResponses;

            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not authenticated", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse addPreviousView(AddPreviousViewRequest addPreviousViewRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
            if (profile != null && profile.getIsActive() == 1) {

                final String itemCode = addPreviousViewRequest.getItemCode();
                final Integer sectionCurriculumItemId = addPreviousViewRequest.getSectionCurriculumItemId();

                if (itemCode == null || itemCode.isEmpty() || sectionCurriculumItemId == null || sectionCurriculumItemId.toString().isEmpty())
                    throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);

                OrderHasCourse orderHasCourse = orderHasCourseRepository.getOrderHasCourseByItemCode(itemCode);
                if (orderHasCourse == null)
                    throw new ErrorException("Invalid item code", VarList.RSP_NO_DATA_FOUND);

                if (!orderHasCourse.getOrder().getGeneralUserProfile().equals(profile))
                    throw new ErrorException("You can't do this process because you don't own it", VarList.RSP_NO_DATA_FOUND);

                SectionCurriculumItem sectionCurriculumItem = sectionCurriculumItemRepository.getSectionCurriculumItemById(sectionCurriculumItemId);
                if (sectionCurriculumItem == null)
                    throw new ErrorException("Invalid section curriculum item id", VarList.RSP_NO_DATA_FOUND);

                if (!orderHasCourse.getCourse().equals(sectionCurriculumItem.getCourseSection().getCourse()))
                    throw new ErrorException("Invalid details", VarList.RSP_NO_DATA_FOUND);

                PreviousView previousView = previousViewRepository.getPreviousViewByOrderHasCourseAndGeneralUserProfile(orderHasCourse, profile);

                if (previousView == null) {
                    previousView = new PreviousView();
                    previousView.setGeneralUserProfile(profile);
                    previousView.setOrderHasCourse(orderHasCourse);
                }

                previousView.setSectionCurriculumItem(sectionCurriculumItem);
                previousViewRepository.save(previousView);

                successResponse.setMessage("Previous view updated successfully");
                successResponse.setVariable(VarList.RSP_SUCCESS);

                return successResponse;
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not authenticated", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public GetPreviousViewResponse getPreviousView(String itemCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
            if (profile != null && profile.getIsActive() == 1) {

                OrderHasCourse orderHasCourse = orderHasCourseRepository.getOrderHasCourseByItemCode(itemCode);
                if (orderHasCourse == null)
                    throw new ErrorException("Invalid item code", VarList.RSP_NO_DATA_FOUND);

                if (!orderHasCourse.getOrder().getGeneralUserProfile().equals(profile))
                    throw new ErrorException("You can't do this process because you don't own it", VarList.RSP_NO_DATA_FOUND);

                PreviousView previousView = previousViewRepository.getPreviousViewByOrderHasCourseAndGeneralUserProfile(orderHasCourse, profile);
                GetPreviousViewResponse getPreviousViewResponse = new GetPreviousViewResponse();
                getPreviousViewResponse.setPreviousSectionCurriculumItemId(previousView == null ? "" : previousView.getSectionCurriculumItem().getId().toString());
                getPreviousViewResponse.setCurriculumItemType(previousView == null ? "" : previousView.getSectionCurriculumItem().getCurriculumItemType().getName());
                getPreviousViewResponse.setSectionId(previousView == null ? "" : previousView.getSectionCurriculumItem().getCourseSection().getId().toString());

                return getPreviousViewResponse;

            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not authenticated", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse updatePreviousViewDuration(UpdatePreviousViewDurationRequest updatePreviousViewDurationRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
            if (profile != null && profile.getIsActive() == 1) {

                final Integer curriculumItemId = updatePreviousViewDurationRequest.getCurriculumItemId();
                final Double duration = updatePreviousViewDurationRequest.getDuration();

                if (curriculumItemId == null || curriculumItemId.toString().isEmpty() || duration == null || duration.toString().isEmpty())
                    throw new ErrorException("Invalid request details", VarList.RSP_NO_DATA_FOUND);

                SectionCurriculumItem sectionCurriculumItem = sectionCurriculumItemRepository.getSectionCurriculumItemById(curriculumItemId);
                if (sectionCurriculumItem == null)
                    throw new ErrorException("Invalid curriculum item id", VarList.RSP_NO_DATA_FOUND);

                PreviousView previousView = previousViewRepository.getPreviousViewBySectionCurriculumItemAndGeneralUserProfile(sectionCurriculumItem, profile);
                if (previousView == null)
                    throw new ErrorException("Not found data", VarList.RSP_NO_DATA_FOUND);

                previousView.setDuration(duration);
                previousViewRepository.save(previousView);

                successResponse.setMessage("Duration successfully updated");
                successResponse.setVariable(VarList.RSP_SUCCESS);

                return successResponse;
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not authenticated", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public String getCertificate(String itemCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
            if (profile != null && profile.getIsActive() == 1) {
                OrderHasCourse orderHasCourse = orderHasCourseRepository.getOrderHasCourseByItemCode(itemCode);
                if (orderHasCourse == null)
                    throw new ErrorException("Invalid itemCode", VarList.RSP_NO_DATA_FOUND);
                if (orderHasCourse.getOrder().getGeneralUserProfile() != profile)
                    throw new ErrorException("You cannot access someone else's certificate", VarList.RSP_NO_DATA_FOUND);
                return orderHasCourse.getCertificate() == null || orderHasCourse.getCertificate().isEmpty() ?
                        "You have not completed your course yet" : orderHasCourse.getCertificate();
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not authenticated", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public Set<GetCoursesDataResponse> getExcelCoursesPurchasedByTheStudent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                List<Order> orders = orderRepository.getOrdersByGeneralUserProfile(profile);
                Set<GetCoursesDataResponse> responseLists = new HashSet<>();
                for (Order order : orders) {
                    List<OrderHasCourse> orderHasCourses = orderHasCourseRepository.getOrderHasCoursesByOrder(order);
                    for (OrderHasCourse orderHasCourse : orderHasCourses) {
                        if (orderHasCourse.getIsDelete() == null || orderHasCourse.getIsDelete() == 0) {
                            Course course = orderHasCourse.getCourse();
                            CourseLandingPage courseLandingPage = courseLandingPageRepository.getCourseLandingPagesByCourse(course);
                            if (courseLandingPage == null)
                                throw new ErrorException("Course landing page not found", VarList.RSP_NO_DATA_FOUND);
                            List<Topic> topics = topicRepository.getTopicsByLinkNameOrLinkName("excel", "api-ex");
                            for (Topic topic : topics) {
                                if (courseLandingPage.getTopic().equals(topic)) {
                                    if (course.getApprovalType().getId() == 5) {
                                        responseLists.add(getCoursesDataResponses(course, orderHasCourse, courseLandingPage));
                                    }
                                }
                            }
                            if (course.getApprovalType().getId() == 5) {
                                CourseKeyword courseKeyword = courseKeywordRepository.getCourseKeywordByCourseAndName(course, "excel");
                                if (courseKeyword != null) {
                                    responseLists.add(getCoursesDataResponses(course, orderHasCourse, courseLandingPage));
                                }
                            }
                        }
                    }
                }
                return responseLists;
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    private GetCoursesDataResponse getCoursesDataResponses(Course course, OrderHasCourse orderHasCourse, CourseLandingPage courseLandingPage) {
        GetCoursesDataResponse getCoursesDataResponse = new GetCoursesDataResponse();
        getCoursesDataResponse.setProgressValue(orderHasCourse.getProgress());
        getCoursesDataResponse.setItem_code(orderHasCourse.getItemCode());
        getCoursesDataResponse.setPurchasedDate(orderHasCourse.getOrder().getBuyDate());
        getCoursesDataResponse.setCourse_code(course.getCode());
        getCoursesDataResponse.setTotalVideoLength(course.getCourseLength());
        getCoursesDataResponse.setCreated_date(course.getCreatedDate().toString());
        getCoursesDataResponse.setId(course.getId());
        getCoursesDataResponse.setImg(course.getImg());
        getCoursesDataResponse.setDuration(Double.toString(course.getCourseLength()));
        getCoursesDataResponse.setIsPaid(course.getIsPaid() == 2);
        getCoursesDataResponse.setCourse_outline("OutLine is not added to the course");
        getCoursesDataResponse.setLevel((courseLandingPage != null) ? courseLandingPage.getCourseLevel().getName() : null);
        getCoursesDataResponse.setTitle(course.getCourseTitle());
        List<Review> reviews = reviewRepository.getReviewsByCourse(orderHasCourse.getCourse());
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
            getRatingResponses.add(getRatingResponse);
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
        getCoursesDataResponse.setReviews(getRatingResponses);

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
        getCoursesDataResponse.setStudent(0);
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
                    getCurriculumItemFilesResponse.setVideoLength(curriculumItemFile.getVideoLength() == null ? 0 : curriculumItemFile.getVideoLength());
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

}
