package com.example.url_shortener.dto;


import com.example.url_shortener.dto.response.AnalyticsResponse;import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Value
@Builder
public class UrlAnalytics {

    String shortCode;

    String originalUrl;

    Long totalClicks;

    Long uniqueVisitors;

    Map<LocalDate, Long> clicksByDate;

    Map<Integer, Long> clicksByHour;

    List<AnalyticsResponse.RefererStatus> topReferers;
}

