package com.example.url_shortener.controller;


import com.example.url_shortener.service.AnalyticsService;
import dto.AnalyticsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/urls")
public class AnalyticsController {

     private final AnalyticsService analyticsService;

     @Autowired
    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    //общая статистика по ссылке
    @GetMapping("/{shortCode}/stats")
    public AnalyticsResponse.ShortUrlStatus getStats(@PathVariable String shortCode) {
        return analyticsService.getShortUrlStatus(shortCode);
    }

    //детальная статистика по ссылке
    @GetMapping("/{shortCode}/analytics")
    public AnalyticsResponse.DetailedAnalitics getAnalytics(@PathVariable String shortCode) {
        return analyticsService.getDetailedAnalitics(shortCode);
    }

}
