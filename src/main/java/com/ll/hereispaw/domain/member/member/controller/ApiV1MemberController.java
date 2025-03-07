package com.ll.hereispaw.domain.member.member.controller;

import com.ll.hereispaw.domain.member.member.dto.request.LoginRequest;
import com.ll.hereispaw.domain.member.member.dto.request.ModifyRequest;
import com.ll.hereispaw.domain.member.member.dto.request.RadiusRequest;
import com.ll.hereispaw.domain.member.member.dto.request.SignupRequest;
import com.ll.hereispaw.domain.member.member.dto.response.LoginResponse;
import com.ll.hereispaw.domain.member.member.dto.response.MemberInfoDto;
import com.ll.hereispaw.domain.member.member.entity.Member;
import com.ll.hereispaw.domain.member.member.service.MemberService;
import com.ll.hereispaw.global.error.ErrorCode;
import com.ll.hereispaw.global.globalDto.GlobalResponse;
import com.ll.hereispaw.global.rq.Rq;
import com.ll.hereispaw.global.webMvc.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class ApiV1MemberController {

    private final MemberService memberService;
    private final Rq rq;

    // 유저 기본 정보 가져오기
    @GetMapping("/me")
    public GlobalResponse<?> me(@LoginUser Member loginUser) {

        if (loginUser == null) {
            // 미로그인시 401 Unauthorized 반환
            return GlobalResponse.error(ErrorCode.ACCESS_DENIED);
        }

        MemberInfoDto userInfo = memberService.me(loginUser);

        return GlobalResponse.success(userInfo);
    }

    // 어드민 - 유저 추가
    // username (= id)의 앞에 admin 입력하여 등록 시 admin 권한 부여
    // username (= id)의 앞에 manager 입력하여 등록 시 manager 권한 부여
    @PostMapping("/signup")
    public GlobalResponse<String> signup(@Valid @RequestBody SignupRequest signupRq) {

        memberService.signup(signupRq);
        return GlobalResponse.createSuccess("회원 생성 완료");
    }

    // 어드민 - 로그인
    @PostMapping("/login")
    public GlobalResponse<LoginResponse> login(@Valid @RequestBody LoginRequest loginRq) {
        LoginResponse loginUser = memberService.login(loginRq);

        return GlobalResponse.success(loginUser);
    }

    @PatchMapping("/radius")
    public GlobalResponse<String> radius_update(@LoginUser Member loginUser, @Valid @RequestBody RadiusRequest radiusRequest) {
        log.debug("radius : {}", radiusRequest.radius());

        memberService.radius_update(loginUser, radiusRequest.radius());
        return GlobalResponse.success("로케이션 업데이트");
    }

    @PatchMapping("/modify")
    public GlobalResponse<MemberInfoDto> modify(@LoginUser Member member, ModifyRequest modifyRequest) {
        MemberInfoDto memberInfoDto = memberService.modify(member, modifyRequest);

        return GlobalResponse.success(memberInfoDto);
    }

    // 로그아웃
    @DeleteMapping("/logout")
    public GlobalResponse<String> logout() {
        rq.deleteCookie("accessToken");
        rq.deleteCookie("apiKey");

        return GlobalResponse.success("로그아웃 되었습니다.");
    }
}
