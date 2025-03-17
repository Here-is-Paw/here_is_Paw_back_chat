package com.ll.hereispaw.domain.chat.chatRoom.controller;


import com.ll.hereispaw.domain.chat.chatRoom.dto.ChatRoomDto;
import com.ll.hereispaw.domain.chat.chatRoom.entity.ChatRoom;
import com.ll.hereispaw.domain.chat.chatRoom.service.ChatRoomService;
import com.ll.hereispaw.domain.member.dto.MemberDto;
import com.ll.hereispaw.global.globalDto.GlobalResponse;
import com.ll.hereispaw.global.webMvc.LoginUser;
import com.ll.hereispaw.global.webSocket.SseEmitters;
import com.ll.hereispaw.global.webSocket.Ut;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat/rooms")
@Slf4j
public class ApiV1ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final SseEmitters sseEmitters;

    @Getter
    public static class requestDto {
        Long targetUserId;
    }
    //채팅방 조회 후 생성 또는 참여
    @PostMapping("")
    public GlobalResponse<ChatRoomDto> create(@LoginUser MemberDto chatUser, @RequestBody requestDto requestDto) {
        ChatRoom chatRoom = chatRoomService.createRoomOrView(chatUser, requestDto.targetUserId);
        ChatRoomDto chatRoomDto = new ChatRoomDto(chatRoom);
        //실시간 채팅 목록
        simpMessagingTemplate.convertAndSend("/topic/api/v1/chat/new-room", chatRoomDto);
        //실시간 채팅방 마지막 메세지
        simpMessagingTemplate.convertAndSend("/topic/api/v1/chat/" + chatRoom.getId() + "/messages", chatRoomDto);

        MemberDto targetUserId = new MemberDto(chatRoom.getTargetUserId(),chatRoom.getTargetUserNickname(),chatRoom.getTargetUserUrl());
        // SSE로 채팅방 생성 알림 전송 (양쪽 사용자 모두에게)
        notifyUnreadMessages(chatUser);
        notifyUnreadMessages(targetUserId); // targetUser에게도 알림 전송
        
        return GlobalResponse.success(chatRoomDto);
    }

    //채팅방 목록
    @GetMapping("/list")
    public GlobalResponse<List<ChatRoomDto>>roomList(@LoginUser MemberDto chatUser){
        List<ChatRoom> chatRooms = chatRoomService.roomList(chatUser);

        //chatRoom을 하나하나 chatRoomDto로 변환해서 전송
        List<ChatRoomDto> chatRoomDtos = new ArrayList<>();
        for(ChatRoom chatRoom: chatRooms) {
            ChatRoomDto chatRoomDto = new ChatRoomDto(chatRoom);
            chatRoomDtos.add(chatRoomDto);
        }
        return GlobalResponse.success(chatRoomDtos);
    }
    
    //안 읽은 메시지 수가 포함된 채팅방 목록
    @GetMapping("/list-with-unread")
    public GlobalResponse<List<ChatRoomDto>> roomListWithUnreadCount(@LoginUser MemberDto chatUser) {
        log.debug("{}:여기가 문제야",chatUser.getId());
        List<ChatRoomDto> chatRoomDtos = chatRoomService.roomListWithUnreadCount(chatUser);
        log.debug("{}:두번째",chatRoomDtos.size());
        return GlobalResponse.success(chatRoomDtos);
    }
    
    // SSE 연결 엔드포인트
    @GetMapping(value = "/sse/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(
        @RequestParam("userId") Long userId
    ) {
        return sseEmitters.add(userId, new SseEmitter(60 * 1000L * 60)); // 60분 유지
    }
    
    // 안 읽은 메시지 상태 업데이트를 위한 SSE 알림 메서드
    private void notifyUnreadMessages(MemberDto member) {
        List<ChatRoomDto> chatRoomDtos = chatRoomService.roomListWithUnreadCount(member);
        
        // 현재 로그인한 사용자의 ID를 포함하여 모든 연결된 클라이언트에게 알림
        sseEmitters.noti("unreadMessages", Ut.mapOf(
            "userId", member.getId(),
            "chatRooms", chatRoomDtos
        ));
    }

    //채팅방나가기
    @PostMapping("/{roomId}/leave")
    public GlobalResponse<String> leaveRoom(@PathVariable("roomId")Long roomId, @LoginUser MemberDto member){
        chatRoomService.leaveRoom(roomId,member);
        return GlobalResponse.success("채팅방 나가기 성공");
    }
}
