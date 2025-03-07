package com.ll.hereispaw.domain.search.search.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchPostResponseDto {
    @NonNull
    private String id;

    private long post_id;

    @NonNull
    private String breed;

    @NonNull
    private String location;

    private int type; // 0 = 실종 1 = 발견

    private String etc;
}
