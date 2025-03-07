package com.ll.hereispaw.domain.search.search.document;

import lombok.Getter;

@Getter
public enum IndexName {
    POST("post");

    private final String indexName; // String 필드 추가

    IndexName(String indexName) { // Getter 메서드
        this.indexName = indexName;
    }
}
