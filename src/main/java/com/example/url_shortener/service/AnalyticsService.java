package com.example.url_shortener.service;


import com.example.url_shortener.dto.AnalyticsResponse;
import com.example.url_shortener.dto.ServiceStats;
import com.example.url_shortener.dto.UrlAnalytics;
import com.example.url_shortener.entity.ShortUrlEntity;
import com.example.url_shortener.exception.UrlNotFoundException;
import com.example.url_shortener.repository.ClickRepository;
import com.example.url_shortener.repository.ShortUrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {

    private final ShortUrlRepository urlRepository;
    private final ClickRepository clickRepository;

    public UrlAnalytics getAnalytics(String shortCode) {
        ShortUrlEntity shortUrlEntity = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));

        Long urlId = shortUrlEntity.getId();

        return UrlAnalytics.builder()
                .shortCode(shortCode)
                .originalUrl(shortUrlEntity.getOriginalUrl())
                .totalClicks(shortUrlEntity.getClickCount())
                .uniqueVisitors(clickRepository.countUniqueIpsByShortUrlId(urlId))
                .clicksByDate(getClicksByDate(urlId))
                .clicksByHour(getClicksByHour(urlId))
                .topReferers(getTopReferers(urlId, 5))
                .build();
    }

    private Map<LocalDate, Long> getClicksByDate(Long urlId) {
        return clickRepository.countClicksByDay(urlId).stream()
                .collect(Collectors.toMap(
                        row -> ((java.sql.Date) row[0]).toLocalDate(),
                        row -> ((Number) row[1]).longValue(),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

    }

    private Map<Integer, Long> getClicksByHour(Long urlId) {
        return clickRepository.countClicksByHour(urlId).stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).intValue(),
                        row -> ((Number) row[1]).longValue(),
                        (a, b) -> a,
                        //TreeMap - автоматически отсортировать ключи  по возрастанию
                        TreeMap::new
                ));
    }

    private List<AnalyticsResponse.RefererStatus> getTopReferers(Long urlId, int limit) {
        return clickRepository.findTopReferers(urlId, limit).stream()
                .map(row ->
                        new AnalyticsResponse.RefererStatus(
                                (String) row[0],
                                ((Number) row[1]).longValue()))
                .toList();
    }

    //общая статистика сервиса
    private ServiceStats getServiceStats() {
        LocalDateTime now = LocalDateTime.now();
        //Эта строка превращает текущее время в начало сегодняшнего дня
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();

        return ServiceStats.builder()
                .totalUrls(urlRepository.count())
                // size - считает в java коллекции
                //findAllActive(now) — кастомный метод,
                // который возвращает список сущностей
                // .size() — считает количество элементов в этом списке.
                .activeUrls((long) urlRepository.findAllActive(now).size())
                // count - считает в БД
                .totalClicks(clickRepository.count())
                .build();
    }

}




