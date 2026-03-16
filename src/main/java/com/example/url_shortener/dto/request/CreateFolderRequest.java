package com.example.url_shortener.dto.request;


public record CreateFolderRequest(
        String name,
        Long parentId
) {}
