package com.example.url_shortener.service;


import com.example.url_shortener.exception.ShortCodeAlreadyExistsException;
import com.example.url_shortener.repository.ShortUrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShortCodeValidator {

    private final ShortUrlRepository urlRepository;

    public String validate(String customCode) {
        if (!customCode.matches("^[a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException("некорректный формат кастомного кода " + customCode);
        }
            if (urlRepository.existsByShortCode(customCode)) {
                throw new ShortCodeAlreadyExistsException("Код уже используется " + customCode);
            }
        return customCode;
    }
}