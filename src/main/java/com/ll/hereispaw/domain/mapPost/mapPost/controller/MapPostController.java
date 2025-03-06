package com.ll.hereispaw.domain.mapPost.mapPost.controller;

import com.ll.hereispaw.domain.mapPost.mapPost.service.MapPostService;
import com.ll.hereispaw.domain.missing.missing.entity.Missing;
import com.ll.hereispaw.global.globalDto.GlobalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/map-posts")
@RequiredArgsConstructor
public class MapPostController {
    private final MapPostService mapPostService;

    @GetMapping
    public GlobalResponse<?> allPosts(@RequestParam Double latitude,
                                      @RequestParam Double longitude,
                                      @RequestParam(required = false) Double radius) {
        List<Missing> missingList = mapPostService.allSearch(latitude, longitude,radius);

        return GlobalResponse.success(missingList);
    }
}
