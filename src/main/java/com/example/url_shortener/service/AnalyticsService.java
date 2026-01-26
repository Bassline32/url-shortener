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
import java.util.function.Function;
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
        long totalClicks = clicks.size();

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

        //получаем рефереры
        List<AnalyticsResponse.RefererStatus> topReferers = clicks.stream()
                //получаем реферер из каждого клика
                .map(Click::getReferer)
                //отфильтровываем клики без рефереров(лямбда выражение)
                .filter(referer -> referer != null && referer.isEmpty())
                //раскладвываю клики по реферерам и считаю их значение
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                //беру все пары ключ значение из мапы и преобразую их в поток
                .entrySet().stream()
                //сортируем пары ключ значения начиная с самых популярных
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                //оставляем первые пять элементов в потоке
                .limit(5)
                //преобразуем каждый элемент потока в новый объект.
                // Это полезно, когда нужно создать новую структуру данных на основе существующих данных
                .map(entry -> new AnalyticsResponse.RefererStatus(entry.getKey(), entry.getValue()))
                //преобразую поток в список
                .toList();


        //получаем браузеры
        List<AnalyticsResponse.BrowserStatus> topBrowsers = clicks.stream()
                //получаем браузер из каждого клика
                .map(Click::getUserAgent)
                .filter(browser -> browser != null && browser.isEmpty())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(entry -> new AnalyticsResponse.BrowserStatus(entry.getKey(), entry.getValue()))
                .toList();


        return new AnalyticsResponse.DetailedAnalitics(
                totalClicks,
                uniqueIps,
                clicksByDate,
                clicksByHour,
                topReferers,
                topBrowsers
        );
    }
}




