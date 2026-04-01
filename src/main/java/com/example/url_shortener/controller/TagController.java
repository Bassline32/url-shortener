package com.example.url_shortener.controller;

import com.example.url_shortener.dto.request.CreateTagRequest;
import com.example.url_shortener.dto.response.PopularTagResponse;
import com.example.url_shortener.dto.response.TagResponse;
import com.example.url_shortener.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @PostMapping
    public ResponseEntity<TagResponse> createTag(@RequestBody CreateTagRequest request) {
        //String name = body.get("name");
        return ResponseEntity.ok(tagService.createTag(request.name()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<TagResponse>> getAllTags() {
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @GetMapping("/popular")
    public ResponseEntity<List<PopularTagResponse>> getPopularTags(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(tagService.getPopularTags(limit));
    }

}
