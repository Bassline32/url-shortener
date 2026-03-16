package com.example.url_shortener.service;

import com.example.url_shortener.dto.request.CreateFolderRequest;
import com.example.url_shortener.dto.response.CreateFolderResponse;
import com.example.url_shortener.entity.Folder;
import com.example.url_shortener.entity.User;
import com.example.url_shortener.exception.UserNotFoundException;
import com.example.url_shortener.repository.FolderRepository;
import com.example.url_shortener.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)

public class FolderService {

    private final FolderRepository folderRepository;
    private final UserRepository userRepository;

    @Transactional
    public CreateFolderResponse createFolder(String userName, CreateFolderRequest request) {

        //находим пользователя по юзернейму
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new UserNotFoundException("Такой пользователь не найден"));

        Long userId = user.getId();

        //проверка того,  что  у пользователя нет папки  с таким же именем
        folderRepository.findByUserIdAndName(userId, request.name())
                .ifPresent(f -> {
                    throw new IllegalArgumentException("Папка с таким именем существует");
                });

        //если указан parent id, то ищем родительскую папку
        Folder parent = null;
        if (request.parentId() != null) {
            parent = folderRepository.findByIdAndUserId(request.parentId(), userId)
                    .orElseThrow(() -> new IllegalArgumentException("Ролительская папка не найдена"));
        }

        //создаём новую  папку
        Folder folder = Folder.builder()
                .name(request.name())
                .parent(parent)
                .user(user)
                .build();

        //проверка на null parentId
        Long parentId = Optional.ofNullable(folder.getParent())
                .map(Folder::getId)
                .orElse(null);

        //сохраняем в бд
        folderRepository.save(folder);

        //возвращаем DTO
        return new CreateFolderResponse(
                folder.getId(),
                folder.getName(),
                parentId
        );
    }

}
