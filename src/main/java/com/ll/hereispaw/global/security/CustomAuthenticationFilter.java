package com.ll.hereispaw.global.security;


import com.ll.hereispaw.domain.member.member.entity.Member;
import com.ll.hereispaw.domain.member.member.service.MemberService;
import com.ll.hereispaw.global.rq.Rq;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {
    // OncePerRequestFilter -> Http 요청 한번 당 한번만 실행

    private final MemberService memberService;
    private final Rq rq;

    record AuthTokens(String apiKey, String accessToken) {
    }

    private AuthTokens getAuthTokensFromRequest() {
        String authorization = rq.getHeader("Authorization");

        // OAuth2 및 JWT 기반 인증에서 Access 토큰을 전달할 떄 Bearer<토큰 값> 형식
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring("Bearer ".length());
            String[] tokenBits = token.split(" ", 2);

            if (tokenBits.length == 2)
                return new AuthTokens(tokenBits[0], tokenBits[1]);
        }

        // 쿠키 중 apiKey 가져오기
        String apiKey = rq.getCookieValue("apiKey");

        // 쿠키 중 accessToken 가져오기
        String accessToken = rq.getCookieValue("accessToken");

        if (apiKey != null && accessToken != null)
            return new AuthTokens(apiKey, accessToken);

        return null;
    }

    // 엑세스 토큰 재발급
    private void refreshAccessToken(Member member) {
        String newAccessToken = memberService.genAccessToken(member);

        rq.setHeader("Authorization", "Bearer " + member.getApiKey() + " " + newAccessToken);
        rq.setCookie("accessToken", newAccessToken);
    }

    // 해당 api 키 보유 유저 존재 확인 후 엑세스 토큰 재발급
    private Member refreshAccessTokenByApiKey(String apiKey) {
        Optional<Member> opMemberByApiKey = memberService.findByApiKey(apiKey);

        if (opMemberByApiKey.isEmpty()) {
            return null;
        }

        Member member = opMemberByApiKey.get();

        refreshAccessToken(member);

        return member;
    }

    // 해당 필터 실행
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!request.getRequestURI().startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (
                List.of(
                        "/api/v1/members/login",
                        "/api/v1/members/logout",
                        "/api/v1/members/signup",
                        "/api/v1/profile/**",
                        "/swagger-ui/index.html",
                        "/api/v1/chat/**"
//                        "/api/v1/missings"
//                        "/api/v1/members/login"
                ).contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        AuthTokens authTokens = getAuthTokensFromRequest();

        if (authTokens == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = authTokens.apiKey;
        String accessToken = authTokens.accessToken;

        Member member = memberService.getMemberFromAccessToken(accessToken);

        if (member == null)
            member = refreshAccessTokenByApiKey(apiKey);
        if (member != null)
            rq.setLogin(member);

        filterChain.doFilter(request, response);
    }
}