package com.example.url_shortener.dto.request;

import lombok.Data;

@Data
public class UrlFilterRequest {

    private Long userId;

    private String tag;

    private Boolean activeOnly;

}
