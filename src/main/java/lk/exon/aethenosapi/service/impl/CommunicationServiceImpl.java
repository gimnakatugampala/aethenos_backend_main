package lk.exon.aethenosapi.service.impl;

import lk.exon.aethenosapi.config.EmailConfig;
import lk.exon.aethenosapi.entity.*;
import lk.exon.aethenosapi.exception.ErrorException;
import lk.exon.aethenosapi.payload.request.AddAnnouncementRequest;
import lk.exon.aethenosapi.payload.request.AddAnswerRequest;
import lk.exon.aethenosapi.payload.request.AddQuestionRequest;
import lk.exon.aethenosapi.payload.response.*;
import lk.exon.aethenosapi.repository.*;
import lk.exon.aethenosapi.service.CommunicationService;
import lk.exon.aethenosapi.utils.EmailSender;
import lk.exon.aethenosapi.utils.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class CommunicationServiceImpl implements CommunicationService {
    @Autowired
    private UserProfileServiceImpl userProfileServiceImpl;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private AnnouncementsRepository announcementsRepository;
    @Autowired
    private QuestionsRepository questionsRepository;
    @Autowired
    private OrderHasCourseRepository orderHasCourseRepository;
    @Autowired
    private InstructorProfileRepository instructorProfileRepository;
    @Autowired
    private GupTypeRepository gupTypeRepository;
    @Autowired
    private GeneralUserProfileRepository generalUserProfileRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public SuccessResponse addAnnouncements(AddAnnouncementRequest addAnnouncementRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getGupType().getId() == 2) {
                if (profile.getIsActive() == 1) {

                    final String courseCode = addAnnouncementRequest.getCourseCode();
                    final String title = addAnnouncementRequest.getTitle();
                    final String content = addAnnouncementRequest.getContent();

                    if (courseCode == null || courseCode.isEmpty() ||
                            title == null || title.isEmpty() ||
                            content == null || content.isEmpty())
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);

                    Course course = courseRepository.getCourseByCode(courseCode);
                    if (course == null)
                        throw new ErrorException("Course not found related to course code", VarList.RSP_NO_DATA_FOUND);

                    if (!course.getInstructorId().getGeneralUserProfile().equals(profile))
                        throw new ErrorException("You cannot access this process because this course is not owned", VarList.RSP_NO_DATA_FOUND);

                    Announcements announcements = new Announcements();
                    announcements.setCourse(course);
                    announcements.setTitle(title);
                    announcements.setContent(content);
                    announcements.setCreatedDate(new Date());
                    announcementsRepository.save(announcements);

                    List<OrderHasCourse> orderHasCourses = orderHasCourseRepository.getOrderHasCoursesByCourse(course);
                    for (OrderHasCourse orderHasCourse : orderHasCourses) {
                        GeneralUserProfile generalUserProfile = orderHasCourse.getOrder().getGeneralUserProfile();

                        String noficationCode = UUID.randomUUID().toString();

                        Notification notification = new Notification();
                        notification.setNotificationCode(noficationCode);
                        notification.setNotification("Your instructor, " + orderHasCourse.getCourse().getInstructorId().getGeneralUserProfile().getFirstName() + " " +
                                orderHasCourse.getCourse().getInstructorId().getGeneralUserProfile().getLastName() + ", has just posted a new announcement in " + orderHasCourse.getCourse().getCourseTitle() + ".");
                        notification.setNotificationTime(new Date());
                        notification.setGeneralUserProfile(generalUserProfile);
                        notification.setRead(false);
                        notificationRepository.save(notification);


                        Properties properties = EmailConfig.getEmailProperties(title, title + "[Announcement]", content);
                        properties.put("courseName", course.getCourseTitle());
                        properties.put("instructor", course.getInstructorId().getGeneralUserProfile().getFirstName() + " " + course.getInstructorId().getGeneralUserProfile().getLastName());
                        try {
                            EmailSender emailSender = new EmailSender();
                            emailSender.sendEmail("AnnouncementMessage", generalUserProfile.getEmail(), (String) properties.getProperty("from"), (String) properties.get("subject"), properties);
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        }

                    }

                    SuccessResponse successResponse = new SuccessResponse();
                    successResponse.setMessage("Announcement added successfully");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;
                } else {
                    throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not an instructor", VarList.RSP_NO_DATA_FOUND);
            }

        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public List<GetAnnouncementsResponse> getAnnouncements(String courseCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getGupType().getId() == 2) {
                if (profile.getIsActive() == 1) {
                    List<Announcements> announcementsList;
                    if (courseCode == null || courseCode.isEmpty()) {

                        InstructorProfile instructorProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(profile);
                        if (instructorProfile == null) {
                            throw new ErrorException("Invalid user", VarList.RSP_NO_DATA_FOUND);
                        }
                        List<Course> courses = courseRepository.getCourseByInstructorId(instructorProfile);
                        announcementsList = new ArrayList<>();
                        for (Course course : courses) {
                            announcementsList.addAll(announcementsRepository.getAnnouncementsByCourse(course));
                        }
                    } else {
                        Course course = courseRepository.getCourseByCode(courseCode);
                        if (course == null) {
                            throw new ErrorException("Course not found related to course code", VarList.RSP_NO_DATA_FOUND);
                        }
                        announcementsList = announcementsRepository.getAnnouncementsByCourse(course);
                    }
                    return getAnnouncementsResponses(announcementsList);
                } else {
                    throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not an instructor", VarList.RSP_NO_DATA_FOUND);
            }

        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    private List<GetAnnouncementsResponse> getAnnouncementsResponses(List<Announcements> announcementsList) {
        List<GetAnnouncementsResponse> responseList = new ArrayList<>();
        for (Announcements announcements : announcementsList) {
            GetAnnouncementsResponse response = new GetAnnouncementsResponse();
            response.setTittle(announcements.getTitle());
            response.setContent(announcements.getContent());
            response.setCreatedDate(announcements.getCreatedDate());
            responseList.add(response);
        }
        return responseList;
    }

    @Override
    public SuccessResponse addQuestion(AddQuestionRequest addQuestionRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (addQuestionRequest.getItemCode() == null || addQuestionRequest.getItemCode().isEmpty()) {
                    throw new ErrorException("please add a course code", VarList.RSP_NO_DATA_FOUND);
                }
                if (addQuestionRequest.getQuestion() == null || addQuestionRequest.getQuestion().isEmpty()) {
                    throw new ErrorException("please add a question", VarList.RSP_NO_DATA_FOUND);
                }

                try {
                    Course course = orderHasCourseRepository.getOrderHasCourseByItemCode(addQuestionRequest.getItemCode()).getCourse();

                    Questions question = new Questions();
                    question.setCourse(course);
                    question.setCode(UUID.randomUUID().toString());
                    question.setQuestion(addQuestionRequest.getQuestion());
                    question.setGeneralUserProfile(profile);
                    question.setDate(new Date());
                    question.setIsRead(0);
                    questionsRepository.save(question);


                    String noficationCode = UUID.randomUUID().toString();
                    Properties properties;

                    EmailSender emailSender = new EmailSender();

                    Notification notification = new Notification();
                    notification.setNotificationCode(noficationCode);
                    notification.setNotification("You have a new question from a student in \"" + course.getCourseTitle() + "\".");
                    notification.setNotificationTime(new Date());
                    notification.setGeneralUserProfile(course.getInstructorId().getGeneralUserProfile());
                    notification.setRead(false);
                    notificationRepository.save(notification);

                    properties = EmailConfig.getEmailProperties(course.getInstructorId().getGeneralUserProfile().getFirstName() + " " + course.getInstructorId().getGeneralUserProfile().getLastName(), "New Student Question for " + course.getCourseTitle() + ".");
                    properties.put("courseTitle", course.getCourseTitle());
                    emailSender.sendEmail("NewStudentQuestionMessage", course.getInstructorId().getGeneralUserProfile().getEmail(), (String) properties.getProperty("from"), (String) properties.get("subject"), properties);


                    SuccessResponse successResponse = new SuccessResponse();
                    successResponse.setMessage("The question related to the course was added successfully");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;
                } catch (NullPointerException e) {
                    throw new ErrorException("Course not found related to itemCode", VarList.RSP_NO_DATA_FOUND);
                } catch (MessagingException e) {
                    throw new ErrorException(e.getMessage(), VarList.RSP_NO_DATA_FOUND);
                }

            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }

        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public List<GetAllQuestionResponse> getAllQuestions(String courseCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);

        if (profile == null) {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }

        if (profile.getIsActive() != 1) {
            throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
        }

        if (courseCode == null || courseCode.isEmpty()) {
            InstructorProfile instructorProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(profile);
            if (instructorProfile == null) {
                throw new ErrorException("Invalid user", VarList.RSP_NO_DATA_FOUND);
            }
            List<Course> courses = courseRepository.getCourseByInstructorId(instructorProfile);
            List<GetAllQuestionResponse> allQuestions = new ArrayList<>();
            for (Course course : courses) {
                List<Questions> questions = questionsRepository.getQuestionsByCourse(course);
                allQuestions.addAll(getQuestionResponses(questions));
            }
            return allQuestions;
        } else {
            Course course = courseRepository.getCourseByCode(courseCode);
            if (course == null) {
                throw new ErrorException("Course not found related to course code", VarList.RSP_NO_DATA_FOUND);
            }
            List<Questions> questions = questionsRepository.getQuestionsByCourse(course);
            return getQuestionResponses(questions);
        }
    }

    private List<GetAllQuestionResponse> getQuestionResponses(List<Questions> questions) {

        List<GetAllQuestionResponse> responseList = new ArrayList<>();
        for (Questions questionsObj : questions) {
            GetAllQuestionResponse response = new GetAllQuestionResponse();
            response.setCourseTitle(questionsObj.getCourse().getCourseTitle());
            response.setQuestionCode(questionsObj.getCode());
            response.setUserName(questionsObj.getGeneralUserProfile().getFirstName() + " " + questionsObj.getGeneralUserProfile().getLastName());
            response.setProfileImg(questionsObj.getGeneralUserProfile().getProfileImg());
            response.setQuestion(questionsObj.getQuestion());
            response.setIsRead(Integer.toString(questionsObj.getIsRead()));
            response.setAnswer(questionsObj.getAnswer());
            response.setDate(questionsObj.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter));
            responseList.add(response);
        }
        return responseList;
    }

    @Override
    public SuccessResponse addAnswer(AddAnswerRequest addAnswerRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getGupType().getId() == 2) {
                if (profile.getIsActive() == 1) {
                    if (addAnswerRequest.getQuestionCode() == null || addAnswerRequest.getQuestionCode().isEmpty()) {
                        throw new ErrorException("please add a question code", VarList.RSP_NO_DATA_FOUND);
                    }
                    if (addAnswerRequest.getAnswer() == null || addAnswerRequest.getAnswer().isEmpty()) {
                        throw new ErrorException("please add a answer", VarList.RSP_NO_DATA_FOUND);
                    }
                    Questions question = questionsRepository.getQuestionByCode(addAnswerRequest.getQuestionCode());
                    if (question == null) {
                        throw new ErrorException("The question related to the question code was not found", VarList.RSP_NO_DATA_FOUND);
                    } else {
                        question.setAnswer(addAnswerRequest.getAnswer());
                        question.setIsRead(1);
                        questionsRepository.save(question);

                        try {
                            String noficationCode = UUID.randomUUID().toString();
                            Properties properties;

                            EmailSender emailSender = new EmailSender();

                            Notification notification = new Notification();
                            notification.setNotificationCode(noficationCode);
                            notification.setNotification("Your question has been answered by the instructor.");
                            notification.setNotificationTime(new Date());
                            notification.setGeneralUserProfile(question.getGeneralUserProfile());
                            notification.setRead(false);
                            notificationRepository.save(notification);

                            properties = EmailConfig.getEmailProperties(question.getGeneralUserProfile().getFirstName() + " " + question.getGeneralUserProfile().getLastName(), "Your Question for " + question.getCourse().getCourseTitle() + " Has Been Answered.");
                            properties.put("courseTitle", question.getCourse().getCourseTitle());
                            emailSender.sendEmail("AddAnswerMessage", question.getGeneralUserProfile().getEmail(), (String) properties.getProperty("from"), (String) properties.get("subject"), properties);

                        } catch (MessagingException e) {
                            throw new ErrorException(e.getMessage(), VarList.RSP_NO_DATA_FOUND);
                        }

                        SuccessResponse successResponse = new SuccessResponse();
                        successResponse.setMessage("The answer to the question was successfully added");
                        successResponse.setVariable(VarList.RSP_SUCCESS);
                        return successResponse;
                    }

                } else {
                    throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not an instructor", VarList.RSP_NO_DATA_FOUND);
            }

        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public List<GetAnnouncementsResponse> getAnnouncementsByCourseCode(String courseCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                Course course = courseRepository.getCourseByCode(courseCode);
                if (course == null) {
                    throw new ErrorException("Course not found related to course code", VarList.RSP_NO_DATA_FOUND);
                }
                List<Announcements> announcementsList = announcementsRepository.getAnnouncementsByCourse(course);
                List<GetAnnouncementsResponse> responseList = new ArrayList<>();
                for (Announcements announcements : announcementsList) {
                    GetAnnouncementsResponse response = new GetAnnouncementsResponse();
                    response.setTittle(announcements.getTitle());
                    response.setContent(announcements.getContent());
                    response.setCreatedDate(announcements.getCreatedDate());
                    responseList.add(response);
                }
                return responseList;
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }

        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public List<GetQuestionByItemCodeResponse> getAllQuestionsByItemCode(String itemCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                try {
                    Course course = orderHasCourseRepository.getOrderHasCourseByItemCode(itemCode).getCourse();
                    List<Questions> questions = questionsRepository.getQuestionsByCourse(course);
                    List<GetQuestionByItemCodeResponse> responseList = new ArrayList<>();
                    for (Questions questionsObj : questions) {
                        GetQuestionByItemCodeResponse response = new GetQuestionByItemCodeResponse();
                        response.setQuestionCode(questionsObj.getCode());
                        response.setUserName(questionsObj.getGeneralUserProfile().getFirstName() + " " + questionsObj.getGeneralUserProfile().getLastName());
                        response.setProfileImg(questionsObj.getGeneralUserProfile().getProfileImg());
                        response.setQuestion(questionsObj.getQuestion());
                        response.setAnswer(questionsObj.getAnswer());
                        response.setDate(questionsObj.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter));
                        responseList.add(response);
                    }
                    return responseList;
                } catch (NullPointerException e) {
                    throw new ErrorException("Course not found related to order_has_course's itemCode", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }

        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }
}
