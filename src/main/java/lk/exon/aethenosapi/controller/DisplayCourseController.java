package lk.exon.aethenosapi.controller;

import lk.exon.aethenosapi.entity.Topic;
import lk.exon.aethenosapi.payload.response.*;
import lk.exon.aethenosapi.service.DisplayCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "displayCourse")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DisplayCourseController {

    @Autowired
    private DisplayCourseService displayCourseService;

    @GetMapping("/searchCourses/{keyword}")
    public List<GetCoursesDataResponse> searchCourses(@PathVariable("keyword") String keyword) {
        return displayCourseService.searchCourses(keyword);
    }

    @GetMapping("/searchNewCourses/{keyword}")
    public List<GetCoursesDataResponse> searchNewCourses(@PathVariable("keyword") String keyword) {
        return displayCourseService.searchNewCourses(keyword);
    }

    @GetMapping("/getBeginnerFavoritesCoursesByTopicLinkName/{topicLinkName}")  //pending...
    public List<GetCoursesDataResponse> GetBeginnerFavoritesCoursesByTopicLinkName(@PathVariable("topicLinkName") String topicLinkName) {
        return displayCourseService.GetBeginnerFavoritesCoursesByTopicLinkName(topicLinkName);
    }

    @GetMapping("/getRelatedCategoriesByTopicLinkName/{topic}")
    public List<GetCategoryByTopicResponse> getRelatedCategoriesByTopicLinkName(@PathVariable("topic") String topicLinkName) {
        return displayCourseService.getRelatedCategoriesByTopicLinkName(topicLinkName);
    }

    @GetMapping("/getTopSubCategoryCoursesByTopicLinkName/{topicLinkName}")
    public GetTopRatedCoursesWithSubCategoryByTopicResponse getTopSubCategoryCoursesByTopicLinkName(@PathVariable("topicLinkName") String topicLinkName) {
        return displayCourseService.getTopSubCategoryCoursesByTopicLinkName(topicLinkName);
    }

    @GetMapping("/getCourseContent/{courseCode}")
    public List<GetCourseContentResponse> getCourseContent(@PathVariable("courseCode") String courseCode) {
        return displayCourseService.getCourseContent(courseCode);
    }

    @GetMapping("/getAllCourses")
    public List<GetCoursesDataResponse> getAllCourses() {
        return displayCourseService.getAllCourses();
    }

    @GetMapping("/getTopicCategorySubCategoryByTopic/{topicLinkName}")
    public GetTopicCategorySubCategoryByTopicResponse getTopicCategorySubCategoryByTopic(@PathVariable("topicLinkName") String topicLinkName) {
        return displayCourseService.getTopicCategorySubCategoryByTopic(topicLinkName);
    }

    @GetMapping("/getLimitedCountCoursesForHomeByLinkName/{linkName}")
    public List<GetCoursesDataResponse> GetLimitedCountCoursesForHomeByLinkName(@PathVariable("linkName") String linkName) {
        return displayCourseService.GetLimitedCountCoursesForHomeByLinkName(linkName);
    }

    @GetMapping("/getRelatedTopicsByTopicLinkName/{topicLinkName}")
    public List<TopicResponse> getRelatedTopicsByTopicLinkName(@PathVariable("topicLinkName") String topicLinkName) {
        return displayCourseService.getRelatedTopicsByTopicLinkName(topicLinkName);
    }
    @GetMapping("/getAllFreeCourses")
    public List<GetCoursesDataResponse>  getAllFreeCourses() {
        return displayCourseService.getAllFreeCourses();
    }
}