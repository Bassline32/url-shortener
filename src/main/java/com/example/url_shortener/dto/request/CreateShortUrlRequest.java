package com.example.url_shortener.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record CreateShortUrlRequest(
        @NotBlank
        String originalUrl,

        String customCode,
        LocalDateTime expiresAt,
        Long folderId,
        List<String> tags
) {}
