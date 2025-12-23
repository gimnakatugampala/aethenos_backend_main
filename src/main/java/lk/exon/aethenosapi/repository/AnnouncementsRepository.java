package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.Announcements;
import lk.exon.aethenosapi.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementsRepository  extends JpaRepository<Announcements, Integer> {
    List<Announcements> getAnnouncementsByCourse(Course course);
}
