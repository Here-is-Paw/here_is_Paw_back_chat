package com.ll.hereispaw.domain.member.member.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ll.hereispaw.domain.chat.chatMessage.entity.ChatMessage;
import com.ll.hereispaw.domain.chat.chatRoom.entity.ChatRoom;
import com.ll.hereispaw.domain.member.mypet.entity.MyPet;
import com.ll.hereispaw.domain.payment.payment.entity.Payment;
import com.ll.hereispaw.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Member extends BaseEntity {
    @Column(unique = true, length = 30)
    private String username;

    private String password;

    @Column(length = 30)
    private String nickname;

    @Column(unique = true, length = 50)
    private String apiKey;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<MyPet> myPets;

    //채팅 관계
    @OneToMany(fetch = FetchType.EAGER ,mappedBy = "chatUser")
    @JsonManagedReference
    private List<ChatRoom> chatRoomsCU;

    @OneToMany(fetch = FetchType.EAGER ,mappedBy = "targetUser")
    @JsonManagedReference
    private List<ChatRoom> chatRoomsTU;

    //메세지 관계
    @OneToMany(fetch = FetchType.EAGER ,mappedBy = "member")
    @JsonManagedReference
    private List<ChatMessage> chatMessages;

    private String avatar;

    private Integer radius;

    public boolean isAdmin() {
        return "admin".startsWith(username);
    }

    public boolean isManager() {
        return "manager".startsWith(username);
    }

    public boolean matchPassword(String password) {
        return this.password.equals(password);
    }

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    public Member(long id, String username, String nickname) {
        this.setId(id);
        this.username = username;
        this.nickname = nickname;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getAuthoritiesAsStringList()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    public List<String> getAuthoritiesAsStringList() {
        List<String> authorities = new ArrayList<>();

        if (isAdmin())
            authorities.add("ROLE_ADMIN");

        if (isManager())
            authorities.add("ROLE_MANAGER");

        return authorities;
    }
}