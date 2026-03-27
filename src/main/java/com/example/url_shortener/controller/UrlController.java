//СОЗДАНИЕ КОРОТКОЙ ССЫЛКИ

package com.example.url_shortener.controller;

import com.example.url_shortener.dto.request.*;
import com.example.url_shortener.dto.response.CreateShortUrlResponse;
import com.example.url_shortener.entity.ShortUrlEntity;
import com.example.url_shortener.entity.User;
import com.example.url_shortener.exception.UserNotFoundException;
import com.example.url_shortener.mapper.ShortUtlMapper;
import com.example.url_shortener.model.ShortUrl;
import com.example.url_shortener.repository.UserRepository;
import com.example.url_shortener.service.UrlService;
import com.example.url_shortener.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/vi/urls")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;
    private final UserService userService;
    private final UserRepository userRepository;

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
    public ResponseEntity<CreateShortUrlResponse> createShortUrl(
            @RequestBody CreateShortUrlRequest request
    ) {
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new UserNotFoundException("такого пользователя не существует"));

        ShortUrlEntity entity = urlService.createShortUrl(request, user);

        return ResponseEntity.ok(ShortUtlMapper.response(entity));
    }


    //пагинация и сортировка
    @GetMapping("/filtred")
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
    @GetMapping("/search/keyword/{keyword}")
    public ResponseEntity<List<ShortUrl>> searchUrlsByKeyword(@PathVariable String keyword) {
        List<ShortUrl> urls = urlService.searchUrlByKeyWord(keyword);
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
    public ResponseEntity<List<ShortUrl>> createUrls(@RequestBody List<CreateShortUrlRequest> requests) {

        User user = userRepository.findById(1L)
                .orElseThrow(() -> new UserNotFoundException("Такой пользователь не найден"));

        List<ShortUrl> urls = urlService.createUrls(requests, user);
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
            return ResponseEntity.ok(csv);
        } else {
            return ResponseEntity.badRequest().body("Неверный формат");
        }
    }

    @PutMapping("/{shortCode}/tags")
    public ResponseEntity<?> updateTags(@PathVariable String shortCode,
                                        @RequestBody UpdateTagRequest request) {

        User user = userService.getCurrentUser();
        ShortUrlEntity updated = urlService.updateTags(user, shortCode, request.tags());
        return ResponseEntity.ok(updated);

    }

    @GetMapping("/promoTag")
    public ResponseEntity<?> getUrlsWereEqualsPromoTag(
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        User user = userService.getCurrentUser();

        if (tag != null) {
            return ResponseEntity.ok(urlService.getUrlByTag(user, tag));
        }
        return ResponseEntity.ok(urlService.getUrlsForUser(user, page, size));
    }

    @PutMapping("/{shortCode}/folder")
    public ResponseEntity<String> moveUrlToFolder(
            @PathVariable String shortCode,
            @RequestBody MoveUrlRequest request
    ) {
        urlService.moveUrlToFolder(shortCode, request.folderId());
        return ResponseEntity.ok("Cсылка перемещена в папку");
    }


}
