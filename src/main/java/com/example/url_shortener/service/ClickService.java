package com.example.url_shortener.service;


import com.example.url_shortener.entity.ClickEntity;
import com.example.url_shortener.entity.ShortUrlEntity;
import com.example.url_shortener.repository.ClickRepository;
import com.example.url_shortener.repository.UrlRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClickService {

    private final ClickRepository clickRepository;
    private final UrlRepository urlRepository;

    @Autowired
    public ClickService(ClickRepository clickRepository, UrlRepository urlRepository) {
        this.clickRepository = clickRepository;
        this.urlRepository = urlRepository;
    }

    //сохраняем новый клик в бд
    public void save(ClickEntity click) {
        clickRepository.save(click);
    }

    //возвращаем список всех кликов
    public List<ClickEntity> getAllCliks() {
        return clickRepository.findAll();
    }

    //Возвращаем количество кликов по указанному короткому коду
    // Stream API
    public List<ClickEntity> getClickCountByShortCode(String shortCode) {
        //находим сущность  ссылки
        ShortUrlEntity urlEntity =urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("Не найдена короткая ссылка"));

        //возвращаем клики  оп сущности
        return clickRepository.findByShortUrl(urlEntity);



    }

    @Async("taskExecutor")
    //метод для отслеживания кликов и информации по ним
    public void trackClick(String shortCode, HttpServletRequest request) {
        ClickEntity click = new ClickEntity();
       // click.setShortCode(shortCode);
      //  click.setTimestamp(LocalDateTime.now());
        //возвращаем ip фдрес клинта (71)
        click.setIpAddress(request.getRemoteAddr());
        click.setUserAgent(request.getHeader("User-Agent"));
        //используется для сохранения информации о том,
        // с какой страницы пришел пользователь, в объекте click (75)
        //@TODO ПОДУМАТЬ ОТКУДА БРАТЬ РЕФЕР И ЗАЧЕМ
        click.setReferer("Referer");
        clickRepository.save(click);
    }
}
