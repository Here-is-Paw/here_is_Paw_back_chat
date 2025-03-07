package com.ll.hereispaw.domain.search.search.controller;

import com.ll.hereispaw.domain.search.search.document.PostDocument;
import com.ll.hereispaw.domain.search.search.service.PostDocumentService;
import com.ll.hereispaw.global.globalDto.GlobalResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class PostController {
    private final PostDocumentService postDocumentService;

    @GetMapping("add")
    public GlobalResponse<String> add(PostDocument postDoc) {
        postDocumentService.add(postDoc, "post");
        return GlobalResponse.createSuccess("메일리서치 저장 완료");
    }

    @GetMapping
    public GlobalResponse<?> search(
            @RequestParam("kw") String keyword,
            @PageableDefault(size = 10) Pageable pageable) {
//        Page
//                <Item>
        return GlobalResponse.success(postDocumentService.typeAllSearch(keyword, pageable));
    }
//    @GetMapping
//    public GlobalResponse<SearchResult> search(@RequestParam("kw") String kw) {
//        SearchResult result = postDocumentService.typeAllSearch(kw);
//
//        return GlobalResponse.success(result);
//    }

}
