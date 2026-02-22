package com.example.url_shortener.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "short_url")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortUrlEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "short_code", nullable = false, unique = true, length = 20)
    // "abc123"
    private String shortCode;

    @Column(name = "original_url", nullable = false, length = 2048)
    // "https://example.com/very/long/path"
    private String originalUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    //используется для хранения даты и времени,
    // когда была создана сокращённая ссылка.
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    // nullable — может быть бессрочной
    private LocalDateTime expiresAt = LocalDateTime.now().plusDays(20);

    @Column(name = "click_count", nullable = false)
    //счётчик переходов по ссылке
    private Integer clickCount = 0;

}
