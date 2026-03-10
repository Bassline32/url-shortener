package com.example.url_shortener.dto.response;

import lombok.Value;

@Value
public class ShortUrlResponse {
    Long id;
    String originalUrl;
    String shortCode;
    Long clickCount;
    Boolean active;
}
