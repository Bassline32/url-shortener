package com.example.url_shortener.mapper;


import com.example.url_shortener.entity.ShortUrlEntity;
import com.example.url_shortener.model.ShortUrl;

import java.time.LocalDateTime;
import java.util.Objects;

public class ShortUtlMapper {


    public static ShortUrl mapUrlEntityToDto(ShortUrlEntity shortUrlEntity) {
        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setCreatedAt(shortUrlEntity.getCreatedAt());
        shortUrl.setShortCode(shortUrlEntity.getShortCode());
        shortUrl.setOriginalUrl(shortUrlEntity.getOriginalUrl());

        shortUrl.setClickCount(Objects.requireNonNullElse(shortUrlEntity.getClickCount(), 0));

        shortUrl.setExpiresAt(shortUrlEntity.getExpiresAt() == null ?
                LocalDateTime.now().plusDays(20) : shortUrlEntity.getExpiresAt());

        return shortUrl;
    }

}
