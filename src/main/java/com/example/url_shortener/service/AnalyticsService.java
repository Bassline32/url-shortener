package com.example.url_shortener.service;


import com.example.url_shortener.model.Click;
import com.example.url_shortener.model.ShortUrl;
import com.example.url_shortener.repository.ClickRepository;
import dto.AnalyticsResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final UrlService urlService;
    private final ShortUrl shortUrl;
    private final ClickRepository clickRepository;
    private final AnalyticsResponse analyticsResponse;
    public final ClickService clickService;

    public AnalyticsService(UrlService urlService, ShortUrl shortUrl, ClickRepository clickRepository, AnalyticsResponse analyticsResponse, ClickService clickService) {
        this.urlService = urlService;
        this.shortUrl = shortUrl;

        this.clickRepository = clickRepository;
        this.analyticsResponse = analyticsResponse;
        this.clickService = clickService;
    }

    // Возвращаю общую статистику по одной ссылке

    public AnalyticsResponse.ShortUrlStatus getShortUrlStatus(String shortCode) {
// получаем ссылку по короткому коду и проверяем есть ли она
        ShortUrl url = urlService.getUrlByShortCode(shortCode);
        if (url == null) {
            throw new RuntimeException("Link not Found");
        }
        // получаем список кликов по ссылке
        List<Click> clicks = clickRepository.findByAllShortCode(shortCode);
        // подчёт общего количества кликов
        long totalClicks = clicks.size();
        //считаем уникальные IP
        long uniqueIp = clicks.stream()
                .map(Click::getIpAddress)
                .distinct()
                .count();

        //Определяем, когда был последний клик по ссылке.
        // если кликов не было, то возвращаем null, а если были, то берём время последнего клика.
        LocalDateTime lastClickAt = clicks.isEmpty() ? null : clicks.get(clicks.size() - 1).getTimestamp();


        return new AnalyticsResponse.ShortUrlStatus(
                shortCode,
                url.getOriginalUrl(),
                totalClicks,
                uniqueIp,
                url.getCreatedAt(),
                lastClickAt
        );
    }

    // Возвращаю детальную аналитику по ссылке
    public AnalyticsResponse.DetailedAnalitics getDetailedAnalitics(String shortcode) {
        ShortUrl url = urlService.getUrlByShortCode(shortcode);
        // проверка на наличие ссылки
        if (url == null) {
            throw new RuntimeException("Link not found");
        }
        //получаем список всех кликов по короткой ссылке (делаем список элементов)
        List<Click> clicks = clickService.getClickCountByShortCode(shortcode);

        //получаем общее количество кликов (считаем элементы в списке)
        long totalclicks = clicks.size();

        //получаем уникальных посетителей
        long uniqueIps = clicks.stream().
                map(Click::getIpAddress).
                distinct().
                count();

        //группируем клики по дате
        //тут и стим и лямбда выражения omg
        Map<LocalDate, Long> clicksByDate = clicks.stream()
                .collect(Collectors.groupingBy(c -> c.getTimestamp().toLocalDate(), //функция группироваки
                        Collectors.counting())); //функция сбора

        //группируем клики по часам
        Map<Integer, Long> clicksByHour = clicks.stream()
                .collect(Collectors.groupingBy(d -> d.getTimestamp().getHour(),
                        Collectors.counting()));


    }
}




