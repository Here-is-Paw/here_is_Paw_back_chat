package com.ll.here_is_paw_back_chatting.domain.chatRoom.controller;


import com.ll.here_is_paw_back_chatting.domain.chatMessage.document.ChatMessage;
import com.ll.here_is_paw_back_chatting.domain.chatMessage.service.ChatMessageService;
import com.ll.here_is_paw_back_chatting.domain.chatRoom.entity.ChatRoom;
import com.ll.here_is_paw_back_chatting.domain.chatRoom.service.ChatRoomService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chatRooms")
public class ChatRoomController {

  private final ChatRoomService chatRoomService;


  @PostMapping
  @ResponseBody
  public ChatRoom createChatRoom(@RequestParam String user1Id, @RequestParam String user2Id) {
    return chatRoomService.createChatRoom(user1Id, user2Id);
  }

  @GetMapping("/{userId}")
  @ResponseBody
  public List<ChatRoom> getUserChatRooms(@PathVariable String userId) {
    return chatRoomService.getUserChatRooms(userId);
  }
}
