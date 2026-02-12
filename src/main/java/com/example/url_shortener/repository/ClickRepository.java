package com.example.url_shortener.repository;

import com.example.url_shortener.entity.ClickEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClickRepository extends JpaRepository<ClickEntity, Long> {
    List<ClickEntity> findByShortCode(String shortCode);
}
