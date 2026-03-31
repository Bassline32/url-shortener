package com.example.url_shortener.entity;


import com.example.url_shortener.model.ShortUrl;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "clicks")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClickEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    //связь ManyToOne с ссылкой
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "short_url_id", nullable = false)
    private ShortUrlEntity shortUrl;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(length = 2048)
    private String referer;

   // @Column(name = "shortCode", nullable = false)
   // private String shortCode;

   // @Transient
    //@Column(name = "timestamp", nullable = false)
   // private LocalDateTime timestamp;

    @Column(name = "clicked_at", nullable = false)
    private LocalDateTime clickedAt;

    @PrePersist
    protected void onCreate() {
        clickedAt = LocalDateTime.now();
    }
}