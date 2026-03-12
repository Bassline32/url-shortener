package com.example.url_shortener.dto.response;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class ShortUrlResponse {
    Long id;
    String originalUrl;
    String shortCode;
    Long clickCount;
    LocalDateTime active;
}
