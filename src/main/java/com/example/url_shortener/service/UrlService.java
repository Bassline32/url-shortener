package com.example.url_shortener.service;

import com.example.url_shortener.dto.request.CreateUrlRequest;
import com.example.url_shortener.dto.request.UrlFilterRequest;
import com.example.url_shortener.dto.response.UserStatsResponse;
import com.example.url_shortener.entity.ClickEntity;
import com.example.url_shortener.entity.ShortUrlEntity;
import com.example.url_shortener.entity.Tag;
import com.example.url_shortener.entity.User;
import com.example.url_shortener.exception.UrlExpiredException;
import com.example.url_shortener.exception.UrlNotFoundException;
import com.example.url_shortener.mapper.ShortUtlMapper;
import com.example.url_shortener.model.ShortUrl;
import com.example.url_shortener.repository.ClickRepository;
import com.example.url_shortener.repository.ShortUrlRepository;
import com.example.url_shortener.repository.TagRepository;
import com.example.url_shortener.repository.UrlRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UrlService {

    private final ShortUrlRepository shortUrlRepository;
    private final ClickRepository clickRepository;
    private final ShortCodeGenerator shortCodeGenerator;
    private final TagRepository tagRepository;
    private final ShortCodeValidator codeValidator;
    private final UrlRepository urlRepository;

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

    @Transactional
    //удаляем юрл по короткому коду
    public void deleteUrl(String shortCode) {
        urlRepository.deleteByShortCode(shortCode);
    }

    //получаем все юрл
    public List<ShortUrl> getAllUrls() {
        return urlRepository.findAll().stream()
                .map(ShortUtlMapper::mapUrlEntityToDto)
                .toList();
    }

    //получение короткой ссылки по короткому коду.
    public ShortUrl getUrlByShortCode(String shortCode) {
        Optional<ShortUrlEntity> optionalShortUrl = urlRepository.findByShortCode(shortCode);
        return optionalShortUrl.map(ShortUtlMapper::mapUrlEntityToDto)
                .orElse(null);

    }

    //пагинция и сортировка
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

    //поиск по ключевому слову
    public List<ShortUrl> searchUrlByKeyWord(String keyword) {
        return urlRepository.findByOriginalUrlContaining(keyword).stream()
                .map(ShortUtlMapper::mapUrlEntityToDto)
                .toList();
    }

    @Transactional
    public List<ShortUrl> getActualUrls() {
        LocalDateTime now = LocalDateTime.now();
        return urlRepository.findByExpiresAtAfterOrExpiresAtIsNull(now).stream()
                .map(ShortUtlMapper::mapUrlEntityToDto)
                .toList();
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

    public Page<ShortUrlEntity> getUrlsForUser(User user, int page, int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return shortUrlRepository.findByUser(user, pageable);
    }

    public UserStatsResponse getStatsForUser(User user) {
        long totalUrls = shortUrlRepository.countByUser(user);
        int activeByDate = shortUrlRepository.countByUserAndExpiresAtAfter(user, LocalDateTime.now());
        int activeWithoutExpry = shortUrlRepository.countByUserAndExpiresAtIsNull(user);
        long activeUrls = activeByDate + activeWithoutExpry;
        long totalClicks = user.getUrls().stream().mapToLong(
                        ShortUrlEntity::getClickCount)
                .sum();
        return new UserStatsResponse(totalClicks, totalUrls, activeUrls);
    }

    public List<ShortUrlEntity> getUrlByTag(User user, String tagName) {
        Tag tag = tagRepository.findByName(tagName)
                .orElseThrow(() -> new IllegalArgumentException("Такой тег не найден"));

        //вот тут возвращаем тег, который есть у урл конкретного пользователя
        return tag.getUrls().stream()
                .filter(e -> e.getUser().equals(user))
                .toList();
    }

    @Transactional
    public ShortUrlEntity updateTags(User user, String shortCode, List<String> tagNames) {
        ShortUrlEntity url = shortUrlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));

        //если ссылка принадлежит другому пользователю
        if (!url.getUser().equals(user)) {
            throw new SecurityException("НЕТ");
        }

        //создаём или находим теги
        var newTags = tagNames.stream()
                //превращаем имя тега в сущность Tag
                .map(name -> tagRepository.findByName(name)
                        .orElseGet(() -> tagRepository.save(Tag.builder().name(name).build()))
                ).collect(Collectors.toSet());

        //обновляем теги
        url.setTags(newTags);

        return shortUrlRepository.save(url);
    }


}
