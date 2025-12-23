package lk.exon.aethenosapi.service;

import lk.exon.aethenosapi.payload.request.ChatRequest;
import lk.exon.aethenosapi.payload.response.*;

import java.util.List;

public interface ChatService {
    List<ProfileResponse> getInstructors();

    SuccessResponse sendChat(ChatRequest chatRequest);

    List<ProfileResponse> getStudents();

    List<InstructorDetailsResponse> getInstructorsToPurchasedCourses();

    GetMessageByStudentResponse getChatRoomDetailsByStudent();

    GetMessageByInstructorResponse getChatRoomDetailsByInstructor();

    List<GetMessagesResponse> getChatRoomDetailsByStudentUsingChatRoomCode(String chatRoomCode);

    List<GetMessagesResponse> getChatRoomDetailsByInstructorUsingChatRoomCode(String chatRoomCode);

    GetStudentChatRoomResponse getStudentChatRoom();

    GetInstructorChatRoomResponse getInstructorChatRoom();
}
