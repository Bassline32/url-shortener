package com.example.url_shortener.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "short_url")
public class ShortUrlEntity {

    @Id
    @Column(name = "short_code", nullable = false)
    // "abc123"
    private String shortCode;

    @Column(name = "original_url", nullable = false)
    // "https://example.com/very/long/path"
    private String originalUrl;

    @Column(name = "created_at", nullable = false)
    //используется для хранения даты и времени,
    // когда была создана сокращённая ссылка.
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    // nullable — может быть бессрочной
    private LocalDateTime expiresAt;

    @Column(name = "click_count")
    //счётчик переходов по ссылке
    private Integer clickCount;

}
