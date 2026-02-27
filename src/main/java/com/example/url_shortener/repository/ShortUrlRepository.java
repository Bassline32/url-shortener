package com.example.url_shortener.repository;

import com.example.url_shortener.model.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {

    //поиск по ShortCode
    Optional<ShortUrl> findByShortCode(String shortCode);

    // Проверка существования
    boolean existsByShortCode(String shortCode);

    //ссылки пользователя
    List<ShortUrl> findByUserIdOrderByCreatedAtDesc(Long userId);

    //поиск по домену (JPQL)
    @Query("SELECT s FROM ShortUrlEntity s WHERE s.originalUrl LIKE %:domain%")
    List<ShortUrl> findByDomain(@Param("domain") String domain);


}
