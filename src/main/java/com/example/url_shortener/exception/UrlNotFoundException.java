package com.example.url_shortener.exception;

public class UrlNotFoundException extends RuntimeException {
    public UrlNotFoundException(String shorCode) {
        super("URL не найден");
    }
}
