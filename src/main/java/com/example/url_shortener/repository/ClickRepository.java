package com.example.url_shortener.repository;

import com.example.url_shortener.model.Click;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClickRepository extends JpaRepository<Click, Long> {
    List<Click> findByAllShortCode(String shortCode);
}
