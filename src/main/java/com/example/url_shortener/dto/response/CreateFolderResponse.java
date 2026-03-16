package com.example.url_shortener.dto.response;

import lombok.Value;

@Value
public class CreateFolderResponse {
    Long id;
    String name;
    Long parentId;
}
