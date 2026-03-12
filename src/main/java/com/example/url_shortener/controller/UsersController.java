package com.example.url_shortener.controller;

import com.example.url_shortener.dto.response.ShortUrlResponse;
import com.example.url_shortener.dto.response.UserResponse;
import com.example.url_shortener.dto.response.UserStatsResponse;
import com.example.url_shortener.entity.ShortUrlEntity;
import com.example.url_shortener.entity.User;
import com.example.url_shortener.service.UrlService;
import com.example.url_shortener.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;
    private final UrlService urlService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {

        User user = userService.getCurrentUser();

        UserResponse response = new UserResponse(
                user.getUsername(),
                user.getEmail(),
                user.getId()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/urls")
    public ResponseEntity<Page<ShortUrlResponse>> getMyUrls(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        User user = userService.getCurrentUser();

        Page<ShortUrlEntity> urls = urlService.getUrlsForUser(user, page, size);


        //Полученный список объектов ShortUrlEntity
        // преобразуется в новый список объектов ShortUrlResponse
        Page<ShortUrlResponse> response = urls.map(url ->
                new ShortUrlResponse(
                        url.getId(),
                        url.getOriginalUrl(),
                        url.getShortCode(),
                        url.getClickCount(),
                        url.getExpiresAt()
                )
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/stats")
    public ResponseEntity<UserStatsResponse> getMyStats() {
        User user = userService.getCurrentUser();
        UserStatsResponse response = urlService.getStatsForUser(user);
        return ResponseEntity.ok(response);

    }



}
