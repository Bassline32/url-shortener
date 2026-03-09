package com.example.url_shortener.dto.request;

import lombok.Data;

@Data
public class LoginRequest {

    private String username;

    private String password;
}

