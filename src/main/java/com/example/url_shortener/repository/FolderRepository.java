package com.example.url_shortener.repository;

import com.example.url_shortener.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {

    List<Folder> findByUserIdAndParentIsNull(Long userId); //корневые папки

    List<Folder> findByParentId(Long parentId); //подпапки

    Optional<Folder> findByUserId(Long userId, String name);

}

