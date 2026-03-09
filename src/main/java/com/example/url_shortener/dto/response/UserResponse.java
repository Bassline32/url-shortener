package com.example.url_shortener.dto.response;

import lombok.Value;

@Value
public class UserResponse {

    String username;

    String email;

    Long id;
}
