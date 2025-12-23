package lk.exon.aethenosapi.service.impl;

import lk.exon.aethenosapi.config.Config;
import lk.exon.aethenosapi.config.EmailConfig;
import lk.exon.aethenosapi.entity.*;
import lk.exon.aethenosapi.exception.ErrorException;
import lk.exon.aethenosapi.payload.request.CourseRequest;
import lk.exon.aethenosapi.payload.response.CourseResponse;
import lk.exon.aethenosapi.payload.response.FileUploadResponse;
import lk.exon.aethenosapi.payload.response.SuccessResponse;
import lk.exon.aethenosapi.repository.*;
import lk.exon.aethenosapi.service.EditCourseService;
import lk.exon.aethenosapi.utils.EmailSender;
import lk.exon.aethenosapi.utils.FileUploadUtil;
import lk.exon.aethenosapi.utils.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
public class EditCourseImpl implements EditCourseService {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserProfileServiceImpl userProfileServiceImpl;
    @Autowired
    private CourseKeywordRepository courseKeywordRepository;
    @Autowired
    private CourseCategoryRepository courseCategoryRepository;
    @Autowired
    private ApprovalTypeRepository approvalTypeRepository;
    @Autowired
    private GupTypeRepository gupTypeRepository;
    @Autowired
    private GeneralUserProfileRepository generalUserProfileRepository;
    @Autowired
    private NotificationRepository notificationRepository;

    public CourseResponse getCourseByID(String courseID) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                Course course = courseRepository.getCourseByCode(courseID);
                if (course != null) {
                    CourseResponse courseResponse = new CourseResponse();
                    courseResponse.setCourse_title(course.getCourseTitle());
                    courseResponse.setCode(course.getCode());
                    courseResponse.setCourse_category_id(course.getCourseCategory().getId());
//                    courseResponse.setCourse_sub_category_id(course.getCourseSubCategory().getId());
                    courseResponse.setImg(course.getImg());
                    courseResponse.setTest_video(course.getTest_video());
                    courseResponse.setApproval_type_id(course.getApprovalType().getId());
//                    courseResponse.setDefault_price(course.getDefaultPrice());
                    List<CourseKeyword> courseKeywordList = courseKeywordRepository.findByCourse(course);
                    if (courseKeywordList != null) {
                        String[] courseKeywordArray = new String[5];
                        int i = 0;
                        for (CourseKeyword obj : courseKeywordList) {
                            courseKeywordArray[i] = obj.getName();
                            i = i + 1;
                        }
                        courseResponse.setKeywordArray(courseKeywordArray);
                    }
                    return courseResponse;
                } else {
                    throw new ErrorException("Course not available", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse updateCourse(CourseRequest courseRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        final String courseVideo = courseRequest.getTest_video();
        final MultipartFile courseImg = courseRequest.getImg();
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                Course course = courseRepository.getCourseByCode(courseRequest.getCode());

                if (course != null) {

                    if (courseImg != null && !courseImg.isEmpty()) {
                        if (!courseImg.getContentType().startsWith("image/") || !courseImg.getOriginalFilename().matches(".*\\.(jpg|jpeg|png|gif|bmp|webp)$")) {
                            throw new ErrorException("Invalid image file type or extension. Only image files are allowed.", VarList.RSP_NO_DATA_FOUND);
                        }
                        try {
                            FileUploadResponse imageUploadResponse = FileUploadUtil.saveFile(courseRequest.getImg(), "courses-images");
                            if (course.getImg() != null && !course.getImg().isEmpty()) {
                                Files.delete(Paths.get(Config.UPLOAD_URL + course.getImg()));
                            }
                            course.setImg(imageUploadResponse.getFilename());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    if (courseVideo != null && !courseVideo.isEmpty()) {
                        try {

                            if (course.getTest_video() != null && !course.getTest_video().isEmpty()) {
                                Files.delete(Paths.get(Config.UPLOAD_URL + course.getTest_video()));
                            }

                            course.setTest_video(Config.TEST_VIDEO_UPLOAD_URL + courseVideo);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (courseRequest.getCourse_title() != null && !courseRequest.getCourse_title().isEmpty()) {
                        course.setCourseTitle(courseRequest.getCourse_title());
                    }
                    CourseCategory courseCategory = courseCategoryRepository.getCourseCategoryById(courseRequest.getCourse_category_id());
                    if (courseCategory != null) {
                        course.setCourseCategory(courseCategory);
                    } else {
                        throw new ErrorException("Course category not available", VarList.RSP_NO_DATA_FOUND);
                    }

                    ApprovalType approvalType = approvalTypeRepository.getApprovalTypeById(courseRequest.getApproval_type_id());
                    if (approvalType != null) {
                        course.setApprovalType(approvalType);
                    } else {
                        throw new ErrorException("Approval type not available", VarList.RSP_NO_DATA_FOUND);
                    }
                    List<CourseKeyword> courseKeywordList = courseKeywordRepository.findByCourse(course);
                    if (courseKeywordList != null) {
                        int i = 0;
                        String[] courseArray = courseRequest.getKeywords();
                        for (CourseKeyword obj : courseKeywordList) {
                            obj.setName(courseArray[i]);
                            i = i + 1;
                            courseKeywordRepository.save(obj);
                        }
                    }
                    courseRepository.save(course);

                    Properties properties = EmailConfig.getEmailProperties(course.getInstructorId().getGeneralUserProfile().getFirstName() + " " + course.getInstructorId().getGeneralUserProfile().getLastName(), "Your test video has been re-submitted for approval");
                    properties.put("courseTitle",course.getCourseTitle());
                    try {
                        EmailSender emailSender = new EmailSender();
                        emailSender.sendEmail("TestVideoSentForReviewMessage", course.getInstructorId().getGeneralUserProfile().getEmail(), (String) properties.getProperty("from"), (String) properties.get("subject"), properties);

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
                            emailSender.sendEmail("TestVideoSubmissionForAdminReviewMessage", adminProfileObj.getEmail(), (String) properties.getProperty("from"), (String) properties.get("subject"), properties);
                        }

                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }

                    SuccessResponse successResponse = new SuccessResponse();
                    successResponse.setMessage("Test video updated successfully");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;


                } else {
                    throw new ErrorException("Course not available", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }


}
