package com.example.url_shortener.mapper;


import com.example.url_shortener.entity.ShortUrlEntity;
import com.example.url_shortener.model.ShortUrl;

public class ShortUtlMapper {


    public static ShortUrl mapUrlEntityToDto(ShortUrlEntity shortUrlEntity) {
        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setCreatedAt(shortUrlEntity.getCreatedAt());
        shortUrl.setShortCode(shortUrlEntity.getShortCode());
        shortUrl.setOriginalUrl(shortUrlEntity.getOriginalUrl());
        shortUrl.setClickCount(shortUrlEntity.getClickCount());
        shortUrl.setExpiresAt(shortUrlEntity.getExpiresAt());
        return shortUrl;
    }

}
