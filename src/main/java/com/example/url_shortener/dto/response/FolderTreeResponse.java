package com.example.url_shortener.dto.response;

import com.example.url_shortener.entity.Folder;
import lombok.Value;

import java.util.List;

@Value
public class FolderTreeResponse {
    Long id;
    String name;
    Long parentId;
    List<FolderTreeResponse> children;
}
