package com.ll.hereispaw.domain.chat.chatRoom.controller;


import com.ll.hereispaw.domain.chat.chatRoom.dto.ChatRoomDto;
import com.ll.hereispaw.domain.chat.chatRoom.entity.ChatRoom;
import com.ll.hereispaw.domain.chat.chatRoom.service.ChatRoomService;
import com.ll.hereispaw.domain.member.member.entity.Member;
import com.ll.hereispaw.global.globalDto.GlobalResponse;
import com.ll.hereispaw.global.webMvc.LoginUser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat/rooms")
public class ApiV1ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Getter
    public static class requestDto {
        Long targetUserId;
    }
    //채팅방 조회 후 생성 또는 참여
    @PostMapping("")
    public GlobalResponse<ChatRoomDto> create(@LoginUser Member chatUser, @RequestBody requestDto requestDto) {
        ChatRoom chatRoom = chatRoomService.createRoomOrView(chatUser, requestDto.targetUserId);
        ChatRoomDto chatRoomDto = new ChatRoomDto(chatRoom);
        //실시간 채팅 목록
        simpMessagingTemplate.convertAndSend("/topic/api/v1/chat/new-room", chatRoomDto);
        //실시간 채팅방 마지막 메세지
        simpMessagingTemplate.convertAndSend("/topic/api/v1/chat/" + chatRoom.getId() + "/messages", chatRoomDto);
        return GlobalResponse.success(chatRoomDto);
    }

    //채팅방 목록
    @GetMapping("/list")
    public GlobalResponse<List<ChatRoomDto>>roomList(@LoginUser Member chatUser){
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
    public GlobalResponse<List<ChatRoomDto>> roomListWithUnreadCount(@LoginUser Member chatUser) {
        List<ChatRoomDto> chatRoomDtos = chatRoomService.roomListWithUnreadCount(chatUser);
        return GlobalResponse.success(chatRoomDtos);
    }

//    //채팅방 입장
//    @GetMapping("/{roomId}")
//    public GlobalResponse<ChatRoomDto> viewRoom(@PathVariable("roomId")Long roomId){
//        ChatRoom chatRoom = this.chatRoomService.viewRoom(roomId);
//        ChatRoomDto chatRoomDto = new ChatRoomDto(chatRoom);
//        return GlobalResponse.success(chatRoomDto);
//    }


    //채팅방나가기
    @PostMapping("/{roomId}/leave")
    public GlobalResponse<String> leaveRoom(@PathVariable("roomId")Long roomId, @LoginUser Member member){
        chatRoomService.leaveRoom(roomId,member);
        return GlobalResponse.success("채팅방 나가기 성공");
    }
}
