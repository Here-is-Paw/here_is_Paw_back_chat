package com.ll.hereispaw.domain.chat.chatRoom.service;

import com.ll.hereispaw.domain.chat.chatMessage.repository.ChatMessageRepository;
import com.ll.hereispaw.domain.chat.chatRoom.dto.ChatRoomDto;
import com.ll.hereispaw.domain.chat.chatRoom.entity.ChatRoom;
import com.ll.hereispaw.domain.chat.chatRoom.repository.ChatRoomRepository;
import com.ll.hereispaw.domain.member.dto.MemberDto;
import com.ll.hereispaw.domain.member.service.MemberServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
//    private final MemberRepository memberRepository;
    private final MemberServiceClient memberServiceClient;
    private final ChatMessageRepository chatMessageRepository;

    //채팅방 생성
    public ChatRoom createRoom(MemberDto chatUser,Long targetUserId){
//        Member targetUser = memberServiceClient.findById(targetUserId).orElse(null);
        MemberDto targetUser = memberServiceClient.getMemberById(targetUserId);
        if (targetUser == null) {
            throw new RuntimeException("상대방 사용자를 찾을 수 없습니다.");
        }
        ChatRoom chatRoom = ChatRoom.builder()
                .chatUserId(chatUser.getId())
                .chatUserNickname(chatUser.getNickname())
                .chatUserUrl(chatUser.getAvatar())
                .targetUserId(targetUser.getId())
                .targetUserNickname(targetUser.getNickname())
                .targetUserUrl(targetUser.getAvatar())
                .createDate(LocalDateTime.now())
                //.roomState()
                .build();
        chatRoomRepository.save(chatRoom);
        return chatRoom;
    }
    //채팅방 입장
    public ChatRoom viewRoom(Long id) {
        Optional<ChatRoom> chatRoom = this.chatRoomRepository.findById(id);
        if (chatRoom.isPresent()) {
            ChatRoom cR = chatRoom.get();
            //List<ChatMessage> messages = cR.getChatMessages();
            return cR;
        } else {
            throw new RuntimeException("error ChatService viewRoom");
        }
    }

    //사용자간의 채팅방 조회 후 생성 또는 참여
    public ChatRoom createRoomOrView(MemberDto chatUser, Long targetUserId) {
        // 상대방 조회 람다 연습
//        Member targetUser = memberRepository.findById(targetUserId)
                MemberDto targetUser = memberServiceClient.getMemberById(targetUserId);
//                .orElseThrow(() -> new RuntimeException("상대방 사용자를 찾을 수 없습니다."));

        Optional<ChatRoom> existingRoom = chatRoomRepository.findByRoom(chatUser.getId(), targetUser.getId());

        // 존재하는 채팅방 조회 및 상태 확인
        if (existingRoom.isPresent() && existingRoom.get().getRoomState() != 3) {
            ChatRoom room = existingRoom.get();

            // 사용자가 이전에 나갔던 경우 다시 참여시 방 상태 0으로 만듬
            if (room.getChatUserId().equals(chatUser.getId()) && room.getRoomState() == 1) {
                room.setRoomState(0);
                chatRoomRepository.save(room);
            } else if (room.getTargetUserId().equals(chatUser.getId()) && room.getRoomState() == 2) {
                room.setRoomState(0);
                chatRoomRepository.save(room);
            }

            return viewRoom(room.getId());
        } else {
            // 채팅방이 없거나 roomState가 3인 경우 새 채팅방 생성
            return createRoom(chatUser, targetUserId);
        }
    }

    //안 읽은 메시지 수를 포함한 채팅방 목록 조회
    public List<ChatRoomDto> roomListWithUnreadCount(MemberDto member) {
        List<ChatRoom> chatRooms = roomList(member);
        
        return chatRooms.stream()
                .map(chatRoom -> {
                    long unreadCount = countUnreadMessages(chatRoom, member);
                    return new ChatRoomDto(chatRoom, member, unreadCount);
                })
                .collect(Collectors.toList());
    }

    //특정 채팅방의 안 읽은 메시지 개수 계산
    private long countUnreadMessages(ChatRoom chatRoom, MemberDto member) {
        // 채팅방에서 현재 사용자가 수신자인 메시지 중 읽지 않은 메시지 수 반환
        return chatRoom.getChatMessages().stream()
                .filter(msg -> !msg.getMemberId().equals(member.getId()) && !msg.isReadByMember(member.getId()))
                .count();
    }

    //참여된 채팅방 목록 조회
    public List<ChatRoom> roomList(MemberDto member) {
        List<ChatRoom> allRooms = chatRoomRepository.findByRoomList(member.getId(), member.getId());
        List<ChatRoom> filteredRooms = new ArrayList<>();

        for (ChatRoom room : allRooms) {
            //지역 변수 재활당 코드 shouldInclude
            boolean shouldInclude = false;

            if (room.getChatUserId().equals(member.getId())) {
                // chatUser인 경우 roomState가 0 또는 2인 채팅방만 표시
                if (room.getRoomState() == 0 || room.getRoomState() == 2) {
                    shouldInclude = true;
                }
            } else if (room.getTargetUserId().equals(member.getId())) {
                // targetUser인 경우 roomState가 0 또는 1인 채팅방만 표시
                if (room.getRoomState() == 0 || room.getRoomState() == 1) {
                    shouldInclude = true;
                }
            }

            if (shouldInclude) {
                filteredRooms.add(room);
            }
        }

        return filteredRooms;
    }

    //채팅방 나가기
    public void leaveRoom(Long roomId, MemberDto member){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
        //맴버의 객체를 비교하기 때문에 equals 사용
        // chatUser가 나가는 경우
        if(chatRoom.getChatUserId().equals(member.getId())){
            if (chatRoom.getRoomState() == 2) {
                // targetUser가 나가서 방 상태가 2이면 3
                chatRoom.setRoomState(3);
            } else {
                //chatUser만 나갔을 경우
                chatRoom.setRoomState(1);
            }
            // targetUser가 나가는 경우
        } else if (chatRoom.getTargetUserId().equals(member.getId())) {
            if (chatRoom.getRoomState() == 1) {
                // chatUser가 나가서 방 상태가 1이면 3
                chatRoom.setRoomState(3);
            } else {
                //targetUser만 나간 경우
                chatRoom.setRoomState(2);
            }
        } else {
            throw new RuntimeException("해당 채팅방의 참여자가 아닙니다.");
        }
        chatRoomRepository.save(chatRoom);
    }
}
