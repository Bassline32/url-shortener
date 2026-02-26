package com.example.url_shortener.repository;

import com.example.url_shortener.model.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {

    //поиск по ShortCode
    Optional<ShortUrl> findByShortCode(String shortCode);

    // Проверка существования
    boolean existsByShortCode(String shortCode);

    //ссылки пользователя
    List<ShortUrl> findByUserIdOrderByCreatedAtDesc(Long userId);






}
