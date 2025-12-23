package lk.exon.aethenosapi.controller;

import lk.exon.aethenosapi.payload.request.ChatRequest;
import lk.exon.aethenosapi.payload.response.*;
import lk.exon.aethenosapi.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "chat")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ChatController {

    @Autowired
    ChatService chatService;

    @GetMapping("/getInstructors")
    public List<ProfileResponse> getInstructors(){
        return chatService.getInstructors();
    }

    @GetMapping("/getStudents")
    public List<ProfileResponse> getStudents(){
        return chatService.getStudents();
    }

    @PostMapping("/sendChat")
    public SuccessResponse sendChat(ChatRequest chatRequest){
        return chatService.sendChat(chatRequest);
    }
      @GetMapping("/getInstructorsToPurchasedCourses")
    public List<InstructorDetailsResponse> getInstructorsToPurchasedCourses(){
        return chatService.getInstructorsToPurchasedCourses();
    }
    @GetMapping("/getChatRoomDetailsByStudent")
    public GetMessageByStudentResponse getChatRoomDetailsByStudent(){
        return chatService.getChatRoomDetailsByStudent();
    }
    @GetMapping("/getChatRoomDetailsByInstructor")
    public GetMessageByInstructorResponse getChatRoomDetailsByInstructor(){
        return chatService.getChatRoomDetailsByInstructor();
    }
    @GetMapping("/getChatRoomDetailsByStudentUsingChatRoomCode/{chatRoomCode}")
    public List<GetMessagesResponse> getChatRoomDetailsByStudentUsingChatRoomCode(@PathVariable String chatRoomCode){
        return chatService.getChatRoomDetailsByStudentUsingChatRoomCode(chatRoomCode);
    }
    @GetMapping("/getChatRoomDetailsByInstructorUsingChatRoomCode/{chatRoomCode}")
    public List<GetMessagesResponse> getChatRoomDetailsByInstructorUsingChatRoomCode(@PathVariable String chatRoomCode){
        return chatService.getChatRoomDetailsByInstructorUsingChatRoomCode(chatRoomCode);
    }
    @GetMapping("/getStudentChatRoom")
    public GetStudentChatRoomResponse getStudentChatRoom(){
        return chatService.getStudentChatRoom();
    }
    @GetMapping("/getInstructorChatRoom")
    public GetInstructorChatRoomResponse getInstructorChatRoom(){
        return chatService.getInstructorChatRoom();
    }

}
