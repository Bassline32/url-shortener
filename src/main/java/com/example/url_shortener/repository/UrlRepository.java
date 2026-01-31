package com.example.url_shortener.repository;

import com.example.url_shortener.model.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<ShortUrl, String> {

    Optional<ShortUrl> findByShortCode(String shortCode);

    void deleteByShortCode(String shortcode);

    //поиск по домену
    List<ShortUrl> findByOriginalUrlContainingDomain(String domain);

    //поиск по ключевому слову
    List<ShortUrl> findByOriginalUrlContainingKeyword(String keyword);

    //получаем просроченные ссылки
    List<ShortUrl> findByExpiresAtBefore(LocalDateTime now);

    //получаем активные ссылки
    List<ShortUrl> findByExpiresAtAfter(LocalDateTime now);

    //проверка существования короткой ссылки по коду
    boolean existByShortCode(String shortCode);
}
