package com.example.url_shortener.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//  статистика перехода
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Click {

    @NotNull
    private Long id;

    @NotNull
    private String shortCode;

    @NotNull
    private LocalDateTime timestamp;

    @NotNull
    private String ipAddress;

    @NotNull
    // "Mozilla/5.0 Chrome/120..."
    private String userAgent;

    @NotNull
    // откуда пришёл пользователь
    private String referer;

}
