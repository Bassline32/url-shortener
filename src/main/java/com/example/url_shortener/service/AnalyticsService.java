package com.example.url_shortener.service;


import com.example.url_shortener.dto.response.AnalyticsResponse;
import com.example.url_shortener.dto.ServiceStats;
import com.example.url_shortener.dto.UrlAnalytics;
import com.example.url_shortener.entity.ClickEntity;
import com.example.url_shortener.entity.ShortUrlEntity;
import com.example.url_shortener.exception.UrlNotFoundException;
import com.example.url_shortener.mapper.ShortUtlMapper;
import com.example.url_shortener.model.ShortUrl;
import com.example.url_shortener.repository.ClickRepository;
import com.example.url_shortener.repository.ShortUrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {

    private final ShortUrlRepository urlRepository;
    private final ClickRepository clickRepository;
    private final UrlService urlService;
    private final ClickService clickService;

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

    // Возвращаю общую статистику по одной ссылке

    public AnalyticsResponse.ShortUrlStatus getShortUrlStatus(String shortCode) {
        // получаем ссылку по короткому коду и проверяем есть ли она
        ShortUrl url = urlService.getUrlByShortCode(shortCode);
        if (url == null) {
            throw new RuntimeException("Link not Found");
        }
        // получаем список кликов по ссылке
        List<com.example.url_shortener.entity.ClickEntity> clicks = clickRepository.findByShortCode(shortCode);
        // подчёт общего количества кликов
        long totalClicks = clicks.size();
        //считаем уникальные IP
        long uniqueIp = clicks.stream()
                .map(com.example.url_shortener.entity.ClickEntity::getIpAddress)
                .distinct()
                .count();

        //Определяем, когда был последний клик по ссылке.
        // если кликов не было, то возвращаем null, а если были, то берём время последнего клика.
        LocalDateTime lastClickAt = clicks.isEmpty() ? null : clicks.get(clicks.size() - 1).getTimestamp();


        return new AnalyticsResponse.ShortUrlStatus(
                shortCode,
                url.getOriginalUrl(),
                (int) totalClicks,
                (int) uniqueIp,
                url.getCreatedAt(),
                lastClickAt
        );
    }

    // Возвращаю детальную аналитику по ссылке
    public AnalyticsResponse.DetailedAnalitics getDetailedAnalitics(String shortcode) {
        ShortUrl url = urlService.getUrlByShortCode(shortcode);
        // проверка на наличие ссылки
        if (url == null) {
            throw new UrlNotFoundException(shortcode);
        }
        //получаем список всех кликов по короткой ссылке (делаем список элементов)
        List<ClickEntity> clicks = clickService.getClickCountByShortCode(shortcode);

        //получаем общее количество кликов (считаем элементы в списке)
        long totalClicks = clicks.size();

        //получаем уникальных посетителей
        long uniqueIps = clicks.stream().
                map(ClickEntity::getIpAddress).
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
                .map(ClickEntity::getReferer)
                //отфильтровываем клики без рефереров(лямбда выражение)
                .filter(referer -> referer != null && !referer.isEmpty())
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
                .map(ClickEntity::getUserAgent)
                .filter(browser -> browser != null && !browser.isEmpty())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(entry -> new AnalyticsResponse.BrowserStatus(entry.getKey(), entry.getValue()))
                .toList();


        return new AnalyticsResponse.DetailedAnalitics(
                (int) totalClicks,
                (int) uniqueIps,
                clicksByDate,
                clicksByHour,
                topReferers,
                topBrowsers
        );
    }

    //собираем общую аналитику по коротким ссылкам и возвращаем её в виде объекта
    // AnalyticsResponse

    public AnalyticsResponse.SummaryAnaliticsResponse getAnaliticsSummary() {

        //получим все ссылки
        List<ShortUrl> allUrls = urlRepository.findAll()
                .stream()
                .map(ShortUtlMapper::mapUrlEntityToDto)
                .toList();


        //получим текущее время
        LocalDateTime now = LocalDateTime.now();

        //общее количество ссылок
        int totalUrls = allUrls.size();

        //активные ссылки
        int activeUrls = (int) allUrls.stream()
                .filter(url -> url.getExpiresAt().isAfter(now))
                .count();

        //просроченные ссылки
        int expiredUrls = totalUrls - activeUrls;

        //общее количество кликов
        int totalClick = allUrls.stream()
                .mapToInt(ShortUrl::getClickCount)
                .sum();

        //количество кликов за сегодня
        int todayClicks = allUrls.stream()
                .filter(url -> url.getCreatedAt()
                        .toLocalDate().equals(now.toLocalDate()))
                .mapToInt(ShortUrl::getClickCount)
                .sum();

        //среднее количестов кликов на ссылку
        double averageClickPerUrl = totalUrls == 0 ? 0.0 : (double) totalClick / totalUrls;


        //самая популярная ссылка
        ShortUrl mostPopularUrl = allUrls.stream()
                .max(Comparator.comparingInt(ShortUrl::getClickCount))
                .orElse(null);

        //количество ссылок, созданных сегодня
        int urlCreatedToday = (int) allUrls.stream().filter(url -> url.getCreatedAt()
                        .toLocalDate()
                        .equals(now.toLocalDate()))
                .count();

        //количество кликов за последнюю неделю
        Map<LocalDate, Long> clicksLastWeek = allUrls.stream()
                .filter(url -> url.getCreatedAt().toLocalDate().isAfter(now.minusDays(7).toLocalDate()))
                .collect(Collectors.groupingBy(url -> url.getCreatedAt().toLocalDate(),
                        Collectors.summingLong(ShortUrl::getClickCount)));

        AnalyticsResponse.SummaryAnaliticsResponse response = new AnalyticsResponse.SummaryAnaliticsResponse();

        response.setActiveUrls(activeUrls);
        response.setTodayClicks(todayClicks);
        response.setExpiredUrls(expiredUrls);
        response.setMostPopularUrl(mostPopularUrl);
        response.setClicksLastWeek(clicksLastWeek);
        response.setTotalClicks(totalClick);
        response.setTotalUrls(totalUrls);
        response.setAverageClicksPerUrl(averageClickPerUrl);
        response.setUrlsCreatedToday(urlCreatedToday);


        return response;
    }


}




