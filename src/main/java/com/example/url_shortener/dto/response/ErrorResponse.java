package com.example.url_shortener.dto.response;

import lombok.Value;

@Value

public class ErrorResponse {

    String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}