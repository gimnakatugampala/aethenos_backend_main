package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.CourseMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseMessageRepository extends JpaRepository<CourseMessage, Integer> {
    CourseMessage getCourseMessageByCourseId(int id);
}