package com.ll.hereispaw.domain.member.service;


import com.ll.hereispaw.domain.member.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceClient {

  private final RestTemplate restTemplate;

  @Value("${services.auth.url}")
  private String memberServiceUrl;

  public MemberDto getMemberById(Long id) {
    try {
      // 멤버 서비스 URL 로깅
      log.info("Member Service URL: {}", memberServiceUrl);

      // 전체 URL 생성 및 로깅
      String url = UriComponentsBuilder.fromUriString(memberServiceUrl)
          .path("/api/v1/members/{id}")
          .buildAndExpand(id)
          .toUriString();

      log.info("Full URL for member request: {}", url);

      // 요청 시도 로깅
      log.info("Attempting to fetch member with id: {}", id);

      // RestTemplate 요청 및 응답 로깅
      MemberDto memberDto = restTemplate.getForObject(url, MemberDto.class);

      log.info("Successfully fetched member: {}", memberDto);

      return memberDto;
    } catch (Exception e) {
      // 예외 상세 로깅
      log.error("Error fetching member with id {}: {}", id, e.getMessage(), e);

      // 기본 Member 객체 반환 (ID만 포함)
      MemberDto memberDto = new MemberDto();
      memberDto.setId(id);

      log.info("Returning default MemberDto with id: {}", id);

      return memberDto;
    }
  }
}