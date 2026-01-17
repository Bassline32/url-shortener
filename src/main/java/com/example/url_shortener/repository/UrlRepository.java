package com.example.url_shortener.repository;

import com.example.url_shortener.model.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<ShortUrl, String> {

    Optional<ShortUrl> findByShortCode(String shortCode);

    void deleteByShortCode(String shortcode);
}
