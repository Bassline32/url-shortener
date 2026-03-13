package com.example.url_shortener.dto.response;

import lombok.Value;

@Value
public class PopularTagResponse {
    Long id;
    String name;
    Long urlCount;
}
