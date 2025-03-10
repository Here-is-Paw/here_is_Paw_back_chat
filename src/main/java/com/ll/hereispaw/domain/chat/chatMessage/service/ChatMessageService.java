package com.ll.hereispaw.domain.chat.chatMessage.service;

import com.ll.hereispaw.domain.chat.chatMessage.dto.ChatMessageResponseDto;
import com.ll.hereispaw.domain.chat.chatMessage.entity.ChatMessage;
import com.ll.hereispaw.domain.chat.chatMessage.repository.ChatMessageRepository;
import com.ll.hereispaw.domain.chat.chatRoom.entity.ChatRoom;
import com.ll.hereispaw.domain.chat.chatRoom.repository.ChatRoomRepository;
import com.ll.hereispaw.domain.member.member.entity.Member;
import com.ll.hereispaw.global.error.ErrorCode;
import com.ll.hereispaw.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    //메세지 생성
    public ChatMessage writeMessage(Long id, Long messageId, String content, Member member) {
        ChatRoom chatRoom = this.chatRoomRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(content);
        chatMessage.setMember(member);
        chatMessage.setChatRoom(chatRoom);
        
        // 자신이 보낸 메시지는 자동으로 읽음 처리
        if (member.equals(chatRoom.getChatUser())) {
            chatMessage.setChatUserRead(true);
        } else {
            chatMessage.setTargetUserRead(true);
        }
        
        return this.chatMessageRepository.save(chatMessage);
    }

    //메세지 조회
    @Transactional
    public List<ChatMessageResponseDto> listMessage(Member loginUser, Long chatRoomId) {
        ChatRoom chatRoom = this.chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        List<ChatMessageResponseDto> chatMessageResponseDtos = new ArrayList<>();

        // 메시지 조회 시 현재 사용자가 받은 메시지를 읽음으로 처리
        markMessagesAsRead(chatRoom, loginUser);

        for (ChatMessage chatMessage : chatRoom.getChatMessages()) {
            chatMessageResponseDtos.add(new ChatMessageResponseDto(chatMessage, loginUser));
        }

        return chatMessageResponseDtos;
    }
    
    // 특정 사용자가 받은 메시지를 읽음으로 표시
    @Transactional
    public void markMessagesAsRead(ChatRoom chatRoom, Member reader) {
        // 읽지 않은 메시지를 찾음 (사용자별로 구분)
        List<ChatMessage> unreadMessages = chatRoom.getChatMessages().stream()
                .filter(msg -> !msg.getMember().equals(reader) && !msg.isReadByMember(reader))
                .collect(Collectors.toList());
        
        // 각 메시지를 해당 사용자에 대해 읽음으로 표시
        for (ChatMessage message : unreadMessages) {
            message.markAsReadByMember(reader);
            chatMessageRepository.save(message);
        }
    }
    
    // 특정 채팅방의 안 읽은 메시지 개수 계산
    public long countUnreadMessages(ChatRoom chatRoom, Member member) {
        // 채팅방에서 현재 사용자가 수신자인 메시지 중 읽지 않은 메시지 수 반환
        return chatRoom.getChatMessages().stream()
                .filter(msg -> !msg.getMember().equals(member) && !msg.isReadByMember(member))
                .count();
    }
}
