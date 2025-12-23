package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.ChatRoom;
import lk.exon.aethenosapi.entity.Course;
import lk.exon.aethenosapi.entity.GeneralUserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {
    ChatRoom getChatRoomByChatCode(String chatRoomCode);

    List<ChatRoom> getChatRoomByStudent(GeneralUserProfile profile);

    List<ChatRoom> getChatRoomByInstructor(GeneralUserProfile profile);

    ChatRoom findByInstructorAndStudentAndCourse(GeneralUserProfile instructor, GeneralUserProfile student, Course course);
}
