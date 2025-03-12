package com.ll.hereispaw.domain.chat.chatRoom.repository;

import com.ll.hereispaw.domain.chat.chatRoom.entity.ChatRoom;
import com.ll.hereispaw.domain.member.dto.MemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Page<ChatRoom> findAll(Pageable pageable);
    //참여중인 채팅방 목록 조회
    @Query("SELECT c FROM ChatRoom c WHERE c.chatUserId = :chatUser OR c.targetUserId = :targetUser")
    List<ChatRoom> findByRoomList(@Param("chatUser") Long chatUser, @Param("targetUser") Long targetUser);

    //두 사용자의 채팅방 조회 검증
    @Query("SELECT c FROM ChatRoom c WHERE (c.chatUserId = :user1Id AND c.targetUserId = :user2Id) OR (c.chatUserId = :user2Id AND c.targetUserId = :user1Id)")
    Optional<ChatRoom> findByRoom(
            @Param("user1Id") Long user1Id,
            @Param("user2Id") Long user2Id
    );
}
