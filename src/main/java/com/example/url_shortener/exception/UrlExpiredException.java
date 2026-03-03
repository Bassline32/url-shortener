package com.example.url_shortener.exception;

public class UrlExpiredException extends RuntimeException {
    public UrlExpiredException(String shortCode) {
        super(shortCode);
    }
}
