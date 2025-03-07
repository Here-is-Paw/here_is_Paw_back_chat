package com.ll.hereispaw.domain.member.member.dto.response;


import com.ll.hereispaw.domain.member.member.entity.Member;
import jakarta.validation.constraints.NotNull;

public record MemberInfoDto(@NotNull Long id, @NotNull String nickname, String avatar, @NotNull Double radius) {
    public MemberInfoDto(Member member) {
        this(member.getId(), member.getNickname(), member.getAvatar(), member.getRadius());
    }
}