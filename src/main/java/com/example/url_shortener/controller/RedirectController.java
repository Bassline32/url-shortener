package com.example.url_shortener.controller;


import com.example.url_shortener.Service.ClickService;
import com.example.url_shortener.Service.UrlService;
import com.example.url_shortener.model.ShortUrl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/")

public class RedirectController {

    private final UrlService urlService;
    private final ClickService clickService;

    @Autowired
    public RedirectController(UrlService urlService, ClickService clickService) {
        this.urlService = urlService;
        this.clickService = clickService;
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode, HttpServletRequest request) {

        Optional<ShortUrl> optionalUrl = Optional.ofNullable(urlService.getUrlByShortCode(shortCode));
        ShortUrl shortUrl = null;

        if (optionalUrl.isPresent()) {
            shortUrl = optionalUrl.get();
            if (shortUrl.getExpiresAt() != null && shortUrl.getExpiresAt().isBefore(LocalDateTime.now())) {
                return ResponseEntity.status(HttpStatus.GONE).body(null);
            }
            clickService.trackClick(shortCode, request);
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(shortUrl.getOriginalUrl())).build();

        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
