package com.ll.hereispaw.domain.search.search.document;

import lombok.Getter;

@Getter
public enum PostState {
    CREATE(0), UPDATE(1), DELETE(2);

    // Getter 메서드
    private final int code;

    PostState(int code) {
        this.code = code;
    }
}
