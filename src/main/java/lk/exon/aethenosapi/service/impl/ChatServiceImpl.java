package lk.exon.aethenosapi.service.impl;

import lk.exon.aethenosapi.config.EmailConfig;
import lk.exon.aethenosapi.entity.*;
import lk.exon.aethenosapi.exception.ErrorException;
import lk.exon.aethenosapi.payload.request.ChatRequest;
import lk.exon.aethenosapi.payload.request.GetCourseInfoRequest;
import lk.exon.aethenosapi.payload.response.*;
import lk.exon.aethenosapi.repository.*;
import lk.exon.aethenosapi.service.ChatService;
import lk.exon.aethenosapi.utils.EmailSender;
import lk.exon.aethenosapi.utils.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.*;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private UserProfileServiceImpl userProfileServiceImpl;
    @Autowired
    private GeneralUserProfileRepository generalUserProfileRepository;
    @Autowired
    private GupTypeRepository gupTypeRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderHasCourseRepository orderHasCourseRepository;
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public List<ProfileResponse> getInstructors() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);

        if (profile != null) {
            if (profile.getIsActive() == 1) {

                List<ProfileResponse> responseList = new ArrayList<>();
                GupType gupType = gupTypeRepository.getGupTypeById(2);
                List<GeneralUserProfile> generalUserProfileList = generalUserProfileRepository.getGeneralUserProfileByGupType(gupType);
                if (generalUserProfileList.size() > 0) {

                    for (GeneralUserProfile generalUserProfile : generalUserProfileList) {
                        ProfileResponse response = new ProfileResponse();

                        response.setId(generalUserProfile.getId());
                        response.setEmail(generalUserProfile.getEmail());
                        response.setCode(generalUserProfile.getUserCode());
                        response.setFname(generalUserProfile.getFirstName());
                        response.setLname(generalUserProfile.getLastName());

                        responseList.add(response);
                    }

                    return responseList;

                } else {
                    throw new ErrorException("No data found", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse sendChat(ChatRequest chatRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);

        if (profile != null) {
            if (profile.getIsActive() == 1) {

                String message = chatRequest.getMessage();
                String course_code = chatRequest.getCourseCode();
                String to_user_code = chatRequest.getToUserCode();
                Integer to_user_type_id;
                Integer from_user_type_id;
                String chat_room_code = null;
                GeneralUserProfile instructor;
                GeneralUserProfile student;

                if (message != null || course_code != null || to_user_code != null) {

                    Course course = courseRepository.getCourseByCode(course_code);

                    if (course != null) {

                        GeneralUserProfile generalUserProfile = generalUserProfileRepository.getGeneralUserProfileByUserCode(to_user_code);

                        if (generalUserProfile != null) {

                            if (generalUserProfile.getUserCode().equals(profile.getUserCode())) {
                                throw new ErrorException("You cannot send messages to yourself because your user code and toUserCode are the same", VarList.RSP_NO_DATA_FOUND);
                            }

                            if (course.getInstructorId().getGeneralUserProfile().getUserCode().equals(generalUserProfile.getUserCode())) {
                                to_user_type_id = 2;
                                from_user_type_id = 1;
                                instructor = generalUserProfile;
                                student = profile;
                            } else if (course.getInstructorId().getGeneralUserProfile().getUserCode().equals(profile.getUserCode())) {
                                to_user_type_id = 1;
                                from_user_type_id = 2;
                                instructor = profile;
                                student = generalUserProfile;
                            } else {
                                throw new ErrorException("Invalid course code, This course is not owned by this instructor", VarList.RSP_NO_DATA_FOUND);
                            }

                            ChatRoom chatRoom = chatRoomRepository.findByInstructorAndStudentAndCourse(instructor, student, course);

                            if (chatRoom == null) {
                                chat_room_code = UUID.randomUUID().toString();
                                chatRoom = new ChatRoom();
                                chatRoom.setChatCode(chat_room_code);
                                chatRoom.setCourse(course);
                                chatRoom.setLastMessage(message);
                                chatRoom.setLastSeen(new Date());
                                chatRoom.setInstructor(instructor);
                                chatRoom.setStudent(student);


                                try {
                                    Properties properties;

                                    EmailSender emailSender = new EmailSender();

                                    properties = EmailConfig.getEmailProperties(generalUserProfile.getFirstName() + " " + generalUserProfile.getLastName(), "New Message Notification.");
                                    properties.put("senderName", profile.getFirstName()+" "+profile.getLastName());
                                    properties.put("courseTitle",course.getCourseTitle());
                                    emailSender.sendEmail("NewMessageNotification", generalUserProfile.getEmail(), (String) properties.getProperty("from"), (String) properties.get("subject"), properties);

                                } catch (MessagingException e) {
                                    throw new ErrorException(e.getMessage(), VarList.RSP_NO_DATA_FOUND);
                                }


                            } else {
                                chatRoom.setLastMessage(message);
                                chatRoom.setLastSeen(new Date());
                            }
                            chatRoomRepository.save(chatRoom);

                            GupType toGupType = gupTypeRepository.getGupTypeById(to_user_type_id);
                            GupType fromGupType = gupTypeRepository.getGupTypeById(from_user_type_id);
                            if (toGupType == null || fromGupType == null) {
                                throw new ErrorException("Invalid user types", VarList.RSP_NO_DATA_FOUND);
                            }
                            saveChat(message, chatRoom, profile, generalUserProfile, toGupType, fromGupType);


                            SuccessResponse successResponse = new SuccessResponse();
                            successResponse.setMessage("Message sent successfully");
                            successResponse.setVariable(VarList.RSP_SUCCESS);
                            return successResponse;

                        } else {
                            throw new ErrorException("No user found", VarList.RSP_NO_DATA_FOUND);
                        }

                    } else {
                        throw new ErrorException("No course found", VarList.RSP_NO_DATA_FOUND);
                    }

                } else {
                    throw new ErrorException("All fields are required", VarList.RSP_NO_DATA_FOUND);
                }

            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    private void saveChat(String message, ChatRoom chatRoom, GeneralUserProfile from, GeneralUserProfile to, GupType toUserType, GupType fromUserType) {
        Chat chat = new Chat();
        chat.setMessage(message);
        chat.setChatRoom(chatRoom);
        chat.setFromGeneralUserProfile(from);
        chat.setToGeneralUserProfile(to);
        chat.setToGupType(toUserType);
        chat.setFromGupType(fromUserType);
        chat.setSend_date(new Date());
        chat.setIsRead((byte) 0);
        chatRepository.save(chat);

        Notification notification = new Notification();
        notification.setNotification("You have a new message, please check your inbox");
        notification.setNotificationTime(new Date());
        notification.setGeneralUserProfile(to);
        notification.setRead(false);
        notificationRepository.save(notification);

    }

    @Override
    public List<ProfileResponse> getStudents() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);

        if (profile != null) {
            if (profile.getIsActive() == 1) {

                List<ProfileResponse> responseList = new ArrayList<>();
                GupType gupType = gupTypeRepository.getGupTypeById(1);
                List<GeneralUserProfile> generalUserProfileList = generalUserProfileRepository.getGeneralUserProfileByGupType(gupType);
                if (generalUserProfileList.size() > 0) {

                    for (GeneralUserProfile generalUserProfile : generalUserProfileList) {
                        ProfileResponse response = new ProfileResponse();

                        response.setId(generalUserProfile.getId());
                        response.setEmail(generalUserProfile.getEmail());
                        response.setCode(generalUserProfile.getUserCode());
                        response.setFname(generalUserProfile.getFirstName());
                        response.setLname(generalUserProfile.getLastName());

                        responseList.add(response);
                    }

                    return responseList;

                } else {
                    throw new ErrorException("No data found", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public List<InstructorDetailsResponse> getInstructorsToPurchasedCourses() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
            if (profile != null && profile.getIsActive() == 1) {
                List<Order> orders = orderRepository.getOrdersByGeneralUserProfile(profile);
                Set<InstructorProfile> instructorProfiles = new HashSet<>();
                List<InstructorDetailsResponse> instructorDetailsResponses = new ArrayList<>();
                for (Order order : orders) {
                    List<OrderHasCourse> orderHasCourses = orderHasCourseRepository.getOrderHasCoursesByOrder(order);
                    for (OrderHasCourse orderHasCourse : orderHasCourses) {
                        instructorProfiles.add(orderHasCourse.getCourse().getInstructorId());
                    }
                    for (InstructorProfile instructorProfile : instructorProfiles) {
                        InstructorDetailsResponse instructorDetailsResponse = new InstructorDetailsResponse();
                        GeneralUserProfile instructorUserProfile = instructorProfile.getGeneralUserProfile();
                        instructorDetailsResponse.setName(instructorUserProfile.getFirstName() + " " + instructorUserProfile.getLastName());
                        instructorDetailsResponse.setUserCode(instructorUserProfile.getUserCode());
                        instructorDetailsResponse.setProfileImg(instructorUserProfile.getProfileImg());

                        List<GetCourseInfoRequest> getCourseInfoRequests = new ArrayList<>();
                        List<Course> courses = courseRepository.getCourseByInstructorId(instructorProfile);
                        for (Course course : courses) {
                            try {
                                OrderHasCourse orderHasCourse = orderHasCourseRepository.getOrderHasCoursesByCourseAndOrder(course, order);
                                GetCourseInfoRequest getCourseInfoRequest = new GetCourseInfoRequest();
                                getCourseInfoRequest.setCourseCode(orderHasCourse.getCourse().getCode());
                                getCourseInfoRequest.setCourseName(orderHasCourse.getCourse().getCourseTitle());
                                getCourseInfoRequests.add(getCourseInfoRequest);
                            } catch (NullPointerException ex) {
                                ex.printStackTrace();
                            }

                        }
                        instructorDetailsResponse.setCoursesDetails(getCourseInfoRequests);
                        instructorDetailsResponses.add(instructorDetailsResponse);
                    }
                }
                return instructorDetailsResponses;
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not authenticated", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public GetMessageByStudentResponse getChatRoomDetailsByStudent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);

        if (profile == null) {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
        if (profile.getIsActive() != 1) {
            throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
        }

        List<ChatRoom> chatRooms = chatRoomRepository.getChatRoomByStudent(profile);
        GetMessageByStudentResponse getMessageByStudentResponse = new GetMessageByStudentResponse();

        getMessageByStudentResponse.setStudent(profile.getFirstName() + " " + profile.getLastName());
        getMessageByStudentResponse.setStudentUserCode(profile.getUserCode());
        getMessageByStudentResponse.setStudentProfileImg(profile.getProfileImg());

        List<GetChatRoomDetailsByStudentResponse> getChatRoomDetailsByStudentResponses = new ArrayList<>();
        GetChatRoomDetailsByStudentResponse getChatRoomDetailsByStudentResponse;
        for (ChatRoom chatRoom : chatRooms) {
            getChatRoomDetailsByStudentResponse = new GetChatRoomDetailsByStudentResponse();
            getChatRoomDetailsByStudentResponse.setChatRoomCode(chatRoom.getChatCode());

            getChatRoomDetailsByStudentResponse.setCourseTitle(chatRoom.getCourse().getCourseTitle());
            getChatRoomDetailsByStudentResponse.setCourseCode(chatRoom.getCourse().getCode());

            getChatRoomDetailsByStudentResponse.setInstructor(chatRoom.getInstructor().getFirstName() + " " + chatRoom.getInstructor().getLastName());
            getChatRoomDetailsByStudentResponse.setInstructorUserCode(chatRoom.getInstructor().getUserCode());
            getChatRoomDetailsByStudentResponse.setInstructorProfileImg(chatRoom.getInstructor().getProfileImg());

            getChatRoomDetailsByStudentResponse.setLastMessage(chatRoom.getLastMessage());
            getChatRoomDetailsByStudentResponse.setLastSeen(chatRoom.getLastSeen());
            List<GetMessagesResponse> getMessagesResponses = new ArrayList<>();
            List<Chat> chats = chatRepository.getChatByChatRoom(chatRoom);
            GetMessagesResponse getMessagesResponse;
            for (Chat chat : chats) {
                getMessagesResponse = new GetMessagesResponse();
                getMessagesResponse.setTo(chat.getToGeneralUserProfile().getFirstName() + " " + chat.getToGeneralUserProfile().getLastName());

                if (chat.getToGeneralUserProfile().getUserCode().equals(profile.getUserCode()) && !chat.getFromGeneralUserProfile().getUserCode().equals(profile.getUserCode())) {
                    getMessagesResponse.setToType("Student");
                    getMessagesResponse.setFromType("Instructor");
                } else {
                    getMessagesResponse.setToType("Instructor");
                    getMessagesResponse.setFromType("Student");
                }
                getMessagesResponse.setFrom(chat.getFromGeneralUserProfile().getFirstName() + " " + chat.getFromGeneralUserProfile().getLastName());

                getMessagesResponse.setMessage(chat.getMessage());
                getMessagesResponse.setTime(chat.getSend_date());
                getMessagesResponse.setRead(chat.getIsRead() == 0 ? false : true);
                getMessagesResponses.add(getMessagesResponse);
            }
            getChatRoomDetailsByStudentResponse.setMessages(getMessagesResponses);
            getChatRoomDetailsByStudentResponses.add(getChatRoomDetailsByStudentResponse);
        }
        getMessageByStudentResponse.setChatRoomDetails(getChatRoomDetailsByStudentResponses);

        return getMessageByStudentResponse;
    }

    @Override
    public GetMessageByInstructorResponse getChatRoomDetailsByInstructor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);

        if (profile == null) {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
        if (profile.getIsActive() != 1) {
            throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
        }

        List<ChatRoom> chatRooms = chatRoomRepository.getChatRoomByInstructor(profile);
        GetMessageByInstructorResponse getMessageByInstructorResponse = new GetMessageByInstructorResponse();

        getMessageByInstructorResponse.setInstructor(profile.getFirstName() + " " + profile.getLastName());
        getMessageByInstructorResponse.setInstructorUserCode(profile.getUserCode());
        getMessageByInstructorResponse.setInstructorProfileImg(profile.getProfileImg());

        List<GetChatRoomDetailsByInstructorResponse> getChatRoomDetailsByInstructorResponses = new ArrayList<>();
        GetChatRoomDetailsByInstructorResponse getChatRoomDetailsByInstructorResponse;
        for (ChatRoom chatRoom : chatRooms) {
            getChatRoomDetailsByInstructorResponse = new GetChatRoomDetailsByInstructorResponse();
            getChatRoomDetailsByInstructorResponse.setChatRoomCode(chatRoom.getChatCode());

            getChatRoomDetailsByInstructorResponse.setCourseTitle(chatRoom.getCourse().getCourseTitle());
            getChatRoomDetailsByInstructorResponse.setCourseCode(chatRoom.getCourse().getCode());

            getChatRoomDetailsByInstructorResponse.setStudent(chatRoom.getStudent().getFirstName() + " " + chatRoom.getStudent().getLastName());
            getChatRoomDetailsByInstructorResponse.setStudentUserCode(chatRoom.getStudent().getUserCode());
            getChatRoomDetailsByInstructorResponse.setStudentProfileImg(chatRoom.getStudent().getProfileImg());

            getChatRoomDetailsByInstructorResponse.setLastMessage(chatRoom.getLastMessage());
            getChatRoomDetailsByInstructorResponse.setLastSeen(chatRoom.getLastSeen());
            List<GetMessagesResponse> getMessagesResponses = new ArrayList<>();
            List<Chat> chats = chatRepository.getChatByChatRoom(chatRoom);
            GetMessagesResponse getMessagesResponse;
            for (Chat chat : chats) {
                getMessagesResponse = new GetMessagesResponse();
                getMessagesResponse.setTo(chat.getToGeneralUserProfile().getFirstName() + " " + chat.getToGeneralUserProfile().getLastName());

                if (chat.getToGeneralUserProfile().getUserCode().equals(profile.getUserCode()) && !chat.getFromGeneralUserProfile().getUserCode().equals(profile.getUserCode())) {
                    getMessagesResponse.setToType("Instructor");
                    getMessagesResponse.setFromType("Student");
                } else {
                    getMessagesResponse.setToType("Student");
                    getMessagesResponse.setFromType("Instructor");
                }
                getMessagesResponse.setFrom(chat.getFromGeneralUserProfile().getFirstName() + " " + chat.getFromGeneralUserProfile().getLastName());

                getMessagesResponse.setMessage(chat.getMessage());
                getMessagesResponse.setTime(chat.getSend_date());
                getMessagesResponse.setRead(chat.getIsRead() == 0 ? false : true);
                getMessagesResponses.add(getMessagesResponse);
            }
            getChatRoomDetailsByInstructorResponse.setMessages(getMessagesResponses);
            getChatRoomDetailsByInstructorResponses.add(getChatRoomDetailsByInstructorResponse);
        }
        getMessageByInstructorResponse.setChatRoomDetails(getChatRoomDetailsByInstructorResponses);

        return getMessageByInstructorResponse;
    }

    @Override
    public List<GetMessagesResponse> getChatRoomDetailsByStudentUsingChatRoomCode(String chatRoomCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);

        if (profile == null) {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
        if (profile.getIsActive() != 1) {
            throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
        }

        ChatRoom chatRoom = chatRoomRepository.getChatRoomByChatCode(chatRoomCode);
        if (chatRoom == null) {
            throw new ErrorException("Invalid chat_room_code", VarList.RSP_NO_DATA_FOUND);
        }
        if (!profile.getUserCode().equals(chatRoom.getStudent().getUserCode())) {
            throw new ErrorException("You cannot see other people's messages", VarList.RSP_NO_DATA_FOUND);
        }

        List<GetMessagesResponse> getMessagesResponses = new ArrayList<>();
        List<Chat> chats = chatRepository.getChatByChatRoom(chatRoom);
        GetMessagesResponse getMessagesResponse;
        for (Chat chat : chats) {
            getMessagesResponse = new GetMessagesResponse();
            getMessagesResponse.setTo(chat.getToGeneralUserProfile().getFirstName() + " " + chat.getToGeneralUserProfile().getLastName());
            if (chat.getToGeneralUserProfile().getUserCode().equals(profile.getUserCode()) && !chat.getFromGeneralUserProfile().getUserCode().equals(profile.getUserCode())) {
                getMessagesResponse.setToType("Student");
                getMessagesResponse.setFromType("Instructor");
            } else {
                getMessagesResponse.setToType("Instructor");
                getMessagesResponse.setFromType("Student");
            }
            getMessagesResponse.setFrom(chat.getFromGeneralUserProfile().getFirstName() + " " + chat.getFromGeneralUserProfile().getLastName());

            getMessagesResponse.setMessage(chat.getMessage());
            getMessagesResponse.setTime(chat.getSend_date());
            getMessagesResponse.setRead(chat.getIsRead() == 0 ? false : true);
            getMessagesResponses.add(getMessagesResponse);
        }

        return getMessagesResponses;
    }

    @Override
    public List<GetMessagesResponse> getChatRoomDetailsByInstructorUsingChatRoomCode(String chatRoomCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);

        if (profile == null) {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
        if (profile.getIsActive() != 1) {
            throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
        }

        ChatRoom chatRoom = chatRoomRepository.getChatRoomByChatCode(chatRoomCode);
        if (chatRoom == null) {
            throw new ErrorException("Invalid chat_room_code", VarList.RSP_NO_DATA_FOUND);
        }
        if (!profile.getUserCode().equals(chatRoom.getInstructor().getUserCode())) {
            throw new ErrorException("You cannot see other people's messages", VarList.RSP_NO_DATA_FOUND);
        }

        List<GetMessagesResponse> getMessagesResponses = new ArrayList<>();
        List<Chat> chats = chatRepository.getChatByChatRoom(chatRoom);
        GetMessagesResponse getMessagesResponse;
        for (Chat chat : chats) {
            getMessagesResponse = new GetMessagesResponse();
            getMessagesResponse.setTo(chat.getToGeneralUserProfile().getFirstName() + " " + chat.getToGeneralUserProfile().getLastName());

            if (chat.getToGeneralUserProfile().getUserCode().equals(profile.getUserCode()) && !chat.getFromGeneralUserProfile().getUserCode().equals(profile.getUserCode())) {
                getMessagesResponse.setToType("Instructor");
                getMessagesResponse.setFromType("Student");
            } else {
                getMessagesResponse.setToType("Student");
                getMessagesResponse.setFromType("Instructor");
            }
            getMessagesResponse.setFrom(chat.getFromGeneralUserProfile().getFirstName() + " " + chat.getFromGeneralUserProfile().getLastName());

            getMessagesResponse.setMessage(chat.getMessage());
            getMessagesResponse.setTime(chat.getSend_date());
            getMessagesResponse.setRead(chat.getIsRead() == 0 ? false : true);
            getMessagesResponses.add(getMessagesResponse);
        }

        return getMessagesResponses;
    }

    @Override
    public GetStudentChatRoomResponse getStudentChatRoom() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);

        if (profile == null) {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
        if (profile.getIsActive() != 1) {
            throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
        }

        List<ChatRoom> chatRooms = chatRoomRepository.getChatRoomByStudent(profile);
        GetStudentChatRoomResponse getStudentChatRoomResponse = new GetStudentChatRoomResponse();

        getStudentChatRoomResponse.setStudent(profile.getFirstName() + " " + profile.getLastName());
        getStudentChatRoomResponse.setStudentUserCode(profile.getUserCode());
        getStudentChatRoomResponse.setStudentProfileImg(profile.getProfileImg());

        List<GetStudentChatRoomInfoResponse> getStudentChatRoomInfoResponses = new ArrayList<>();
        GetStudentChatRoomInfoResponse getStudentChatRoomInfoResponse;
        for (ChatRoom chatRoom : chatRooms) {
            getStudentChatRoomInfoResponse = new GetStudentChatRoomInfoResponse();
            getStudentChatRoomInfoResponse.setCourseTitle(chatRoom.getCourse().getCourseTitle());
            getStudentChatRoomInfoResponse.setCourseCode(chatRoom.getCourse().getCode());
            getStudentChatRoomInfoResponse.setChatRoomCode(chatRoom.getChatCode());
            getStudentChatRoomInfoResponse.setInstructor(chatRoom.getInstructor().getFirstName() + " " + chatRoom.getInstructor().getLastName());
            getStudentChatRoomInfoResponse.setInstructorUserCode(chatRoom.getInstructor().getUserCode());
            getStudentChatRoomInfoResponse.setInstructorProfileImg(chatRoom.getInstructor().getProfileImg());
            getStudentChatRoomInfoResponse.setLastMessage(chatRoom.getLastMessage());
            getStudentChatRoomInfoResponse.setLastSeen(chatRoom.getLastSeen());

            getStudentChatRoomInfoResponses.add(getStudentChatRoomInfoResponse);
        }
        getStudentChatRoomResponse.setChatRoomInfo(getStudentChatRoomInfoResponses);

        return getStudentChatRoomResponse;
    }

    @Override
    public GetInstructorChatRoomResponse getInstructorChatRoom() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);

        if (profile == null) {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
        if (profile.getIsActive() != 1) {
            throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
        }

        List<ChatRoom> chatRooms = chatRoomRepository.getChatRoomByInstructor(profile);
        GetInstructorChatRoomResponse getInstructorChatRoomResponse = new GetInstructorChatRoomResponse();

        getInstructorChatRoomResponse.setInstructor(profile.getFirstName() + " " + profile.getLastName());
        getInstructorChatRoomResponse.setInstructorUserCode(profile.getUserCode());
        getInstructorChatRoomResponse.setInstructorProfileImg(profile.getProfileImg());

        List<GetInstructorChatRoomInfoResponse> getInstructorChatRoomInfoResponses = new ArrayList<>();
        GetInstructorChatRoomInfoResponse getInstructorChatRoomInfoResponse;
        for (ChatRoom chatRoom : chatRooms) {
            getInstructorChatRoomInfoResponse = new GetInstructorChatRoomInfoResponse();
            getInstructorChatRoomInfoResponse.setCourseTitle(chatRoom.getCourse().getCourseTitle());
            getInstructorChatRoomInfoResponse.setCourseCode(chatRoom.getCourse().getCode());
            getInstructorChatRoomInfoResponse.setChatRoomCode(chatRoom.getChatCode());
            getInstructorChatRoomInfoResponse.setStudent(chatRoom.getStudent().getFirstName() + " " + chatRoom.getStudent().getLastName());
            getInstructorChatRoomInfoResponse.setStudentUserCode(chatRoom.getStudent().getUserCode());
            getInstructorChatRoomInfoResponse.setStudentProfileImg(chatRoom.getStudent().getProfileImg());
            getInstructorChatRoomInfoResponse.setLastMessage(chatRoom.getLastMessage());
            getInstructorChatRoomInfoResponse.setLastSeen(chatRoom.getLastSeen());

            getInstructorChatRoomInfoResponses.add(getInstructorChatRoomInfoResponse);
        }
        getInstructorChatRoomResponse.setChatRoomInfo(getInstructorChatRoomInfoResponses);

        return getInstructorChatRoomResponse;
    }

}
