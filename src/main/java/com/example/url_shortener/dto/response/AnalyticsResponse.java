package com.example.url_shortener.dto.response;

import com.example.url_shortener.model.ShortUrl;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


public class AnalyticsResponse {

    @Data
    @AllArgsConstructor
    public static class ShortUrlStatus {
        private String shortCode;
        private String originalUrl;
        private int totalClicks;
        private int uniqueVisitors;
        private LocalDateTime createdAt;
        private LocalDateTime lastClickAt;

    }

    @Data
    @AllArgsConstructor
    public static class RefererStatus {
        private String referer;
        private long count;

    }

    @Data
    @AllArgsConstructor
    public static class BrowserStatus {
        private String browser;
        private long count;

    }

    @Data
    @AllArgsConstructor
    public static class DetailedAnalitics {
        private int totalClicks;
        private int uniqueIps;
        private Map<LocalDate, Long> clicksByDate;
        private Map<Integer, Long> clicksByHour;
        private List<RefererStatus> topReferers;
        private List<BrowserStatus> topBrowsers;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SummaryAnaliticsResponse {
        private int totalUrls;
        private int activeUrls;
        private int expiredUrls;
        private int totalClicks;
        private int todayClicks;
        private double AverageClicksPerUrl;
        private ShortUrl mostPopularUrl;
        private int urlsCreatedToday;
        private Map<LocalDate, Long> clicksLastWeek;
    }

}
