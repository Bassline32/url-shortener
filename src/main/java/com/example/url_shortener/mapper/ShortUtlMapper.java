package com.example.url_shortener.mapper;


import com.example.url_shortener.dto.response.CreateShortUrlResponse;
import com.example.url_shortener.dto.response.FolderShortResponse;
import com.example.url_shortener.entity.Folder;
import com.example.url_shortener.entity.ShortUrlEntity;
import com.example.url_shortener.entity.Tag;
import com.example.url_shortener.model.ShortUrl;

public class ShortUtlMapper {


    public static ShortUrl mapUrlEntityToDto(ShortUrlEntity shortUrlEntity) {
        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setCreatedAt(shortUrlEntity.getCreatedAt());
        shortUrl.setShortCode(shortUrlEntity.getShortCode());
        shortUrl.setOriginalUrl(shortUrlEntity.getOriginalUrl());
        shortUrl.setClickCount(shortUrlEntity.getClickCount().intValue());
        shortUrl.setExpiresAt(shortUrlEntity.getExpiresAt());
        shortUrl.setId(shortUrlEntity.getId());
        return shortUrl;
    }

    public static CreateShortUrlResponse response(ShortUrlEntity entity) {
        FolderShortResponse folder = null;
        if (entity.getFolder() != null) {
            folder = new FolderShortResponse(
                    entity.getFolder().getId(),
                    entity.getFolder().getName()
            );
        }

        return new CreateShortUrlResponse(
                entity.getId(),
                entity.getShortCode(),
                "http://localhost:8080/" + entity.getShortCode(),
                entity.getOriginalUrl(),
                entity.getTags().stream()
                        .map(Tag::getName)
                        .toList(),
                folder,
                entity.getCreatedAt()
        );

    }
}
