package com.example.url_shortener.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "click")
public class ClickEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Long id;

    @Column(name = "shortCode", nullable = false)
    private String shortCode;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "ipAddress", nullable = false)
    private String ipAddress;

    @Column(name = "userAgent", nullable = false)
    private String userAgent;

    @Column(name = "referer", nullable = false)
    private String referer;







}
