package com.example.url_shortener.service;

import com.example.url_shortener.dto.CreateUrlRequest;
import com.example.url_shortener.dto.UrlFilterRequest;
import com.example.url_shortener.entity.ClickEntity;
import com.example.url_shortener.entity.ShortUrlEntity;
import com.example.url_shortener.entity.Tag;
import com.example.url_shortener.entity.User;
import com.example.url_shortener.exception.UrlExpiredException;
import com.example.url_shortener.exception.UrlNotFoundException;
import com.example.url_shortener.repository.ClickRepository;
import com.example.url_shortener.repository.ShortUrlRepository;
import com.example.url_shortener.repository.TagRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


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

    //проверка на expired(вспомогательный метод)
    private boolean isExpired(ShortUrlEntity shortUrlEntity) {
        return shortUrlEntity.getExpiresAt() != null &&
                shortUrlEntity.getExpiresAt().isBefore(LocalDateTime.now());
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

    //получение и трекинг клика
    @Transactional
    public ShortUrlEntity shortUrlEntity(String shorCode, HttpServletRequest request) {
        ShortUrlEntity shortUrlEntity = shortUrlRepository.findByShortCode(shorCode)
                .orElseThrow(() -> new UrlNotFoundException(shorCode));

        if (isExpired(shortUrlEntity)) {
            throw new UrlExpiredException(shorCode);
        }

        //сохраняем клик
        ClickEntity click = ClickEntity.builder()
                .shortUrl(shortUrlEntity)
                //возвращает IP‑адрес клиента, который сделал HTTP‑запрос.
                .ipAddress(request.getRemoteAddr())
                .userAgent(request.getHeader("User-agent"))
                .referer(request.getHeader("Referer"))
                .build();
        clickRepository.save(click);

        // увеличиваем счётчик переходов и сохраняем
        // обновлённую сущность ссылки в базе
        // Инкрементируем счётчик
        shortUrlEntity.incrementClickCount();
        shortUrlRepository.save(shortUrlEntity);
        return shortUrlEntity;
    }

    //поиск с фильтрами
    //Найди ссылки по фильтрам,
    // но верни только ту часть результата, которую описывает pageable
    public Page<ShortUrlEntity> findWithFilter(UrlFilterRequest filter, Pageable pageable) {
        // Используем Specification для динамических фильтров
        Specification<ShortUrlEntity> spec = Specification.allOf();

        if (filter.getUserId() != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("user").get("id"), filter.getUserId()));
        }

        if (filter.getTag() != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.isMember(filter.getTag(), root.get("tags")));
        }

        if (filter.getActiveOnly() != null && filter.getActiveOnly()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.isNull(root.get("expiresAt")),
                            criteriaBuilder.greaterThan(root.get("expiresAt"), LocalDateTime.now()))
            );
        }
        return shortUrlRepository.findAll(spec, pageable);
    }








}
