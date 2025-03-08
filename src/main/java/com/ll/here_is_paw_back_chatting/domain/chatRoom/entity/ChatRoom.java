package com.ll.here_is_paw_back_chatting.domain.chatRoom.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

@Data
@Entity
public class ChatRoom{
  @Id
  private String id;

  private String user1Id;

  private String user2Id;

  @CreatedDate
  private LocalDateTime createDate;

  // 두 사용자 ID로 채팅방을 식별하는 복합 ID 생성
  public static String generateRoomId(String user1Id, String user2Id) {
    // 사용자 ID를 정렬하여 항상 동일한 채팅방 ID가 생성되도록 함
    if (user1Id.compareTo(user2Id) > 0) {
      String temp = user1Id;
      user1Id = user2Id;
      user2Id = temp;
    }
    return user1Id + "_" + user2Id;
  }

}
