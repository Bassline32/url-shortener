package com.example.url_shortener.Service;

import com.example.url_shortener.model.Click;
import com.example.url_shortener.model.ShortUrl;
import com.example.url_shortener.repository.ClickRepository;
import com.example.url_shortener.repository.UrlRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;


@Service
public class UrlService {


    private final UrlRepository urlRepository;
private final ClickRepository clickRepository;

    @Autowired
    public UrlService(UrlRepository urlRepository, ClickRepository clickRepository) {
        this.urlRepository = urlRepository;
        this.clickRepository = clickRepository;
    }



    public ShortUrl createShortUrl(String originalUrl) {
        String shortCode = shortCode();
        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setShortCode(shortCode);
        shortUrl.setOriginalUrl(originalUrl);
        //устанавливаем время создания ссылки
        shortUrl.setCreatedAt(LocalDateTime.now());
        return urlRepository.save(shortUrl);
    }

    //получаем все юрл
    public List<ShortUrl> getAllUrls() {
        return urlRepository.findAll();
    }

    //удаляем юрл по короткому коду
    public void deleteUrl(String shortCode) {
        urlRepository.deleteByShortCode(shortCode);
    }

    //получение короткой ссылки по короткому коду.
    public ShortUrl getUrlByShortCode(String shortCode) {
        Optional<ShortUrl> optionalShortUrl = urlRepository.findByShortCode(shortCode);
        return optionalShortUrl.orElse(null);
    }

    //генерируем shortCode
    private String shortCode() {
        String symbols = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder shortCode = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            shortCode.append(symbols.charAt(random.nextInt(symbols.length())));
        }
        return shortCode.toString();
    }

    //метод для отслеживания кликов и информации по ним
    public void trackClick(String shortCode, HttpServletRequest request) {
        Click click = new Click();
        click.setShortCode(shortCode);
        click.setTimestamp(LocalDateTime.now());
        //возвращаем ip фдрес клинта (71)
        click.setIpAddress(request.getRemoteAddr());
        click.setUserAgent(request.getHeader("User-Agent"));
        //используется для сохранения информации о том,
        // с какой страницы пришел пользователь, в объекте click (75)
        click.setReferer(request.getHeader("Referer"));
       clickRepository.save(click);
    }
}
