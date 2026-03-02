package com.example.url_shortener.service;

import com.example.url_shortener.dto.CreateUrlRequest;
import com.example.url_shortener.entity.ShortUrlEntity;
import com.example.url_shortener.entity.Tag;
import com.example.url_shortener.entity.User;
import com.example.url_shortener.repository.ClickRepository;
import com.example.url_shortener.repository.ShortUrlRepository;
import com.example.url_shortener.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UrlService {

    private final ShortUrlRepository shortUrlRepository;
    private final ClickRepository clickRepository;
    private final ShortCodeGenerator shortCodeGenerator;
    private final TagRepository tagRepository;
    private final ShortCodeValidator codeValidator;

    //генерируем уникальный код(вспомогательный метод)
    private String generateUniqueCode() {
        String code;
        do {
            code = shortCodeGenerator.generate(7);
        } while (shortUrlRepository.existsByShortCode(code));
        return code;
    }

    //создание ссылки
    @Transactional
    //определяем shortCode
    public ShortUrlEntity createShortUrl(CreateUrlRequest request, User user) {
        String shortCode = request.getCustomCode() != null
                ? codeValidator.validate(request.getCustomCode())
                : generateUniqueCode();

        //сорздаём сущность
        ShortUrlEntity shortUrlEntity = ShortUrlEntity.builder()
                .shortCode(shortCode)
                .originalUrl(request.getOriginalUrl())
                .user(user)
                .expiresAt(request.getExpiresAt())
                .build();

        //добавляем теги, если есть
        if (request.getTags() != null) {
            request.getTags()
                    .forEach(tagName ->
                            {
                                Tag tag = tagRepository.findByName(tagName)
                                        .orElseGet(() -> tagRepository.save(
                                                Tag.builder().name(tagName).build()
                                        ));
                                shortUrlEntity.addTag(tag);
                            }
                    );
        }


        return shortUrlRepository.save(shortUrlEntity);
    }


}
