package lk.exon.aethenosapi.service;


import lk.exon.aethenosapi.payload.response.*;

import lk.exon.aethenosapi.payload.response.GetCoursesDataResponse;
import lk.exon.aethenosapi.payload.response.GetTopRatedCoursesWithSubCategoryByTopicResponse;

import java.util.List;

public interface DisplayCourseService {
    List<GetCoursesDataResponse> searchCourses(String keyword);

    List<GetCoursesDataResponse> searchNewCourses(String keyword);


    List<GetCategoryByTopicResponse> getRelatedCategoriesByTopicLinkName(String topicLinkName);


    List<GetCoursesDataResponse> GetBeginnerFavoritesCoursesByTopicLinkName(String topicLinkName);

    GetTopRatedCoursesWithSubCategoryByTopicResponse getTopSubCategoryCoursesByTopicLinkName(String topicLinkName);

    List<GetCoursesDataResponse> getAllCourses();

    List<GetCourseContentResponse> getCourseContent(String courseCode);

    GetTopicCategorySubCategoryByTopicResponse getTopicCategorySubCategoryByTopic(String topicLinkName);

    List<GetCoursesDataResponse> GetLimitedCountCoursesForHomeByLinkName(String linkName);

    List<TopicResponse> getRelatedTopicsByTopicLinkName(String topicLinkName);

    List<GetCoursesDataResponse> getAllFreeCourses();
}
