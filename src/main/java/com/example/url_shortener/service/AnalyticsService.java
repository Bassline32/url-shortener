package com.example.url_shortener.service;


import com.example.url_shortener.dto.AnalyticsResponse;
import com.example.url_shortener.entity.ClickEntity;
import com.example.url_shortener.exception.UrlNotFoundException;
import com.example.url_shortener.mapper.ShortUtlMapper;
import com.example.url_shortener.model.ShortUrl;
import com.example.url_shortener.repository.ClickRepository;
import com.example.url_shortener.repository.ShortUrlRepository;
import com.example.url_shortener.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {

    private final ShortUrlRepository urlRepository;
    private final ClickRepository clickRepository;



}




