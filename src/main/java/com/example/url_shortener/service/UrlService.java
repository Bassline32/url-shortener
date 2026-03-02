package com.example.url_shortener.service;

import com.example.url_shortener.dto.CreateUrlRequest;
import com.example.url_shortener.entity.User;
import com.example.url_shortener.model.ShortUrl;
import com.example.url_shortener.repository.ClickRepository;
import com.example.url_shortener.repository.ShortUrlRepository;
import com.example.url_shortener.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UrlService {

    //private final UrlRepository urlRepository;
    private final ShortUrlRepository shortUrlRepository;
    private final ClickRepository clickRepository;
    private final ShortCodeGenerator shortCodeGenerator;
    private final TagRepository tagRepository;
    private final ShortCodeValidator codeValidator;

    //генерация короткого URL
    public ShortUrl createShortUrl(CreateUrlRequest request, User user) {
        String shortCode = request.getCustomCode() != null
                ? codeValidator.validate(request.getCustomCode())
                :

    }

}
