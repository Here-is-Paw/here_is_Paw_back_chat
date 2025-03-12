package com.ll.hereispaw.domain.chat.chatMessage.controller;

import com.ll.hereispaw.domain.chat.chatMessage.dto.ChatMessageDto;
import com.ll.hereispaw.domain.chat.chatMessage.dto.ChatMessageResponseDto;
import com.ll.hereispaw.domain.chat.chatMessage.dto.CreateChatMessage;
import com.ll.hereispaw.domain.chat.chatMessage.entity.ChatMessage;
import com.ll.hereispaw.domain.chat.chatMessage.service.ChatMessageService;
import com.ll.hereispaw.domain.chat.chatRoom.entity.ChatRoom;
import com.ll.hereispaw.domain.chat.chatRoom.repository.ChatRoomRepository;
import com.ll.hereispaw.domain.chat.chatRoom.service.ChatRoomService;
import com.ll.hereispaw.domain.member.dto.MemberDto;
import com.ll.hereispaw.global.error.ErrorCode;
import com.ll.hereispaw.global.exception.CustomException;
import com.ll.hereispaw.global.globalDto.GlobalResponse;
import com.ll.hereispaw.global.webMvc.LoginUser;
import com.ll.hereispaw.global.webSocket.SseEmitters;
import com.ll.hereispaw.global.webSocket.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ApiV1ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomService chatRoomService;
    private final SseEmitters sseEmitters;

    //메세지 생성
    @PostMapping("/{chatRoomId}/messages")
    public GlobalResponse<ChatMessageDto> writeMessage(@PathVariable("chatRoomId") Long chatRoomId, @RequestBody CreateChatMessage createChatMessage, @LoginUser MemberDto member){
        ChatMessage chatMessage = this.chatMessageService.writeMessage(chatRoomId, createChatMessage.getChatMessageId(), createChatMessage.getContent(), member);
        ChatMessageDto messages = new ChatMessageDto(chatMessage);
        simpMessagingTemplate.convertAndSend("/topic/api/v1/chat/"+ chatRoomId +"/messages", messages);
        
        // 메시지 전송 후 안 읽은 메시지 수 업데이트 알림
        notifyUnreadMessages(member);
        
        // 상대방 사용자에게도 알림 보내기
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        MemberDto targetUserDto = member.getId().equals(chatRoom.getChatUserId()) ?
                new MemberDto(chatRoom.getTargetUserId(),chatRoom.getTargetUserNickname(),chatRoom.getTargetUserUrl())
                : new MemberDto(chatRoom.getChatUserId(),chatRoom.getChatUserNickname(),chatRoom.getChatUserUrl());
        notifyUnreadMessages(targetUserDto);
        
        return GlobalResponse.success(messages);
    }

    //메세지 조회
    @GetMapping("/{chatRoomId}/messages")
    public GlobalResponse<List<ChatMessageResponseDto>> listMessage(@LoginUser MemberDto loginUser, @PathVariable("chatRoomId")Long chatRoomId){
        List<ChatMessageResponseDto> list = this.chatMessageService.listMessage(loginUser, chatRoomId);
        
        // 메시지 조회 후 안 읽은 메시지 수 업데이트 알림
        notifyUnreadMessages(loginUser);
        
        return GlobalResponse.success(list);
    }
    
    //메시지를 읽음으로 표시
    @PostMapping("/{chatRoomId}/mark-as-read")
    public GlobalResponse<String> markMessagesAsRead(@LoginUser MemberDto loginUser, @PathVariable("chatRoomId") Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
        
        // 메시지를 읽음으로 표시
        chatMessageService.markMessagesAsRead(chatRoom, loginUser);
        
        // 메시지를 읽은 후 안 읽은 메시지 수 업데이트 알림
        notifyUnreadMessages(loginUser);
        
        // 상대방 사용자에게도 알림 보내기 (읽음 상태 변경 표시)
        MemberDto targetUserDto = loginUser.getId().equals(chatRoom.getChatUserId()) ?
                new MemberDto(chatRoom.getTargetUserId(),chatRoom.getTargetUserNickname(),chatRoom.getTargetUserUrl())
                : new MemberDto(chatRoom.getChatUserId(),chatRoom.getChatUserNickname(),chatRoom.getChatUserUrl());
        notifyUnreadMessages(targetUserDto);
        
        return GlobalResponse.success("메시지가 읽음으로 표시되었습니다");
    }
    
    // 안 읽은 메시지 상태 업데이트를 위한 SSE 알림 메서드
    private void notifyUnreadMessages(MemberDto member) {
        if (member == null) return;
        
        // 사용자의 모든 채팅방에 대한 안 읽은 메시지 정보 가져오기
        sseEmitters.noti("unreadMessages", Ut.mapOf(
            "userId", member.getId(),
            "chatRooms", chatRoomService.roomListWithUnreadCount(member)
        ));
    }
}
