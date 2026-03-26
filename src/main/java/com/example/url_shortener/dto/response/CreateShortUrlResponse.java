package com.example.url_shortener.dto.response;

import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;


public record CreateShortUrlResponse(
        Long id,
        String shortCode,
        String shortUrl,
        String originalUrl,
        List<String> tags,
        FolderShortResponse folder,
        LocalDateTime createdAt
) {
}



