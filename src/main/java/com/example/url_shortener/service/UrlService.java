package com.example.url_shortener.service;

import com.example.url_shortener.dto.CreateUrlRequest;
import com.example.url_shortener.entity.ShortUrlEntity;
import com.example.url_shortener.exception.ShortCodeAlreadyExistsException;
import com.example.url_shortener.mapper.ShortUtlMapper;
import com.example.url_shortener.model.ShortUrl;
import com.example.url_shortener.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        ShortUrlEntity shortUrlEntity = new ShortUrlEntity();
        shortUrlEntity.setShortCode(shortCode);
        shortUrlEntity.setOriginalUrl(originalUrl);
        //устанавливаем время создания ссылки
        shortUrlEntity.setCreatedAt(LocalDateTime.now());
        ShortUrlEntity savedShortUrl = urlRepository.save(shortUrlEntity);
        return ShortUtlMapper.mapUrlEntityToDto(savedShortUrl);
    }


    //получаем все юрл
    public List<ShortUrl> getAllUrls() {
        return urlRepository.findAll().stream()
                .map(ShortUtlMapper::mapUrlEntityToDto)
                .toList();
    }

    //удаляем юрл по короткому коду
    @Transactional
    public void deleteUrl(String shortCode) {
        urlRepository.deleteByShortCode(shortCode);
    }

    //получение короткой ссылки по короткому коду.
    public ShortUrl getUrlByShortCode(String shortCode) {
        Optional<ShortUrlEntity> optionalShortUrl = urlRepository.findByShortCode(shortCode);

        return optionalShortUrl.map(ShortUtlMapper::mapUrlEntityToDto)
                .orElse(null);


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
        Page<ShortUrlEntity> urlPage = urlRepository.findAll(pageable);

        //возвращаем отсортированную страницу
        return urlPage
                .map(ShortUtlMapper::mapUrlEntityToDto)
                .getContent();
    }

    //получение просроченных ссылок
    public List<ShortUrl> getExpiredUrls() {
        LocalDateTime now = LocalDateTime.now();
        return urlRepository.findByExpiresAtBefore(now).stream()
                .map(ShortUtlMapper::mapUrlEntityToDto)
                .toList();
    }

    //получение активных ссылок
    // TODO ПОПРАВИТЬ ВЫВОД актуальных ссылок
    @Transactional
    public List<ShortUrl> getActualUrls() {
        LocalDateTime now = LocalDateTime.now();
        return urlRepository.findByExpiresAtAfterOrExpiresAtIsNull(now).stream()
                .map(ShortUtlMapper::mapUrlEntityToDto)
                .toList();
    }

    //получение результатов поиска по домену
    public List<ShortUrl> searchUrlsByDomain(String domain) {
        return urlRepository.findByOriginalUrlContainingDomain(domain).stream()
                .map(ShortUtlMapper::mapUrlEntityToDto)
                .toList();
    }

    //поиск по ключевому слову
    public List<ShortUrl> searchUrlByKeyWord(String keyword) {
        return urlRepository.findByOriginalUrlContaining(keyword).stream()
                .map(ShortUtlMapper::mapUrlEntityToDto)
                .toList();
    }

    //обработка ошибок, если кастомный код уже занят
    public ShortUrl createShortUrl(String originalUrl, String customCode, LocalDateTime expiresAt) {
        if (customCode != null && urlRepository.existsByShortCode(customCode)) {
            throw new ShortCodeAlreadyExistsException("Такой кастомный код уже существует " + customCode);
        }

        ShortUrlEntity shortUrlEntity = new ShortUrlEntity();
        //shortCode == generateShortcode
        shortUrlEntity.setShortCode(customCode != null ? customCode : shortCode());
        shortUrlEntity.setOriginalUrl(originalUrl);
        shortUrlEntity.setCreatedAt(LocalDateTime.now());
        shortUrlEntity.setExpiresAt(expiresAt);

        return ShortUtlMapper.mapUrlEntityToDto(urlRepository.save(shortUrlEntity));
    }

    //массовое создание ссылкок
    public List<ShortUrl> createUrls(List<CreateUrlRequest> requests) {
        List<ShortUrl> urls = new ArrayList<>();
        //для каждого элемента request в коллекции requests
        for (CreateUrlRequest request : requests) {
            ShortUrl url = createShortUrl(request.getOriginalUrl(),
                    request.getCustomCode(), request.getExpiresAt());
            urls.add(url);
        }
        return urls;
    }

    //экспорт данных в CSV формат
    public String exportToCsv(List<ShortUrl> urls) {
        StringBuilder csv = new StringBuilder();
        //добавим заголовок csv
        csv.append("shortCode, originalUrl, createdAt, clickCount\n");
        for (ShortUrl url : urls) {
            csv.append(url.getShortCode()).append(",")
                    .append(url.getOriginalUrl()).append(",")
                    .append(url.getCreatedAt()).append(",")
                    .append(url.getClickCount()).append("\n");
        }
        return csv.toString();
    }
}
