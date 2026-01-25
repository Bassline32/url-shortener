package dto;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class AnalyticsResponse {

    @Data
    public static class ShortUrlStatus {
        private String shortCode;
        private String originalUrl;
        private int totalClicks;
        private int uniqueVisitors;
        private LocalDateTime createdAt;
        private LocalDateTime lastClickAt;

        public ShortUrlStatus(String shortCode, @NotNull String originalUrl, long totalClicks, long uniqueIp, @NotNull LocalDateTime createdAt, LocalDateTime lastClickAt) {
        }
    }

    @Data
    public static class RefererStatus {
        private String referer;
        private long count;
    }

    @Data
    public static class BrowserStatus {
        private String browser;
        private long count;
    }

    @Data
    public static class DetailedAnalitics {
        private int totalClicks;
        private int uniqueIps;
        private Map<LocalDate, Long> clicksByDate;
        private Map<Integer, Long> clicksByHour;
        private List<RefererStatus> topReferers;
        private List<BrowserStatus> topBrowsers;
    }

    @Data
    public static class SummaryAnalitics {
        private int totalUrls;
        private int activeUrls;
        private int expiredUrls;
        private int totalClicks;
        private int todayClicks;
        private Map<LocalDate, Long> clicksLastWeek;
    }

}
