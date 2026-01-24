package com.example.url_shortener.service;


import com.example.url_shortener.model.Click;
import com.example.url_shortener.model.ShortUrl;
import com.example.url_shortener.repository.ClickRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnalyticsService {

    private final UrlService urlService;
    private final ClickService clickService;
    private final ClickRepository clickRepository;

    public AnalyticsService(UrlService urlService, ClickService clickService, ClickRepository clickRepository) {
        this.urlService = urlService;
        this.clickService = clickService;
        this.clickRepository = clickRepository;
    }

    // Возвращаю общую статистику по одной ссылке

    public int getShortUrlStatus(String shortCode) {
        //получаю ссылку по короткому коду
        Optional<ShortUrl> url = Optional.ofNullable(urlService.getUrlByShortCode(shortCode));
        // получаем список кликов по ссылке
        List<Click> clicks = clickRepository.findByAllShortCode(shortCode);
        // подчёт общего количества кликов
        long totalClicks = clicks.size();
        //считаем уникальные IP
        long uniqueIp = clicks.stream()
                .map(Click::getIpAddress)
                .distinct()
                .count();

    }
}





