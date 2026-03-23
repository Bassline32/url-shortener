package com.example.url_shortener.controller;

import com.example.url_shortener.dto.request.CreateFolderRequest;
import com.example.url_shortener.dto.response.CreateFolderResponse;
import com.example.url_shortener.dto.response.FolderTreeResponse;
import com.example.url_shortener.repository.FolderRepository;
import com.example.url_shortener.service.FolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<FolderTreeResponse>> getFolderTree() {

        Long userId = 1L;

        List<FolderTreeResponse> tree = folderService.getTreeResponse(userId);

        return ResponseEntity.ok(tree);
    }

    @GetMapping("/{id}")
    public ResponseEntity<> какой-то метод (@PathVariable "id" Long id) {


    }

}
