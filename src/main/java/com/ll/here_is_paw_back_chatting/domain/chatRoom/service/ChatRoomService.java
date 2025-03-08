package com.ll.here_is_paw_back_chatting.domain.chatRoom.service;

import com.ll.here_is_paw_back_chatting.domain.chatRoom.entity.ChatRoom;
import com.ll.here_is_paw_back_chatting.domain.chatRoom.repository.ChatRoomRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

  private final ChatRoomRepository chatRoomRepository;

  @Transactional
  public ChatRoom createChatRoom(String user1Id, String user2Id) {
    String roomId = ChatRoom.generateRoomId(user1Id, user2Id);
    Optional<ChatRoom> existingRoom = chatRoomRepository.findById(roomId);

    if (existingRoom.isPresent()) {
      return existingRoom.get();
    }

    ChatRoom chatRoom = new ChatRoom();
    chatRoom.setId(roomId);
    chatRoom.setUser1Id(user1Id.compareTo(user2Id) < 0 ? user1Id : user2Id);
    chatRoom.setUser2Id(user1Id.compareTo(user2Id) < 0 ? user2Id : user1Id);

    return chatRoomRepository.save(chatRoom);
  }

  public List<ChatRoom> getUserChatRooms(String userId) {
    return chatRoomRepository.findByUser1IdOrUser2Id(userId, userId);
  }
}
