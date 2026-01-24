//СОЗДАНИЕ КОРОТКОЙ ССЫЛКИ

package com.example.url_shortener.controller;

import com.example.url_shortener.service.UrlService;
import com.example.url_shortener.model.ShortUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/vi/urls")
public class UrlController {

    private final UrlService urlService;

    @Autowired
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    //удаляем ссылку
    @DeleteMapping("/{shortCode}")
    public ResponseEntity<Void> deleteUrl(@PathVariable String shortCode) {
        urlService.deleteUrl(shortCode);
        return ResponseEntity.noContent().build();
    }

    //получаем список всех ссылок
    @GetMapping
    public ResponseEntity<List<ShortUrl>> getAllUrls() {
        List<ShortUrl> urls = urlService.getAllUrls();
        return ResponseEntity.ok(urls);
    }

    //получаем конкретную ссылку по её короткому коду
    @GetMapping("/{shortCode}")
    public ResponseEntity<ShortUrl> getShortUrlBYShortCode(@PathVariable String shortCode) {
        ShortUrl url = urlService.getUrlByShortCode(shortCode);
        if (url == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(url);
    }

    //создаём короткую ссылку
    @PostMapping("/shorten")
    public ResponseEntity<?> createShortUrl(@RequestParam("originalUrl") String originalUrl) {
        ShortUrl shortUrl = urlService.createShortUrl(originalUrl);
        URI location = URI.create("/api/vi/urls" + shortUrl.getShortCode());
        return ResponseEntity.created(location).body(shortUrl);
    }

}
