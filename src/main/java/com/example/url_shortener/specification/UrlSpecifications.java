package com.example.url_shortener.specification;

import com.example.url_shortener.entity.ShortUrlEntity;
import com.example.url_shortener.entity.Tag;
import com.example.url_shortener.model.ShortUrl;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

//Specification  для динамичческих фильтров

public class UrlSpecifications {


    //Фильтрует ссылки (ShortUrl) по владельцу — ищет только те, которые принадлежат пользователю с указанным userId.
    public static Specification<ShortUrlEntity> belongsToUser(Long userId) {
        return ((root, query, criteriaBuilder) ->
                //root.get("user").get("id") должны быть равны userId
                userId == null ? null : criteriaBuilder.equal(root.get("user").get("id"), userId));
    }

    //ищет ссылки у которых есть тег  с указанным именем tagName
    public static Specification<ShortUrlEntity> hasTag(String tagName) {
        return (root, query, criteriaBuilder) -> {

            if (tagName == null) return null;
            Join<ShortUrlEntity, Tag> tags = root.join("tags");
            return criteriaBuilder.equal(tags.get("name"), tagName);
        };
    }

    //фильтрует тольько активные ссылки
    public static Specification<ShortUrlEntity> isActive() {
        return (root, query, cb) -> cb.or(
                cb.isNull(root.get("expiresAt")),
                cb.greaterThan(root.get("expiresAt"), LocalDateTime.now())
        );
    }

    //ищет  ссылки, созданные после указанной даты
    public static Specification<ShortUrlEntity> createdAfter(LocalDateTime date) {
        return (root, query, criteriaBuilder) ->
                date == null ? null : criteriaBuilder.greaterThan(root.get("createdAt"), date);
    }

    public static Specification<ShortUrlEntity> originalUrlContains(String keyword) {
        return (root, query, cb) ->
                keyword == null ? null : cb.like(
                        cb.lower(root.get("originalUrl")),
                        "%" + keyword.toLowerCase() + "%"
                );
        //% это левый и правй якорь. Они говорят  что до  и после искомого слова может быть что угодно
    }
}