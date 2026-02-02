//СОЗДАНИЕ КОРОТКОЙ ССЫЛКИ

package com.example.url_shortener.controller;

import com.example.url_shortener.dto.CreateUrlRequest;
import com.example.url_shortener.model.ShortUrl;
import com.example.url_shortener.service.UrlService;
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

    //пагинация и сортировка
    @GetMapping
    public ResponseEntity<List<ShortUrl>> getUrls(
            //праметр метода номер страницы
            @RequestParam(defaultValue = "0") int page,
            //количество элементов, которые нужно вернуть на странице
            @RequestParam(defaultValue = "10") int size,
            //поле, по которому будет производиться сортировка
            @RequestParam(defaultValue = "createdAt") String sortBy,
            // параметр указывает порядок сортировки (asc — по возрастанию, desc — по убыванию).
            // По умолчанию, если параметр не указан в запросе, будет использоваться desc.
            @RequestParam(defaultValue = "desk") String order
    ) {
        List<ShortUrl> urls = urlService.getUrls(page, size, sortBy, order);
        return ResponseEntity.ok(urls);
    }

    //ищем ссылки по ключевому слову
    @GetMapping("/search")
    public ResponseEntity<List<ShortUrl>> searchUrlsByKeyword(@RequestParam String keyword) {
        List<ShortUrl> urls = urlService.searchUrlByKeyWord(keyword);
        return ResponseEntity.ok(urls);
    }

    //ищем ссылки по домену
    @GetMapping("/search")
    public ResponseEntity<List<ShortUrl>> searchUrlsByDomain(@RequestParam String domain) {
        List<ShortUrl> urls = urlService.searchUrlsByDomain(domain);
        return ResponseEntity.ok(urls);
    }

    //ищем просроченные ссылки
    @GetMapping("/expired")
    public ResponseEntity<List<ShortUrl>> searchExpireUrls() {
        List<ShortUrl> urls = urlService.getExpiredUrls();
        return ResponseEntity.ok(urls);
    }

    //ищем актуальные ссылки
    @GetMapping("/actual")
    public ResponseEntity<List<ShortUrl>> searchActualUrls() {
        List<ShortUrl> urls = urlService.getActualUrls();
        return ResponseEntity.ok(urls);
    }

    //возвращаем список массово созданных ссылок
    @PostMapping("/batch")
    public ResponseEntity<List<ShortUrl>> createUrls(@RequestBody List<CreateUrlRequest> requests) {
        List<ShortUrl> urls = urlService.createUrls(requests);
        return ResponseEntity.ok(urls);
    }

    //экспорт данных в JSON и CSV форматах
    @GetMapping("/export")
    public ResponseEntity<?> exportUrls(@RequestParam String format) {
        List<ShortUrl> urls = urlService.getAllUrls();
        if (format.equals("json")) {
            return ResponseEntity.ok(urls);
        } else if ("csv".equals(format)) {
            String csv = urlService.exportToCsv(urls);
            return  ResponseEntity.ok(csv);
        } else {
            return ResponseEntity.badRequest().body("Неверный формат");        }
    }
}
