package com.ll.here_is_paw_back_chatting.global.exception;


import com.ll.here_is_paw_back_chatting.global.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;
}
