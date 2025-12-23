package lk.exon.aethenosapi.controller;

import lk.exon.aethenosapi.payload.request.AddAnnouncementRequest;
import lk.exon.aethenosapi.payload.request.AddAnswerRequest;
import lk.exon.aethenosapi.payload.request.AddQuestionRequest;
import lk.exon.aethenosapi.payload.response.*;
import lk.exon.aethenosapi.service.CommunicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "communication")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CommunicationController {
    @Autowired
    private CommunicationService communicationService;

    @PostMapping("/addAnnouncements")
    public SuccessResponse addAnnouncements(AddAnnouncementRequest addAnnouncementRequest) {
        return communicationService.addAnnouncements(addAnnouncementRequest);
    }
    @PostMapping("/getAnnouncements")
    public List<GetAnnouncementsResponse> getAnnouncements(@RequestParam String courseCode) {
        return communicationService.getAnnouncements(courseCode);
    }
    @PostMapping("/addQuestion")
    public SuccessResponse addQuestion(AddQuestionRequest addQuestionRequest) {
        return communicationService.addQuestion(addQuestionRequest);
    }
    @PostMapping("/getAllQuestions")
    public List<GetAllQuestionResponse> getAllQuestions(@RequestParam String courseCode){
        return communicationService.getAllQuestions(courseCode);
    }
    @PutMapping("/addAnswer")
    public SuccessResponse addAnswer(AddAnswerRequest addAnswerRequest) {
        return communicationService.addAnswer(addAnswerRequest);
    }
    @GetMapping("/getAnnouncementsByCourseCode/{courseCode}")
    public List<GetAnnouncementsResponse> getAnnouncementsByCourseCode(@PathVariable("courseCode")String courseCode) {
        return  communicationService.getAnnouncementsByCourseCode(courseCode);
    }
    @GetMapping("/getAllQuestionsByItemCode/{itemCode}")
    public List<GetQuestionByItemCodeResponse> getAllQuestionsByItemCode(@PathVariable("itemCode")String itemCode) {
        return  communicationService.getAllQuestionsByItemCode(itemCode);
    }
}
