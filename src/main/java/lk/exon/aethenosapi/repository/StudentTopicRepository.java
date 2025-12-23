package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.GeneralUserProfile;
import lk.exon.aethenosapi.entity.StudentTopic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentTopicRepository extends JpaRepository<StudentTopic,Integer> {
    List<StudentTopic> getStudentTopicByGeneralUserProfile(GeneralUserProfile profile);
}
