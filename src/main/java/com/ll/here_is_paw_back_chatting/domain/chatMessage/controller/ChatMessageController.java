package com.ll.here_is_paw_back_chatting.domain.chatMessage.controller;


import com.ll.here_is_paw_back_chatting.domain.chatMessage.document.ChatMessage;
import com.ll.here_is_paw_back_chatting.domain.chatMessage.service.ChatMessageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChatMessageController {

  private final SimpMessagingTemplate messagingTemplate;
  private final ChatMessageService chatMessageService;

  @MessageMapping("/messages")
  public void processMessage(@Payload ChatMessage chatMessage) {
    ChatMessage savedMessage = chatMessageService.saveMessage(chatMessage);

    // 개인 채널로 메시지 전송
    messagingTemplate.convertAndSendToUser(
        chatMessage.getRecipientId(), "/queue/messages",
        savedMessage
    );
  }

  @GetMapping("/{roomId}/messages")
  @ResponseBody
  public List<ChatMessage> getChatMessages(@PathVariable String roomId) {
    return chatMessageService.getChatMessages(roomId);
  }

  @PutMapping("/{roomId}/mark-read")
  @ResponseBody
  public void markMessagesAsRead(@PathVariable String roomId, @RequestParam String userId) {
    chatMessageService.markMessagesAsRead(roomId, userId);
  }

}
