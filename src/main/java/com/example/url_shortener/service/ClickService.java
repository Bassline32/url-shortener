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
        ShortUrlEntity urlEntity = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("Не найдена короткая ссылка"));

        //возвращаем клики  оп сущности
        return clickRepository.findByShortUrl(urlEntity);


    }

    @Async("taskExecutor")
    //метод для отслеживания кликов и информации по ним
    public void trackClick(ShortUrlEntity urlEntity, HttpServletRequest request) {
        //добавляю логирование
        System.out.println("Async thread: " + Thread.currentThread().getName()
                + "Virtual Thread: " + Thread.currentThread().isVirtual());

        try {
            Thread.sleep(2000);
            ClickEntity click = new ClickEntity();
            click.setShortUrl(urlEntity);
            click.setIpAddress(request.getRemoteAddr());
            click.setUserAgent(request.getHeader("User-Agent"));
            click.setReferer(request.getHeader("Referer"));
            click.setClickedAt(LocalDateTime.now());

            clickRepository.save(click);

            //обновляю счётчик кликов
            urlEntity.incrementClickCount();
            urlRepository.save(urlEntity);

        } catch (Exception e) {
            System.err.println("Ошибка в отслеживании клика для этого короткого кода "
                    + urlEntity.getShortCode() + e.getMessage());
        }
    }
}
