package com.example.url_shortener.dto;

import lombok.Data;

@Data
public class UrlFilterRequest {

    private Long userId;

    private String tag;

    private Boolean activeOnly;

}
