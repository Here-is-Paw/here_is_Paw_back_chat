package com.ll.hereispaw.domain.search.search.document;

import lombok.Getter;

@Getter
public enum PostType {
    FIND(0), MISSING(1);

    // Getter 메서드
    private final int code;

    PostType(int code) {
        this.code = code;
    }
}
