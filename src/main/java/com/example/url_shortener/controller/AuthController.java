package com.example.url_shortener.controller;


import com.example.url_shortener.dto.request.LoginRequest;
import com.example.url_shortener.dto.request.RegisterRequest;
import com.example.url_shortener.dto.response.UserResponse;
import com.example.url_shortener.entity.User;
import com.example.url_shortener.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        //создание юзера
        User user = userService.newUserRegistration(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );

        //Вот тут преобразуем сущность user  в дто (UserResponse)
        // Так энтити нельзя отдавать наружу)
        UserResponse response = new UserResponse(
                user.getUsername(),
                user.getEmail(),
                user.getId()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
//используем вайлдКард так как метод вернёт мапу, где могут быть разные типы данных,
// а не конкретный
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        User user = userService.login(
                request.getUsername(),
                request.getPassword()
        );

        return ResponseEntity.ok(java.util.Map.of("userId", user.getId()));
    }

}
