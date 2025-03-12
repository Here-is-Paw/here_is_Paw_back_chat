package com.ll.hereispaw.domain.chat.chatMessage.dto;

import com.ll.hereispaw.domain.chat.chatMessage.entity.ChatMessage;
import com.ll.hereispaw.domain.member.dto.MemberDto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageResponseDto {
    private Long memberId;
    private Long chatMessageId;
    private String memberNickname;
    private String content;
    private LocalDateTime createdDate;

    public ChatMessageResponseDto(ChatMessage chatMessage, MemberDto loginUser) {
        this.memberId = chatMessage.getMemberId();
        this.chatMessageId = chatMessage.getId();
        this.memberNickname = chatMessage.getMemberId().equals(loginUser.getId()) ? "me" : chatMessage.getMemberNickname();
        this.content = chatMessage.getContent();
        this.createdDate = chatMessage.getCreateDate();
    }
}
