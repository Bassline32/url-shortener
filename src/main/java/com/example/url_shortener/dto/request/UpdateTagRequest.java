package com.example.url_shortener.dto.request;

import java.util.List;

public record UpdateTagRequest(List<String> tags) {
}

