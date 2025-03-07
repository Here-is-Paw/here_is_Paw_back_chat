package com.ll.hereispaw.domain.chat.chatMessage.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ll.hereispaw.domain.chat.chatRoom.entity.ChatRoom;
import com.ll.hereispaw.domain.member.member.entity.Member;
import com.ll.hereispaw.global.jpa.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ChatMessage extends BaseEntity {

    @JsonBackReference
    @ManyToOne
    private ChatRoom chatRoom;

    @JsonBackReference
    @ManyToOne
    private Member member;

    private String content;
    
    // 메시지 읽음 상태를 사용자별로 구분
    private boolean isChatUserRead = false;  // chatUser가 메시지를 읽었는지 여부
    private boolean isTargetUserRead = false;  // targetUser가 메시지를 읽었는지 여부
    
    // 메시지를 읽은 사용자인지 확인하는 메서드
    public boolean isReadByMember(Member reader) {
        ChatRoom room = this.getChatRoom();
        boolean isChatUser = reader.equals(room.getChatUser());
        
        return isChatUser ? this.isChatUserRead : this.isTargetUserRead;
    }
    
    // 메시지를 읽음으로 표시하는 메서드
    public void markAsReadByMember(Member reader) {
        ChatRoom room = this.getChatRoom();
        boolean isChatUser = reader.equals(room.getChatUser());
        
        if (isChatUser) {
            this.isChatUserRead = true;
        } else {
            this.isTargetUserRead = true;
        }
    }
}
