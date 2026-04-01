package com.example.url_shortener.controller;


import com.example.url_shortener.entity.ShortUrlEntity;
import com.example.url_shortener.service.ClickService;
import com.example.url_shortener.service.UrlService;
import com.example.url_shortener.model.ShortUrl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/")

public class RedirectController {

    private final UrlService urlService;

    @GetMapping("/{shortCode}")
    @Transactional
    public ResponseEntity<Void> redirect(@PathVariable String shortCode, HttpServletRequest request) {

        ShortUrlEntity entity = urlService.shortUrlEntity(shortCode, request);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(entity.getOriginalUrl()))
                .build();
    }
}