package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.CourseCategory;
import lk.exon.aethenosapi.entity.CourseSubCategory;
import lk.exon.aethenosapi.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Integer> {
    Topic getTopicById(Integer integer);

    List<Topic> getTopicsBySubCategory(CourseSubCategory subcategory);

    Topic getTopicByLinkName(String linkName);

    List<Topic> getTopicsByLinkName(String topicLinkName);

    List<Topic> getTopicsByLinkNameOrLinkName(String linkName1, String linkName2);
}
