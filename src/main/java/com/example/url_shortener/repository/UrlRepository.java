package com.example.url_shortener.repository;

import com.example.url_shortener.entity.ShortUrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<ShortUrlEntity, String> {

    Optional<ShortUrlEntity> findByShortCode(String shortCode);

    void deleteByShortCode(String shortcode);

    //поиск по домену
    @Query("SELECT u FROM ShortUrlEntity u WHERE u.originalUrl LIKE %:domain% AND LOCATE('.', u.originalUrl) > 0")
    List<ShortUrlEntity> findByOriginalUrlContainingDomain(@Param("domain") String domain);

    //поиск по ключевому слову
    List<ShortUrlEntity> findByOriginalUrlContaining(String keyword);


    //получаем просроченные ссылки
    List<ShortUrlEntity> findByExpiresAtBefore(LocalDateTime now);

    //получаем активные ссылки
    List<ShortUrlEntity> findByExpiresAtAfter(LocalDateTime now);

    //проверка существования короткой ссылки по коду
    boolean existsByShortCode(String shortCode);
}
