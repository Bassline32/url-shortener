package com.example.url_shortener.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

//  статистика перехода
@Data
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

    public Click() {
    }


}
