package com.example.url_shortener.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;



@Data
@NoArgsConstructor
public class ShortUrl {

    @NotNull
    // "abc123"
    private String shortCode;

    @NotNull
    // "https://example.com/very/long/path"
    private String originalUrl;

    @NotNull
    //используется для хранения даты и времени,
    // когда была создана сокращённая ссылка.
    private LocalDateTime createdAt;

    // nullable — может быть бессрочной
    private LocalDateTime expiresAt;

    //счётчик переходов по ссылке
    private int clickCount;

}
