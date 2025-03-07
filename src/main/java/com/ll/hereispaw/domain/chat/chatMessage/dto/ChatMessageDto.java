package com.ll.hereispaw.domain.chat.chatMessage.dto;

import com.ll.hereispaw.domain.chat.chatMessage.entity.ChatMessage;
import com.ll.hereispaw.domain.member.member.entity.Member;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatMessageDto {

    private Long chatMessageId;
    private Long chatRoomId;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private Long memberId;
    private String memberNickname;
    private String content;
    private boolean isChatUserRead;
    private boolean isTargetUserRead;

    public ChatMessageDto(ChatMessage chatMessage){
        this.chatMessageId      = chatMessage.getId();
        this.chatRoomId         = chatMessage.getChatRoom().getId();
        this.content            = chatMessage.getContent();
        this.createdDate        = chatMessage.getCreateDate();
        this.modifiedDate       = chatMessage.getModifyDate();

        //Member객체를 통해서 id와 nickname을 member 저장하고 한 번에 호출하는 방식
        /*Member member = chatMessage.getMember();

        this.memberId = member.getId();
        this.memberNickname = member.getNickname();*/

        //chatMessage를 통해서 따로 하나씩 호출하는 방식
        this.memberId = chatMessage.getMember().getId();
        this.memberNickname = chatMessage.getMember().getNickname();
        this.isChatUserRead = chatMessage.isChatUserRead();
        this.isTargetUserRead = chatMessage.isTargetUserRead();
    }
}
