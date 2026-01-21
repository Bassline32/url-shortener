package com.example.url_shortener.repository;

import com.example.url_shortener.model.Click;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClickRepository extends JpaRepository<Click, Long> {

    @Autowired
    private C

