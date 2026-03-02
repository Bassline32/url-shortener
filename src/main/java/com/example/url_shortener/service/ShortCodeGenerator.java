package com.example.url_shortener.service;


import org.springframework.stereotype.Service;

import java.util.Random;

@Service

public class ShortCodeGenerator {
    //генерируем shortCode
    private static final String symbols = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final Random random = new Random();

    public String generate(int length) {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(symbols.charAt(random.nextInt(symbols.length())));
        }
        return code.toString();
    }
}