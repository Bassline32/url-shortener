package com.example.url_shortener.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String username) {
       super(username);
    }
}
