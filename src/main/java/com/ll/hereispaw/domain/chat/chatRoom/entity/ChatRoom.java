package com.ll.hereispaw.domain.chat.chatRoom.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ll.hereispaw.domain.chat.chatMessage.entity.ChatMessage;
import com.ll.hereispaw.global.jpa.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ChatRoom extends BaseEntity {

    private int roomState;

    @OneToMany(fetch = FetchType.EAGER ,mappedBy = "chatRoom" )
    @JsonManagedReference
    private List<ChatMessage> chatMessages;


    private Long chatUserId;
    private String chatUserNickname;
    private String chatUserUrl;

    private Long targetUserId;
    private String targetUserNickname;
    private String targetUserUrl;
}