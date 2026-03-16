package com.example.url_shortener.controller;

import com.example.url_shortener.dto.request.CreateFolderRequest;
import com.example.url_shortener.dto.response.CreateFolderResponse;
import com.example.url_shortener.repository.FolderRepository;
import com.example.url_shortener.service.FolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/folders")
@RequiredArgsConstructor
public class FolderController {

    private final FolderRepository folderRepository;
    private final FolderService folderService;

    @PostMapping
    public ResponseEntity<CreateFolderResponse> createFolder
            (@RequestBody CreateFolderRequest request) {

        CreateFolderResponse response = folderService.createFolder(request.name(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
