package lk.exon.aethenosapi.service.impl;

import lk.exon.aethenosapi.entity.*;
import lk.exon.aethenosapi.exception.ErrorException;
import lk.exon.aethenosapi.payload.response.GetCoursesByInstructorResponse;
import lk.exon.aethenosapi.payload.response.StudentsEnrollResponse;
import lk.exon.aethenosapi.repository.*;
import lk.exon.aethenosapi.service.AnalyticsService;
import lk.exon.aethenosapi.utils.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    @Autowired
    private UserProfileServiceImpl userProfileServiceImpl;
    @Autowired
    private InstructorProfileRepository instructorProfileRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private OrderHasCourseRepository orderHasCourseRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private QuestionsRepository questionsRepository;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public List<GetCoursesByInstructorResponse> getCoursesByInstructor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {

                InstructorProfile instructorProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(profile);
                if (instructorProfile == null) {
                    throw new ErrorException("Instructor profile not available", VarList.RSP_NO_DATA_FOUND);
                }

                List<Course> courseList = courseRepository.getCourseByInstructorId(instructorProfile);
                List<GetCoursesByInstructorResponse> getCoursesByInstructorResponses = new ArrayList<>();

                if (courseList.size() > 0) {
                    for (Course course : courseList) {
                        GetCoursesByInstructorResponse getCoursesByInstructorResponse = new GetCoursesByInstructorResponse();
                        getCoursesByInstructorResponse.setCode(course.getCode());
                        getCoursesByInstructorResponse.setTitle(course.getCourseTitle());
                        getCoursesByInstructorResponses.add(getCoursesByInstructorResponse);
                    }
                }

                return getCoursesByInstructorResponses;

            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public List<StudentsEnrollResponse> getStudentsEnrollByCourse(String courseCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    List<StudentsEnrollResponse> studentsEnrollResponses = new ArrayList<>();

                    if (courseCode.equals("all")) {

                        InstructorProfile instructorProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(profile);
                        if (instructorProfile == null) {
                            throw new ErrorException("Invalid instructor profile", VarList.RSP_NO_DATA_FOUND);
                        }
                        List<Course> courseList = courseRepository.getCourseByInstructorId(instructorProfile);
                        for (Course course : courseList) {
                            List<OrderHasCourse> orderHasCourseList = orderHasCourseRepository.getOrderHasCoursesByCourse(course);
                            for (OrderHasCourse orderHasCourse : orderHasCourseList) {
                                StudentsEnrollResponse studentsEnrollResponse = new StudentsEnrollResponse();
                                studentsEnrollResponse.setCourseTitle(course.getCourseTitle());
                                studentsEnrollResponse.setStudentName(orderHasCourse.getOrder().getGeneralUserProfile().getFirstName() + " " + orderHasCourse.getOrder().getGeneralUserProfile().getLastName());
                                studentsEnrollResponse.setProfileImg((orderHasCourse.getOrder().getGeneralUserProfile().getProfileImg() == null || orderHasCourse.getOrder().getGeneralUserProfile().getProfileImg().isEmpty()) ? "" : orderHasCourse.getOrder().getGeneralUserProfile().getProfileImg());
                                studentsEnrollResponse.setEmail((orderHasCourse.getOrder().getGeneralUserProfile().getEmail() == null || orderHasCourse.getOrder().getGeneralUserProfile().getEmail().isEmpty()) ? "" : orderHasCourse.getOrder().getGeneralUserProfile().getEmail());
                                studentsEnrollResponse.setCountry((orderHasCourse.getOrder().getGeneralUserProfile().getCountry() == null || orderHasCourse.getOrder().getGeneralUserProfile().getCountry() == "") ? "" : orderHasCourse.getOrder().getGeneralUserProfile().getCountry());
                                studentsEnrollResponse.setEnrolledDate(orderHasCourse.getOrder().getBuyDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter));
                                studentsEnrollResponse.setLastVisited(orderHasCourse.getOrder().getBuyDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter));
                                studentsEnrollResponse.setProgress(orderHasCourse.getProgress());
                                List<Questions> questions = questionsRepository.getQuestionsByCourseAndGeneralUserProfile(course, orderHasCourse.getOrder().getGeneralUserProfile());
                                studentsEnrollResponse.setQuestionAskedCount(questions.size());
                                int answeredCount = 0;
                                for (Questions question : questions) {
                                    if (question.getAnswer() != null && !question.getAnswer().isEmpty()) {
                                        answeredCount++;
                                    }
                                }
                                studentsEnrollResponse.setQuestionsAnsweredCount(answeredCount);
                                studentsEnrollResponse.setStudentUserCode(orderHasCourse.getOrder().getGeneralUserProfile().getUserCode());

                                studentsEnrollResponses.add(studentsEnrollResponse);
                            }
                        }

                    } else {
                        Course course = courseRepository.getCourseByCode(courseCode);
                        if (course == null) {
                            throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);
                        }

                        List<OrderHasCourse> orderHasCourseList = orderHasCourseRepository.getOrderHasCoursesByCourse(course);
                        if (orderHasCourseList.size() == 0) {
                            throw new ErrorException("OrderHasCourse not found related to course", VarList.RSP_NO_DATA_FOUND);
                        }
                        for (OrderHasCourse orderHasCourse : orderHasCourseList) {
                            StudentsEnrollResponse studentsEnrollResponse = new StudentsEnrollResponse();
                            studentsEnrollResponse.setStudentName(orderHasCourse.getOrder().getGeneralUserProfile().getFirstName() + " " + orderHasCourse.getOrder().getGeneralUserProfile().getLastName());
                            studentsEnrollResponse.setProfileImg((orderHasCourse.getOrder().getGeneralUserProfile().getProfileImg() == null || orderHasCourse.getOrder().getGeneralUserProfile().getProfileImg().isEmpty()) ? "" : orderHasCourse.getOrder().getGeneralUserProfile().getProfileImg());
                            studentsEnrollResponse.setEmail((orderHasCourse.getOrder().getGeneralUserProfile().getEmail() == null || orderHasCourse.getOrder().getGeneralUserProfile().getEmail().isEmpty()) ? "" : orderHasCourse.getOrder().getGeneralUserProfile().getEmail());
                            studentsEnrollResponse.setCountry((orderHasCourse.getOrder().getGeneralUserProfile().getCountry() == null || orderHasCourse.getOrder().getGeneralUserProfile().getCountry() == "") ? "" : orderHasCourse.getOrder().getGeneralUserProfile().getCountry());
                            studentsEnrollResponse.setEnrolledDate(orderHasCourse.getOrder().getBuyDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter));
                            studentsEnrollResponse.setLastVisited(orderHasCourse.getOrder().getBuyDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter));
                            studentsEnrollResponse.setProgress(orderHasCourse.getProgress());
                            List<Questions> questions = questionsRepository.getQuestionsByCourseAndGeneralUserProfile(course, orderHasCourse.getOrder().getGeneralUserProfile());
                            studentsEnrollResponse.setQuestionAskedCount(questions.size());
                            int answeredCount = 0;
                            for (Questions question : questions) {
                                if (question.getAnswer() != null || !question.getAnswer().isEmpty()) {
                                    answeredCount++;
                                }
                            }
                            studentsEnrollResponse.setQuestionsAnsweredCount(answeredCount);
                            studentsEnrollResponse.setStudentUserCode(orderHasCourse.getOrder().getGeneralUserProfile().getUserCode());

                            studentsEnrollResponses.add(studentsEnrollResponse);
                        }
                    }

                    return studentsEnrollResponses;
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
