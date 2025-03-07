package com.ll.hereispaw.domain.missing.missing.dto.response;

import com.ll.hereispaw.domain.find.find.entity.FindPost;
import com.ll.hereispaw.domain.missing.missing.entity.Missing;
import com.ll.hereispaw.domain.search.search.document.PostType;
import com.ll.hereispaw.global.error.ErrorCode;
import com.ll.hereispaw.global.exception.CustomException;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class CreatePostEventDto {
    @NonNull
    private String id;

    @NonNull
    private Long post_id;

    @NonNull
    private String breed;

    @NonNull
    private String location;

    private int type; // 0 = 실종 1 = 발견

    private String name;

    private String etc;

    private int state;

    public CreatePostEventDto(Object post, int state) {
        if (post instanceof FindPost) {
            setFind((FindPost) post, state);
        }else if (post instanceof Missing) {
            setMissing((Missing) post, state);
        }else {
            throw new CustomException(ErrorCode.INVALID_TYPE_VALUE);
        }
    }

    private void setFind(FindPost post, int state) {
        log.debug("find : {}", post.getId());
        this.id = "find_" + post.getId();
        this.post_id = post.getId();
        this.name = post.getName();
        this.breed = post.getBreed();
        this.location = post.getLocation();
        this.type = PostType.FIND.getCode();
        this.etc = post.getEtc();
        this.state = state;
//        this.createDate = LocalDateTime.now();
    }

    private void setMissing(Missing post, int state) {
        log.debug("missing : {}", post.getId());
        this.id = "missing_" + post.getId();
        this.post_id = post.getId();
        this.name = post.getName();
        this.breed = post.getBreed();
        this.location = post.getLocation();
        this.type = PostType.MISSING.getCode();
        this.etc = post.getEtc();
        this.state = state;
//        this.createDate = LocalDateTime.now();
    }
}
