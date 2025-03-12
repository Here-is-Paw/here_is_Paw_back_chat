package com.ll.hereispaw.domain.member.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
  private Long id;
  private String username;
  private String nickname;
  private String avatar;

  public MemberDto(Long id, String nickname, String avatar){
    this.id = id;
    this.nickname = nickname;
    this.avatar = avatar;
  }

}