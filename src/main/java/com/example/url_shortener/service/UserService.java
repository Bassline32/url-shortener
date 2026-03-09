package com.example.url_shortener.service;


import com.example.url_shortener.entity.User;
import com.example.url_shortener.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    //регистрация нового пользователя
    @Transactional
    public User newUserRegistration(String userName, String email, String passwordHash) {

        if (userRepository.existsByUsername(userName)) {
            throw new IllegalArgumentException("Такое имя пользователя уже существует");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Такой эмэйл уже существует");
        }

        User user = User.builder()
                .username(userName)
                .email(email)
                .passwordHash(passwordHash)
                .build();

        return userRepository.save(user);
    }

    //поиск по UserName
    public User findByUserName(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("ТАКОЕ ИМЯ ПОЛЬЗОВАТЕЛЯ НЕ НАЙДЕНО " + username));
    }

    //поиск по Id
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ТАКОЕ ID ПОЛЬЗОВАТЕЛЯ НЕ НАЙДЕНО"));
    }

    //поиск по Email
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("ТАКОЙ  EMAIL ПОЛЬЗОВАТЕЛЯ НЕ НАЙДЕН"));
    }

    //проверка пароля
    public User login(String passwordHash, String username) {
        User user = findByUserName(username);
        if (!user.getPasswordHash().equals(passwordHash)) {
            throw new RuntimeException("НЕВЕРНЫЙ ПАРОЛЬ");
        }
        return user;
    }

    //получение текущего пользователя
    public User getCurrentUser() {
return userRepository.findAll().stream()
        .findFirst()
        .orElseThrow(() -> new RuntimeException("В БАЗЕ ДАННЫХ НЕТ ПОЛЬЗОВАТЕЛЕЙ"));
    }


}
