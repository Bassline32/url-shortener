package com.example.url_shortener.service;

import com.example.url_shortener.model.ShortUrl;
import com.example.url_shortener.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;


@Service
public class UrlService {


    private final UrlRepository urlRepository;


    @Autowired
    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;

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

    //пагинация и сортировка
    public List<ShortUrl> getUrls(int page, int size, String sortBy, String order) {
        Sort sort = Sort.by(order.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        //Инструкция по пагиации данных
        Pageable pageable = PageRequest.of(page, size, sort);

        //получаю данные из репозитория
        Page<ShortUrl> urlPage = urlRepository.findAll(pageable);

        //возвращаем отсортированную страницу
        return urlPage.getContent();
    }


}
