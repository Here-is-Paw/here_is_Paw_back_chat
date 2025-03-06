package com.ll.hereispaw.domain.mapPost.mapPost.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MapPostRequest {
    @NotNull
    private double latitude;
    @NotNull
    private double longitude;
    @NotNull
    private double radius;
}
