package com.ll.here_is_paw_back_chatting.domain.chatRoom.repository;

import com.ll.here_is_paw_back_chatting.domain.chatRoom.entity.ChatRoom;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {

  Optional<ChatRoom> findById(String roomId);

  List<ChatRoom> findByUser1IdOrUser2Id(String user1Id, String user2Id);

}
