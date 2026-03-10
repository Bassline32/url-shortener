package com.example.url_shortener.dto.response;

import lombok.Value;

@Value
public class UserStatsResponse {
    Long totalUrls;
    Long totalClicks;
    Long activeUrls;
}
