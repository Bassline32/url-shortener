package com.example.url_shortener.service;


import org.springframework.stereotype.Service;

import java.util.Random;
@Service

    public class ShortCodeGenerator {
    //генерируем shortCode
    public String shortCode() {
        String symbols = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder shortCode = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            shortCode.append(symbols.charAt(random.nextInt(symbols.length())));
        }
        return shortCode.toString();
    }

}
