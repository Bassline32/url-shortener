package com.example.url_shortener.entity;

import com.example.url_shortener.model.Click;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "short_urls")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortUrlEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "short_code", nullable = false, unique = true, length = 20)
    // "abc123"
    private String shortCode;

    @Column(name = "original_url", nullable = false, length = 2048)
    // "https://example.com/very/long/path"
    private String originalUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    //используется для хранения даты и времени,
    // когда была создана сокращённая ссылка.
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    // nullable — может быть бессрочной
    private LocalDateTime expiresAt = LocalDateTime.now().plusDays(20);

    @Column(name = "click_count", nullable = false)
    @Builder.Default
    //счётчик переходов по ссылке
    private Long clickCount = 0L;

    //связь ManyToOne с пользователем
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    //связь ManyToOne с папкой
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    private Folder folder;

    //связь OneToMany c кликами
    @OneToMany(mappedBy = "shortUrl", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ClickEntity> clicks = new ArrayList<>();

    //связь ManyToMany с тегами
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "url_tags",
            joinColumns = @JoinColumn(name = "url_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    //вспомогательные методы для упрваления связями
    public void addTag(Tag tag) {
        tags.add(tag);
        tag.getUrls().add(this);
    }

    public void removeTag (Tag tag) {
        tags.remove(tag);
        tag.getUrls().remove(this);
    }

    public void incrementClickCount () {
        this.clickCount++;
    }
}
