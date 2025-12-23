package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.Chat;
import lk.exon.aethenosapi.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Integer> {

    List<Chat> getChatByChatRoom(ChatRoom chatRoom);
}