package com.ll.hereispaw.domain.member.member.dto.request;

import jakarta.validation.constraints.NotNull;

public record RadiusRequest(@NotNull Double radius) {
}
