package com.example.url_shortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateUrlRequest {

    @NotBlank(message = "Оригинальный URL не может быть пустым или состоять только из пробелов")
    @Pattern(regexp = "^https?://.*", message = "URL должен начинаться с https:// или http://")
    private String originalUrl;

    @NotBlank(message = "Кастомный код не может быть пустым или состоять только из пробелов")
    @Size(min = 3, max = 20, message = "Кастомный код должен содержать от 3 до 20 символов")
    @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "кастомный код должен содержать буквы и цифры only")
    private String customCode;

    private LocalDateTime expiresAt;

    private List<String> tags;
}
