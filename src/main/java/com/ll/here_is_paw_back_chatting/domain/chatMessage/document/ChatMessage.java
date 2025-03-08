package com.ll.here_is_paw_back_chatting.domain.chatMessage.document;

import java.util.Date;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document(collection = "message")
public class ChatMessage {
  @Id
  private String id;
  private String roomId;
  private String senderId;
  private String recipientId;
  private String content;
  private Date timestamp;
  private boolean read;
}
