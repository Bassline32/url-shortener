package com.example.url_shortener.repository;

import com.example.url_shortener.entity.ShortUrlEntity;
import com.example.url_shortener.entity.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long > {

    Optional<Tag> findByName(String name);

    boolean existsByName(String name);

    List<Tag> findByNameContainingIgnoreCase(String namePart);

    @Query("""
        SELECT t, COUNT(s) as urlCount 
        FROM Tag t 
        JOIN t.urls s 
        GROUP BY t 
        ORDER BY urlCount DESC
        """)
    List<Object[]> findMostPopular(Pageable pageable);

}
