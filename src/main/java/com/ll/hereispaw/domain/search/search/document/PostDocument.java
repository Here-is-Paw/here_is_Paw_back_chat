package com.ll.hereispaw.domain.search.search.document;

import com.ll.hereispaw.domain.search.search.dto.request.PostEventDto;
import lombok.Data;
import lombok.NonNull;

@Data
public class PostDocument{
    @NonNull
    private String id;

    private long post_id;

    @NonNull
    private String breed;

    @NonNull
    private String location;

    private int type; // 0 = 실종 1 = 발견

    private String etc;

    public PostDocument(PostEventDto postEventDto) {
        this.id = postEventDto.getId();
        this.post_id = postEventDto.getPost_id();
        this.breed = postEventDto.getBreed();
        this.location = postEventDto.getLocation();
        this.type = postEventDto.getType();
        this.etc = postEventDto.getEtc();
    }
}
