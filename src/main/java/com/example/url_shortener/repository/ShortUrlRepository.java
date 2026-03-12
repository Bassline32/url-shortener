package com.example.url_shortener.repository;

import com.example.url_shortener.entity.ShortUrlEntity;
import com.example.url_shortener.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShortUrlRepository extends JpaRepository<ShortUrlEntity, Long>,
        JpaSpecificationExecutor<ShortUrlEntity> {

    //поиск по ShortCode
    Optional<ShortUrlEntity> findByShortCode(String shortCode);

    // Проверка существования
    boolean existsByShortCode(String shortCode);

    //ссылки пользователя
    List<ShortUrlEntity> findByUserIdOrderByCreatedAtDesc(Long userId);

    //поиск по домену (JPQL)
    @Query("SELECT s FROM ShortUrlEntity s WHERE s.originalUrl LIKE %:domain%")
    List<ShortUrlEntity> findByDomain(@Param("domain") String domain);

    //активные ссылки (не истёкшие)
    @Query("SELECT s FROM ShortUrlEntity s WHERE s.expiresAt > :now")
    List<ShortUrlEntity> findAllActive(@Param("now") LocalDateTime now);

    //истёкшие ссылки
    @Query("SELECT s FROM ShortUrlEntity s WHERE s.expiresAt <= :now")
    List<ShortUrlEntity> findAllExpired(@Param("now") LocalDateTime now);

    //топ по кликам
    List<ShortUrlEntity> findTop10ByOrderByClickCountDesc();

    //поиск по тегу
    // JOIN s.tags t: Указывает, что мы хотим соединить сущность ShortUrlEntity с коллекцией tags,
    // которая является полем сущности ShortUrlEntity.
    // Результат соединения будет представлен переменной t.
    @Query("SELECT s FROM ShortUrlEntity s JOIN s.tags t WHERE t.name = :tagName")
    List<ShortUrlEntity> findByTagName(@Param("tagName") String tagName);

    // с пагинацией
    Page<ShortUrlEntity> findByUserId(Long userId, Pageable pageable);

    //подчёт ссылок пользователя
    Long countByUserId(Long userId);

    //ссылки созданные за период
    @Query("SELECT s FROM ShortUrlEntity s WHERE s.createdAt BETWEEN :start AND :end")
    List<ShortUrlEntity> findCreatedBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    Page<ShortUrlEntity> findByUser(User user, Pageable pageable);

    int countByUser (User user);

    int countByUserAndExpiresAtAfter (User user, LocalDateTime now);

    //если ссылка бессрочная, то она тоже активна
    int countByUserAndExpiresAtIsNull(User user);
}
