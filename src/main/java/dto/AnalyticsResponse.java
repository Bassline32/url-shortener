package dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class AnalyticsResponse {

    @Data
    public class ShortUrlStatus {
        private String shortCode;
        private String originalUrl;
        private int totalClicks;
        private int uniqueVisitors;
        private LocalDateTime createdAt;
        private LocalDateTime lastClickAt;
    }

    @Data
    public class RefererStatus {
        private String referer;
        private long count;
    }

    @Data
    public class BrowserStatus {
        private String browser;
        private long count;
    }

    @Data
    public class DetailedAnalitics {
        private int totalClicks;
        private int uniqueIps;
        private Map<LocalDate, Long> clicksByDate;
        private Map<Integer, Long> clicksByHour;
        private List<RefererStatus> topReferers;
        private List<BrowserStatus> topBrowsers;
    }

    @Data
    public class SummaryAnalitics {
        private int totalUrls;
        private int activeUrls;
        private int expiredUrls;
        private int totalClicks;
        private int todayClicks;
        private Map<LocalDate, Long> clicksLastWeek;
    }

}
