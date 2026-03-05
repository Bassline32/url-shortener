package com.example.url_shortener.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Data
@Value
@Builder
public class ServiceStats {

    Long totalUrls;

    Long activeUrls;

    Long totalClicks;
}
