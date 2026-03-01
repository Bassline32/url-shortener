package com.example.url_shortener.repository;

import com.example.url_shortener.entity.ClickEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClickRepository extends JpaRepository<ClickEntity, Long> {

    List<ClickEntity> findByShortCode(String shortCode);

    //клики по ссылке
    List<ClickEntity> findByShortUrlId(Long shortUrlId);

    //количество кликов
    long countByShortUrlId(Long shortUrlId);

    //клики за период
    @Query("SELECT c FROM ClickEntity c WHERE c.shortUrl.id = :urlId AND c.clickedAt BETWEEN :start AND :end")
    List<ClickEntity> findByShortUrlIdAndPeriod(
            @Param("urlId") Long urlId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    //уникальные IP для ссылки
    @Query("SELECT COUNT (DISTINCT c.ipAddress) FROM ClickEntity c WHERE c.shortUrl.id = :urlId")
    long countUniqueIpsByShortUrlId(@Param("urlId") Long urlId);

    //статистика по дням(NATIVE QUERRY)
    @Query(value = """
            SELECT DATE(clicked_at) as date, COUNT(*) as count
            FROM clicks
            WHERE short_url_id = :urlId
            GROUP BY date(clicked_at)
            ORDER BY date
            """, nativeQuery = true)
    List<Object[]> countClicksByDay(@Param("urlId") Long urlId);

    //Статистика по часам
    @Query(value = """
            SELECT EXTRACT(HOUR FROM clicked_at) as hour, COUNT(*) as count
            FROM clicks
            WHERE short_url_id = :urlId
            GROUP BY EXTRACT(HOUR FROM clicked_at)
            ORDER BY hour
            """, nativeQuery = true)
    List<Object[]> countClicksByHour(@Param("urlId") Long urlID);

    //Топ рефереров
    @Query(value = """
        SELECT referer, COUNT(*) as count 
        FROM clicks 
        WHERE short_url_id = :urlId AND referer IS NOT NULL 
        GROUP BY referer 
        ORDER BY count DESC 
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findTopReferers(@Param("urlId") Long urlId, @Param("limit") int limit);

    //удалить старые клики
    @Modifying
    @Query(" DELETE FROM ClickEntity c WHERE c.clickedAt < :date")
    int deleteOlderThan(@Param("date")LocalDateTime date);

}
