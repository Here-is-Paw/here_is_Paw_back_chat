package com.ll.here_is_paw_back_chatting.domain.chatMessage.service;

import com.ll.here_is_paw_back_chatting.domain.chatMessage.document.ChatMessage;
import com.ll.here_is_paw_back_chatting.domain.chatMessage.repository.ChatMessageRepository;
import com.ll.here_is_paw_back_chatting.domain.chatRoom.entity.ChatRoom;
import com.ll.here_is_paw_back_chatting.domain.chatRoom.repository.ChatRoomRepository;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

  private final ChatRoomRepository chatRoomRepository;

  private final ChatMessageRepository chatMessageRepository;

  public List<ChatMessage> getChatMessages(String roomId) {
    return chatMessageRepository.findByRoomIdOrderByTimestampAsc(roomId);
  }

  public ChatMessage saveMessage(ChatMessage message) {
    if (chatRoomRepository.existsById(message.getRoomId())) {
      message.setTimestamp(new Date());
      message.setRead(false);
      return chatMessageRepository.save(message);
    } else {
      throw new RuntimeException("Chat room not found: " + message.getRoomId());
    }
  }

  public void markMessagesAsRead(String roomId, String userId) {
    List<ChatMessage> unreadMessages = chatMessageRepository.findByRoomIdOrderByTimestampAsc(roomId);

    for (ChatMessage message : unreadMessages) {
      if (!message.getSenderId().equals(userId) && !message.isRead()) {
        message.setRead(true);
        chatMessageRepository.save(message);
      }
    }
  }
}
