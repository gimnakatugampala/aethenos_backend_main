package lk.exon.aethenosapi.service;

import lk.exon.aethenosapi.payload.request.AddAnnouncementRequest;
import lk.exon.aethenosapi.payload.request.AddAnswerRequest;
import lk.exon.aethenosapi.payload.request.AddQuestionRequest;
import lk.exon.aethenosapi.payload.response.*;

import java.util.List;

public interface CommunicationService {
    SuccessResponse addAnnouncements(AddAnnouncementRequest addAnnouncementRequest);

    List<GetAnnouncementsResponse> getAnnouncements(String courseCode);

    SuccessResponse addQuestion(AddQuestionRequest addQuestionRequest);

    List<GetAllQuestionResponse> getAllQuestions(String courseCode);

    SuccessResponse addAnswer(AddAnswerRequest addAnswerRequest);

    List<GetAnnouncementsResponse> getAnnouncementsByCourseCode(String courseCode);

    List<GetQuestionByItemCodeResponse> getAllQuestionsByItemCode(String itemCode);
}
