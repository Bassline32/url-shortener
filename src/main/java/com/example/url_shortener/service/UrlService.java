package com.example.url_shortener.service;

import com.example.url_shortener.dto.CreateUrlRequest;
import com.example.url_shortener.exception.ShortCodeAlreadyExistsException;
import com.example.url_shortener.model.ShortUrl;
import com.example.url_shortener.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    //генерация короткого URL
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

    //получение просроченных ссылок
    public List<ShortUrl> getExpiredUrls() {
        LocalDateTime now = LocalDateTime.now();
        return urlRepository.findByExpiresAtBefore(now);
    }

    //получение активных ссылок
    public List<ShortUrl> getActualUrls() {
        LocalDateTime now = LocalDateTime.now();
        return urlRepository.findByExpiresAtAfter(now);
    }

    //получение результатов поиска по домену
    public List<ShortUrl> searchUrlsByDomain(String domain) {
        return urlRepository.findByOriginalUrlContainingDomain(domain);
    }

    //поиск по ключевому слову
    public List<ShortUrl> searchUrlByKeyWord(String keyword) {
        return urlRepository.findByOriginalUrlContainingKeyword(keyword);
    }

    //обработка ошибок, если кастомный код уже занят
    public ShortUrl createShortUrl(String originalUrl, String customCode, LocalDateTime expiresAt) {
        if (customCode != null && urlRepository.existByShortCode(customCode)) {
            throw new ShortCodeAlreadyExistsException("Такой кастомный код уже существует " + customCode);
        }

        ShortUrl shortUrl = new ShortUrl();
        //shortCode == generateShortcode
        shortUrl.setShortCode(customCode != null ? customCode : shortCode());
        shortUrl.setOriginalUrl(originalUrl);
        shortUrl.setCreatedAt(LocalDateTime.now());
        shortUrl.setExpiresAt(expiresAt);
        return urlRepository.save(shortUrl);
    }

    //массовое создание ссылкок
    public List<ShortUrl> createurls(List<CreateUrlRequest> requests) {
        List<ShortUrl> urls = new ArrayList<>();
        //для каждого элемента request в коллекции requests
        for (CreateUrlRequest request : requests) {
            ShortUrl url = createShortUrl(request.getOriginalUrl(),
                    request.getCustomCode(), request.getExpiresAt());
            urls.add(url);
        }
        return urls;
    }

    public String exportToCsv(List<ShortUrl> urls) {
        StringBuilder csv = new StringBuilder();
        //добавим заголовок csv
        csv.append("shortCode, originalUrl, createdAt, clickCount\n");
        for (ShortUrl url : urls) {
            csv.append(url.getShortCode())
        }
    }
}
