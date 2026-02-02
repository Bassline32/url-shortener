package com.example.url_shortener.exception;

public class UrlNotFoundException extends RuntimeException {
    public UrlNotFoundException() {
        super("URL не найден");
    }
}
