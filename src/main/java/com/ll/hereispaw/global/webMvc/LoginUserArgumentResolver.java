package com.ll.hereispaw.global.webMvc;


import com.ll.hereispaw.domain.member.dto.MemberDto;
import com.ll.hereispaw.domain.member.service.MemberServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final MemberServiceClient memberServiceClient;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginUser.class) &&
            parameter.getParameterType().equals(MemberDto.class);
    }

    @Override
    public Object resolveArgument(
        MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory
    ) {

        // 모든 헤더 출력
        webRequest.getHeaderNames().forEachRemaining(headerName -> {
            log.debug("Header: {} = {}", headerName, webRequest.getHeader(headerName));
        });

        String userIdStr = webRequest.getHeader("X-User-Id");

        if (userIdStr == null) {
            log.debug("X-User-Id header not found");
            return null;
        }

        try {
            Long userId = Long.parseLong(userIdStr);
            log.debug("Found userId in header: {}", userId);
            // API Gateway에서 전달된 기본 정보로 Member 객체 생성
            //            MemberDto memberDto = new MemberDto();
            //            memberDto.setId(userId);
            //            memberDto.setUsername(webRequest.getHeader("X-Username"));

                        // 필요에 따라 MemberService에서 상세 정보 로드 (선택적)
                        // 아래 코드는 상세 정보가 필요할 때 주석 해제
                        return memberServiceClient.getMemberById(userId);
//            return memberDto;
        } catch (NumberFormatException e) {
            log.error("Invalid userId format: {}", userIdStr, e);
            return null;
        }
    }
}