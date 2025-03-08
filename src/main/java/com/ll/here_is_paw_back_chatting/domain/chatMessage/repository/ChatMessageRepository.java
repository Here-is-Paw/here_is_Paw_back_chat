package com.ll.here_is_paw_back_chatting.domain.chatMessage.repository;

import com.ll.here_is_paw_back_chatting.domain.chatMessage.document.ChatMessage;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
  List<ChatMessage> findByRoomIdOrderByTimestampAsc(String roomId);
  long countByRoomIdAndRecipientIdAndReadFalse(String roomId, String userId);


}

