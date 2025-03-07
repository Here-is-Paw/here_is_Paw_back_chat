package com.ll.hereispaw.domain.chat.chatRoom.dto;

import com.ll.hereispaw.domain.chat.chatMessage.dto.ChatMessageDto;
import com.ll.hereispaw.domain.chat.chatMessage.entity.ChatMessage;
import com.ll.hereispaw.domain.chat.chatRoom.entity.ChatRoom;
import com.ll.hereispaw.domain.member.member.entity.Member;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ChatRoomDto {

    private Long id;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private int roomState;

    private Long chatUserId;
    private String chatUserNickname;
    private String chatUserImageUrl;

    private Long targetUserId;
    private String targetUserNickname;
    private String targetUserImageUrl;
    
    // 안 읽은 메시지 수
    private long unreadMessageCount;

    private List<ChatMessageDto> chatMessages = new ArrayList<>();
    //private Member chatUser;

    public ChatRoomDto(ChatRoom chatRoom){

        this.id = chatRoom.getId();
        this.createdDate = chatRoom.getCreateDate();
        this.modifiedDate = chatRoom.getModifyDate();
        this.roomState = chatRoom.getRoomState();

        // 그냥 하면 단순화 되어있는 메세지 정보만 가져와서 메세지의 필요한 정보를 DTO에 넣어서 메세지DTO를 각각 개체화하여 하나의 객체로 필요한 정보를 넣어서 가져옴
        List<ChatMessage> messages = chatRoom.getChatMessages();
        if (messages != null) {
            for (ChatMessage message : messages) {
                this.chatMessages.add(new ChatMessageDto(message));
            }
        }

        //다른 테이블에 연결해서 정보를 가져오려면 인스턴스화로 생성해주고 가져와야함
        // Member chatUser = chatRoom.getChatUser();
        //this.chatUserId = chatUser.getId();
        //this.chatUserNickname = chatUser.getNickname();

        //if (chatRoom.getTargetUser() != null) 보류
        this.chatUserId = chatRoom.getChatUser().getId();
        this.chatUserNickname = chatRoom.getChatUser().getNickname();
        this.chatUserImageUrl = chatRoom.getChatUser().getAvatar();

        //if (chatRoom.getTargetUser() != null) 보류
        this.targetUserId = chatRoom.getTargetUser().getId();
        this.targetUserNickname = chatRoom.getTargetUser().getNickname();
        this.targetUserImageUrl = chatRoom.getChatUser().getAvatar();
        
        // 기본값으로 0 설정 (실제로는 서비스에서 설정됨)
        this.unreadMessageCount = 0;
    }
    
    public ChatRoomDto(ChatRoom chatRoom, Member currentUser, long unreadCount){
        this(chatRoom);
        this.unreadMessageCount = unreadCount;
    }
}