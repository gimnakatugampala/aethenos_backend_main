package lk.exon.aethenosapi.service.impl;

import lk.exon.aethenosapi.config.Config;
import lk.exon.aethenosapi.config.EmailConfig;
import lk.exon.aethenosapi.entity.*;
import lk.exon.aethenosapi.entity.Currency;
import lk.exon.aethenosapi.exception.ErrorException;
import lk.exon.aethenosapi.payload.request.*;
import lk.exon.aethenosapi.payload.response.*;
import lk.exon.aethenosapi.repository.*;
import lk.exon.aethenosapi.service.CourseService;
import lk.exon.aethenosapi.utils.EmailSender;
import lk.exon.aethenosapi.utils.FileUploadUtil;
import lk.exon.aethenosapi.utils.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private UserProfileServiceImpl userProfileServiceImpl;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseKeywordRepository courseKeywordRepository;
    @Autowired
    private CourseCategoryRepository courseCategoryRepository;
    @Autowired
    private CourseSubCategoryRepository courseSubCategoryRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private ApprovalTypeRepository approvalTypeRepository;
    @Autowired
    private InstructorProfileRepository instructorProfileRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private DiscountTypeRepository discountTypeRepository;
    @Autowired
    private CoursePriceRepository coursePriceRepository;
    @Autowired
    private CouponTypeRepository couponTypeRepository;
    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponPriceRepository couponPriceRepository;
    @Autowired
    private GeneralUserProfileRepository generalUserProfileRepository;
    @Autowired
    private GupTypeRepository gupTypeRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private PriceSetupRepository priceSetupRepository;
    @Autowired
    private ExternalCourseRepository externalCourseRepository;
    @Autowired
    private StudentBuyCouponCourseRepository studentBuyCouponCourseRepository;
    private SuccessResponse successResponse = new SuccessResponse();

    @Override
    public SuccessResponse addCourse(CourseRequest courseRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    final String courseTitle = courseRequest.getCourse_title();
                    final MultipartFile image = courseRequest.getImg();
                    final String video = courseRequest.getTest_video();
                    final int approvalType = courseRequest.getApproval_type_id();
                    final int categoryId = courseRequest.getCourse_category_id();
                    final String[] keyword = courseRequest.getKeywords();
                    if (courseTitle.isEmpty() || courseTitle == null) {
                        throw new ErrorException("Please add a course title", VarList.RSP_NO_DATA_FOUND);
                    } else if (image == null || image.isEmpty()) {
                        throw new ErrorException("Please add a course's image", VarList.RSP_NO_DATA_FOUND);
                    } else if (video == null || video.isEmpty()) {
                        throw new ErrorException("Please add a course's test video", VarList.RSP_NO_DATA_FOUND);
                    } else if (!image.getContentType().startsWith("image/") || !image.getOriginalFilename().matches(".*\\.(jpg|jpeg|png|gif|bmp|webp)$")) {
                        throw new ErrorException("Invalid image file type or extension. Only image files are allowed.", VarList.RSP_NO_DATA_FOUND);
                    } else if (approvalType == 0) {
                        throw new ErrorException("Please add a approval type", VarList.RSP_NO_DATA_FOUND);
                    } else if (categoryId == 0) {
                        throw new ErrorException("Please add a category", VarList.RSP_NO_DATA_FOUND);
                    } else if (keyword.length < 5) {
                        throw new ErrorException("Please add a keyword", VarList.RSP_NO_DATA_FOUND);
                    } else {
                        CourseCategory courseCategory = courseCategoryRepository.getCourseCategoryById(categoryId);
                        Course getCourse = courseRepository.getCourseByCourseTitle(courseRequest.getCourse_title());
                        if (getCourse == null) {
                            Course course = new Course();
                            course.setCode(UUID.randomUUID().toString());
                            course.setCourseTitle(courseRequest.getCourse_title());
                            course.setTest_video(Config.TEST_VIDEO_UPLOAD_URL + video);
                            try {
                                FileUploadResponse imageUploadResponse = FileUploadUtil.saveFile(courseRequest.getImg(), "courses-images");
                                course.setImg(imageUploadResponse.getFilename());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            course.setCreatedDate(new Date());

                            course.setCreatedDate(new Date());
                            String customId = generateCustomUUID();
                            course.setReferralCode(customId);
                            if (courseCategory != null) {
                                course.setCourseCategory(courseCategory);
                            } else {
                                throw new ErrorException("Course category not available", VarList.RSP_NO_DATA_FOUND);
                            }
                            ApprovalType Courseapproval = approvalTypeRepository.getApprovalTypeById(approvalType);
                            if (Courseapproval != null) {
                                course.setApprovalType(Courseapproval);
                            } else {
                                throw new ErrorException("Approval type not available", VarList.RSP_NO_DATA_FOUND);
                            }
                            InstructorProfile instructorProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(profile);
                            if (instructorProfile != null) {
                                course.setInstructorId(instructorProfile);
                            } else {
                                throw new ErrorException("Instructor profile not available", VarList.RSP_NO_DATA_FOUND);
                            }
                            course.setIsPaid(1);
                            course.setIsOwned((byte) 0);
                            courseRepository.save(course);
                            for (int i = 0; i < keyword.length; i++) {
                                CourseKeyword courseKeyword = new CourseKeyword();
                                courseKeyword.setName(keyword[i]);
                                courseKeyword.setCourse(course);
                                courseKeywordRepository.save(courseKeyword);
                            }
                            List<Country> countryList = countryRepository.findAll();
                            if (countryList.size() > 0) {
                                for (Country country : countryList) {
                                    CoursePrice coursePrice = new CoursePrice();
                                    coursePrice.setDiscountValue((double) 0);
                                    coursePrice.setValue((double) 0);
                                    coursePrice.setCountry(country);
                                    coursePrice.setCourse(course);
                                    Currency currency = currencyRepository.getCurrencyById(country.getId());
                                    coursePrice.setCurrency(currency);
                                    DiscountType discountType = discountTypeRepository.getDiscountTypeById(1);
                                    coursePrice.setDiscountType(discountType);
                                    coursePriceRepository.save(coursePrice);
                                }
                            } else {
                                throw new ErrorException("No countries available", VarList.RSP_NO_DATA_FOUND);
                            }
                            SuccessResponse successResponse = new SuccessResponse();

                            Properties properties = EmailConfig.getEmailProperties(profile.getFirstName() + " " + profile.getLastName(), "Your test video has been submitted for approval.");
                            properties.put("courseTitle", course.getCourseTitle());
                            try {
                                EmailSender emailSender = new EmailSender();
                                emailSender.sendEmail("CourseCreationSuccessfulMessage", profile.getEmail(), (String) properties.getProperty("from"), (String) properties.get("subject"), properties);

                                GupType gupType = gupTypeRepository.getGupTypeById(3);
                                List<GeneralUserProfile> adminProfile = generalUserProfileRepository.getGeneralUserProfileByGupType(gupType);
                                String noficationCode = UUID.randomUUID().toString();
                                for (GeneralUserProfile adminProfileObj : adminProfile) {
                                    Notification notification = new Notification();
                                    notification.setNotificationCode(noficationCode);
                                    notification.setNotification("You have a new test video to review");
                                    notification.setNotificationTime(new Date());
                                    notification.setGeneralUserProfile(adminProfileObj);
                                    notification.setRead(false);
                                    notificationRepository.save(notification);
                                    properties = EmailConfig.getEmailProperties(adminProfileObj.getFirstName() + " " + adminProfileObj.getLastName(), "Test Video For Admin Review.");
                                    properties.put("courseTitle", course.getCourseTitle());
                                    emailSender.sendEmail("TestVideoSubmissionForAdminReviewMessage", adminProfileObj.getEmail(), (String) properties.getProperty("from"), (String) properties.get("subject"), properties);
                                }

                            } catch (MessagingException e) {
                                e.printStackTrace();
                            }
                            successResponse.setMessage("Course added successfully");
                            successResponse.setVariable(VarList.RSP_SUCCESS);
                            return successResponse;
                        } else {
                            throw new ErrorException("The course has already been added", VarList.RSP_NO_DATA_FOUND);
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

    public static String generateCustomUUID() {
        UUID uuid = UUID.randomUUID();
        String customUuid = uuid.toString().replace("-", "").toUpperCase();
        customUuid = customUuid.replaceAll("[^A-Z0-9]", "");
        return customUuid.substring(0, 15);
    }

    @Override
    public List<GetCategoryResponse> getAllCategorySubCategoryTopics() {
        List<CourseCategory> categoryList = courseCategoryRepository.findAll();
        if (categoryList.size() != 0) {
            List<GetCategoryResponse> categoryArray = new ArrayList<>();
            for (CourseCategory category : categoryList) {
                List<CourseSubCategory> subCategoryList = courseSubCategoryRepository.getCourseSubCategoriesByCourseCategory(category);
                if (subCategoryList.size() != 0) {
                    List<GetSubCategoryResponse> subCategoryArray = new ArrayList<>();
                    GetCategoryResponse categories = new GetCategoryResponse();
                    for (CourseSubCategory subCategory : subCategoryList) {
                        List<Topic> topicList = topicRepository.getTopicsBySubCategory(subCategory);
                        List<GetTopicsResponse> topicArray = new ArrayList<>();
                        GetSubCategoryResponse subCategories = new GetSubCategoryResponse();
                        if (topicList.size() != 0) {
                            for (Topic topic : topicList) {
                                GetTopicsResponse topics = new GetTopicsResponse();
                                topics.setTopic(topic.getTopic());
                                topics.setTopicLinkName(topic.getLinkName());
                                topicArray.add(topics);
                            }
                            subCategories.setSubCategory(subCategory.getName());
                            subCategories.setSubCategoryLinkName(subCategory.getSubLinkName());
                            subCategories.setTopics(topicArray);
                            subCategoryArray.add(subCategories);
                        } else {
                            throw new ErrorException("Topics not found according to sub-category", VarList.RSP_NO_DATA_FOUND);
                        }
                    }
                    categories.setCategory(category.getName());
                    categories.setCategoryLinkName(category.getLinkName());
                    categories.setSubCategoryList(subCategoryArray);
                    categoryArray.add(categories);
                } else {
                    throw new ErrorException("Sub categories not found according to category", VarList.RSP_NO_DATA_FOUND);
                }

            }
            return categoryArray;
        } else {
            throw new ErrorException("categories not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse getReferralCodeByCourseCode(String courseCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {

                if (courseCode.isEmpty()) {
                    throw new ErrorException("Please send a course code", VarList.RSP_NO_DATA_FOUND);
                } else {

                    Course course = courseRepository.getCourseByCode(courseCode);
                    if (course != null) {
                        SuccessResponse successResponse = new SuccessResponse();
                        successResponse.setMessage(course.getReferralCode());
                        successResponse.setVariable(VarList.RSP_SUCCESS);
                        return successResponse;
                    } else {
                        throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
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
    public List<CouponTypeResponse> getCourseType() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                List<CouponType> couponTypesList = couponTypeRepository.findAll();
                if (couponTypesList.size() != 0) {

                    List<CouponTypeResponse> couponTypeArray = new ArrayList<>();
                    for (CouponType couponType : couponTypesList) {
                        CouponTypeResponse couponTypeResponse = new CouponTypeResponse();
                        couponTypeResponse.setId(couponType.getId());
                        couponTypeResponse.setName(couponType.getName());
                        couponTypeArray.add(couponTypeResponse);
                    }
                    return couponTypeArray;

                } else {
                    throw new ErrorException("Coupon types not found", VarList.RSP_NO_DATA_FOUND);
                }

            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse addFreeCoupon(AddFreeCouponRequest addFreeCouponRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    Course course = courseRepository.getCourseByCode(addFreeCouponRequest.getCourse_code());

                    Date myDate = addFreeCouponRequest.getStart_date();
                    LocalDate localDate = myDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    int month = localDate.getMonthValue();

                    List<Coupon> couponList = couponRepository.getCouponByCourse(course);
                    List<Integer> monthArray = new ArrayList<>();

                    for (Coupon coupon : couponList) {
                        Date myDate2 = coupon.getStartDate();
                        LocalDate localDate2 = myDate2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        int month2 = localDate2.getMonthValue();
                        monthArray.add(month2);
                    }
                    monthArray.add(month);
                    System.out.println(monthArray);
                    boolean hasMoreThan3Occurrences = checkForMoreThan3Occurrences(monthArray);

                    if (hasMoreThan3Occurrences) {
                        throw new ErrorException("This course has reached the monthly coupon limit of 3", VarList.RSP_NO_DATA_FOUND);
                    } else {
                        Coupon coupon = new Coupon();
                        if (addFreeCouponRequest.getCode().isEmpty()) {
                            coupon.setCode(generateCustomUUID());
                        } else {
                            if (couponRepository.getCouponByCode(addFreeCouponRequest.getCode()) == null) {
                                coupon.setCode(addFreeCouponRequest.getCode());
                            } else {
                                throw new ErrorException("Coupon code already exists", VarList.RSP_NO_DATA_FOUND);
                            }
                        }
                        if (course == null) {
                            throw new ErrorException("Course not found", VarList.RSP_NO_DATA_FOUND);
                        } else {
                            coupon.setCourse(course);
                        }
                        coupon.setCreatedDate(new Date());
                        coupon.setStartDate(addFreeCouponRequest.getStart_date());
                        coupon.setEndDate(addFreeCouponRequest.getEnd_date());
                        coupon.setCouponType(couponTypeRepository.getReferenceById(1));
                        coupon.setIsActive(1);
                        couponRepository.save(coupon);
                        successResponse.setMessage("Free coupon added successfully");
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
    public List<CouponResponse> getCouponsFromCourseCode(String courseCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {

                Course course = courseRepository.getCourseByCode(courseCode);
                if (course != null) {
                    List<Coupon> couponList = couponRepository.getCouponByCourse(course);
                    if (couponList.isEmpty()) {
                        throw new ErrorException("No coupons found", VarList.RSP_NO_DATA_FOUND);
                    } else {
                        List<CouponResponse> couponArrayList = new ArrayList<>();
                        for (Coupon coupon : couponList) {
                            CouponResponse couponResponse = new CouponResponse();
                            couponResponse.setCouponCode(coupon.getCode());
                            couponResponse.setStartDate(coupon.getStartDate());
                            couponResponse.setEndDate(coupon.getEndDate());
                            couponResponse.setCouponType(coupon.getCouponType());
                            couponResponse.setIsActive(coupon.getIsActive());
                            couponResponse.setCreatedDate(coupon.getCreatedDate() == null ? "" : coupon.getCreatedDate().toString());
                            couponResponse.setGlobalDiscount(coupon.getGlobal_discount() == null ? 0 : coupon.getGlobal_discount());
                            couponResponse.setGlobalDiscountPercentage(coupon.getGlobal_discount_percentage() == null ? 0 : coupon.getGlobal_discount_percentage());
                            couponResponse.setGlobalDiscountPrice(coupon.getGlobal_discount_price() == null ? 0 : coupon.getGlobal_discount_price());
                            couponResponse.setGlobalListPrice(coupon.getGlobal_list_price() == null ? 0 : coupon.getGlobal_list_price());
                            List<StudentBuyCouponCourse> studentBuyCouponCourses = studentBuyCouponCourseRepository.getStudentBuyCouponCourseByCoupon(coupon);
                            couponResponse.setRedemptions(coupon.getCouponType().getId() == 2 ? studentBuyCouponCourses.size() + "/Unlimited" : studentBuyCouponCourses.size() + "/1000");
                            List<CouponPriceList> couponPricesList = new ArrayList<>();
                            List<CouponPrice> couponPrices = couponPriceRepository.getCouponPricesByCoupon(coupon);
                            for (CouponPrice couponPrice : couponPrices) {
                                CouponPriceList couponPriceList = new CouponPriceList();
                                couponPriceList.setDiscount(couponPrice.getDiscount());
                                couponPriceList.setDiscountAmount(couponPrice.getDiscountAmount());
                                couponPriceList.setDiscountPrice(couponPrice.getDiscountPrice());
                                couponPriceList.setListPrice(couponPrice.getListPrice());
                                couponPriceList.setCountry(couponPrice.getCountry().getName());
                                couponPricesList.add(couponPriceList);
                            }
                            couponResponse.setCouponPrices(couponPricesList);
                            couponArrayList.add(couponResponse);
                        }
                        return couponArrayList;
                    }
                } else {
                    throw new ErrorException("No course found", VarList.RSP_NO_DATA_FOUND);
                }

            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse activeDeactiveCoupon(String couponCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {

                if (couponCode != null) {
                    Coupon coupon = couponRepository.getCouponByCode(couponCode);
                    if (coupon != null) {

                        if (coupon.getIsActive() == 1) {
                            coupon.setIsActive(0);
                            successResponse.setMessage("Coupon deactivated successfully");
                        } else {
                            coupon.setIsActive(1);
                            successResponse.setMessage("Coupon activated successfully");
                        }
                        couponRepository.save(coupon);
                        successResponse.setVariable(VarList.RSP_SUCCESS);
                        return successResponse;
                    } else {
                        throw new ErrorException("Coupon not found", VarList.RSP_NO_DATA_FOUND);
                    }
                } else {
                    throw new ErrorException("Please add a coupon code", VarList.RSP_NO_DATA_FOUND);
                }

            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse addDiscountCoupon(AddDiscountCouponRequest addDiscountCouponRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    final String courseCode = addDiscountCouponRequest.getCourse_code();
                    final Date startDate = addDiscountCouponRequest.getStart_date();
                    final Date endDate = addDiscountCouponRequest.getEnd_date();
                    final Double globalDiscount = addDiscountCouponRequest.getGlobal_discount();
                    final Double globalListPrice = addDiscountCouponRequest.getGlobal_list_price();
                    final Double globalDiscountPrice = addDiscountCouponRequest.getGlobal_discount_price();
                    final Double globalDiscountPercentage = addDiscountCouponRequest.getGlobal_discount_percentage();
                    final List<CouponPricingRequest> couponPricingRequests = addDiscountCouponRequest.getPrices();
                    String discountCouponCode = addDiscountCouponRequest.getCode();
                    Date createdDate = new Date();
                    if (courseCode == null || courseCode.isEmpty() ||
                            startDate == null || startDate.toString().isEmpty() ||
                            endDate == null || endDate.toString().isEmpty() ||
                            globalDiscount == null || globalDiscount.toString().isEmpty() ||
                            globalListPrice == null || globalListPrice.toString().isEmpty() ||
                            globalDiscountPrice == null || globalDiscountPrice.toString().isEmpty() ||
                            globalDiscountPercentage == null || globalDiscountPercentage.toString().isEmpty())
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);

                    Course course = courseRepository.getCourseByCode(courseCode);

                    if (course == null)
                        throw new ErrorException("Course not found", VarList.RSP_NO_DATA_FOUND);

                    if (startDate.before(removeTime(createdDate)))
                        throw new ErrorException("The start date cannot be earlier than today", VarList.RSP_NO_DATA_FOUND);

                    if (!endDate.after(startDate))
                        throw new ErrorException("The end date is not later than the start date.", VarList.RSP_NO_DATA_FOUND);

                    List<Coupon> couponList = couponRepository.getCouponByCourse(course);
                    List<Integer> monthArray = new ArrayList<>();

                    for (Coupon coupon : couponList) {
                        monthArray.add(coupon.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonthValue());
                    }
                    monthArray.add(startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonthValue());

                    if (checkForMoreThan3Occurrences(monthArray))
                        throw new ErrorException("This course has reached the monthly coupon limit of 3", VarList.RSP_NO_DATA_FOUND);

                    if (discountCouponCode == null || discountCouponCode.isEmpty()) {
                        discountCouponCode = generateCustomUUID();
                    } else {
                        if (couponRepository.getCouponByCode(discountCouponCode) != null)
                            throw new ErrorException("Coupon code already exists", VarList.RSP_NO_DATA_FOUND);
                    }
                    CouponType couponType = couponTypeRepository.getReferenceById(2);
                    if (couponType == null)
                        throw new ErrorException("Coupon type not found", VarList.RSP_NO_DATA_FOUND);
                    PriceSetup priceSetup = priceSetupRepository.getPriceSetupByCountryId(30);
                    if (priceSetup == null)
                        throw new ErrorException("Price setup not found", VarList.RSP_NO_DATA_FOUND);
                    if (globalDiscountPrice < priceSetup.getMinimumPrice())
                        throw new ErrorException("The global discount cannot be lower than the minimum price", VarList.RSP_NO_DATA_FOUND);

                    checkCouponPricingRequest(couponPricingRequests);

                    Coupon coupon = new Coupon();
                    coupon.setCode(discountCouponCode);
                    coupon.setCourse(course);
                    coupon.setCreatedDate(createdDate);
                    coupon.setStartDate(startDate);
                    coupon.setEndDate(endDate);
                    coupon.setCouponType(couponType);
                    coupon.setIsActive(1);
                    coupon.setGlobal_list_price(globalListPrice);
                    coupon.setGlobal_discount_price(globalDiscountPrice);
                    coupon.setGlobal_discount_percentage(globalDiscountPercentage);
                    coupon.setGlobal_discount(globalDiscount);
                    couponRepository.save(coupon);


                    for (CouponPricingRequest couponPricingRequest : couponPricingRequests) {
                        CouponPrice couponPrice = new CouponPrice();
                        couponPrice.setDiscount(couponPricingRequest.getDiscount());
                        couponPrice.setListPrice(couponPricingRequest.getListPrice());
                        couponPrice.setDiscountPrice(couponPricingRequest.getDiscountPrice());
                        Country country = countryRepository.getCountryByName(couponPricingRequest.getCountryName());
                        couponPrice.setCountry(country);
                        couponPrice.setDiscountAmount(couponPricingRequest.getDiscountAmount());
                        couponPrice.setCoupon(coupon);
                        couponPriceRepository.save(couponPrice);
                    }
                    successResponse.setMessage("Discount coupon added successfully");
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

    private Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private void checkCouponPricingRequest(List<CouponPricingRequest> couponPricingRequests) {
        Double discount;
        Double disCountAmount;
        Double discountPrice;
        Double listPrice;
        String countryName;
        Integer currencyId;
        PriceSetup priceSetup;
        for (CouponPricingRequest couponPricingRequest : couponPricingRequests) {
            discount = couponPricingRequest.getDiscount();
            disCountAmount = couponPricingRequest.getDiscountAmount();
            discountPrice = couponPricingRequest.getDiscountPrice();
            listPrice = couponPricingRequest.getListPrice();
            countryName = couponPricingRequest.getCountryName();
            currencyId = couponPricingRequest.getCurrencyId();

            if (discount == null || discount.toString().isEmpty() || disCountAmount == null || disCountAmount.toString().isEmpty() ||
                    discountPrice == null || discountPrice.toString().isEmpty() || listPrice == null | listPrice.toString().isEmpty() ||
                    countryName == null || countryName.isEmpty() || currencyId == null || currencyId.toString().isEmpty())
                throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);

            Country country = countryRepository.getCountryByName(countryName);
            if (country == null)
                throw new ErrorException("Invalid country name", VarList.RSP_NO_DATA_FOUND);

            priceSetup = priceSetupRepository.getPriceSetupByCountryId(country.getId());
            if (priceSetup == null)
                throw new ErrorException("Could not found price setup for " + country.getName(), VarList.RSP_NO_DATA_FOUND);
            if (discountPrice != 0) {
                if (discountPrice < priceSetup.getMinimumPrice())
                    throw new ErrorException("The " + country.getName() + " discount cannot be less than the minimum price", VarList.RSP_NO_DATA_FOUND);
            }
        }
    }

    @Override
    public List<GetTopicsWithIdResponse> getTopicsBySubCategory(int subCategoryId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {

                    List<Topic> topics = topicRepository.getTopicsBySubCategory(courseSubCategoryRepository.getCourseSubCategoryById(subCategoryId));
                    List<GetTopicsWithIdResponse> responseList = new ArrayList<>();

                    for (Topic topic : topics) {
                        GetTopicsWithIdResponse response = new GetTopicsWithIdResponse();
                        response.setId(topic.getId());
                        response.setTopic(topic.getTopic());
                        responseList.add(response);
                    }
                    return responseList;
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
    public int CheckOwnCourse(String courseCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {

                    Course course = courseRepository.getCourseByCode(courseCode);
                    if (course == null) {
                        throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
                    }
                    try {
                        return course.getIsOwned();
                    } catch (NullPointerException e) {
                        throw new ErrorException(e.getMessage(), VarList.RSP_NO_DATA_FOUND);
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
    public SuccessResponse addExternalCourseLinkAndRatings(AddExternalCourseLinkAndRatingsRequest addExternalCourseLinkAndRatingsRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {

                    final String courseCode = addExternalCourseLinkAndRatingsRequest.getCourseCode();
                    final String linkToCourse = addExternalCourseLinkAndRatingsRequest.getLinkToCourse();
                    final Double externalRating = addExternalCourseLinkAndRatingsRequest.getExternalRating();
                    final Long externalNumberOfStudents = addExternalCourseLinkAndRatingsRequest.getExternalNumberOfStudents();
                    final String anyComments = addExternalCourseLinkAndRatingsRequest.getAnyComments();

                    if (courseCode == null || courseCode.isEmpty() || ((linkToCourse == null || linkToCourse.isEmpty()) && (externalRating == null || externalRating.toString().isEmpty()) &&
                            (externalNumberOfStudents == null || externalNumberOfStudents.toString().isEmpty()) && (anyComments == null || anyComments.isEmpty())))
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);

                    Course course = courseRepository.getCourseByCode(courseCode);
                    if (course == null)
                        throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);

                    if (!course.getInstructorId().getGeneralUserProfile().equals(profile))
                        throw new ErrorException("You cannot access this process because this course is not owned", VarList.RSP_NO_DATA_FOUND);

                    ExternalCourse externalCourse = externalCourseRepository.getExternalCourseByCourse(course);

                    if (externalCourse == null) {
                        externalCourse = new ExternalCourse();
                        externalCourse.setCourse(course);
                    }

                    externalCourse.setLinkToCourse((linkToCourse == null || linkToCourse.isEmpty()) ? "" : linkToCourse);
                    externalCourse.setExternalRating((externalRating == null || externalRating.toString().isEmpty()) ? 0 : externalRating);
                    externalCourse.setExternalNumberOfStudents((externalNumberOfStudents == null || externalNumberOfStudents.toString().isEmpty()) ? 0 : externalNumberOfStudents);
                    externalCourse.setAnyComment((anyComments == null || anyComments.isEmpty()) ? "" : anyComments);

                    externalCourseRepository.save(externalCourse);

                    successResponse.setMessage("External course details added successfully");
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


    private static boolean checkForMoreThan3Occurrences(List<Integer> list) {
        Map<Integer, Integer> occurrencesMap = new HashMap<>();

        for (Integer element : list) {
            occurrencesMap.put(element, occurrencesMap.getOrDefault(element, 0) + 1);

            if (occurrencesMap.get(element) > 3) {
                return true;
            }
        }

        return false;
    }

        @Scheduled(cron = "0 0 0 * * ?")
//    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void deactivateExpiredCoupons() {
        Date now = new Date();
        List<Coupon> expiredCoupons =  couponRepository.findByEndDateBeforeAndIsActive(now, 1);

        expiredCoupons.forEach(coupon -> coupon.setIsActive(0));

        couponRepository.saveAll(expiredCoupons);

        System.out.println("✅ Expired coupons deactivated at: " + now);
    }

}

