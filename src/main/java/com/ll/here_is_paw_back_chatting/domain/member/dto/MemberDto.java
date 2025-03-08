package com.ll.here_is_paw_back_chatting.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
  private Long id;
  private String username;
  private String nickname;
  private String avatar;
}