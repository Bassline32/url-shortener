package com.example.url_shortener.service;


import com.example.url_shortener.dto.response.PopularTagResponse;
import com.example.url_shortener.dto.response.TagResponse;
import com.example.url_shortener.entity.Tag;
import com.example.url_shortener.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {

    private final TagRepository tagRepository;

    @Transactional
    //метод ля создания тега(см POST метод  в контроллере)
    public TagResponse createTag(String name) {
        //проверка, что такого тега нет
        if (tagRepository.existsByName(name)) {
            throw new IllegalArgumentException("Такое имя тега уже есть");
        }

        //создание сущности tag
        Tag tag = Tag.builder()
                .name(name)
                .build();

        //и сохраняем её в бд
        Tag saved = tagRepository.save(tag);

        //new используем, что бы создать новый объект dto и вернуть его юзеру
        return new TagResponse(saved.getId(), saved.getName());
    }

    //метод для получения всех тегов
    public List<TagResponse> getAllTags() {
        return tagRepository.findAll().stream()
                .map(e -> new TagResponse(e.getId(), e.getName()))
                .toList();
    }

    //метод дл получения популярных тегов
    public List<PopularTagResponse> getPopularTags(int limit) {

        //Здесь создается объект типа PageRequest,
        // который используется для передачи информации о пагинации в репозиторий.
        var pageable = PageRequest.of(0, limit);

        //Достаю данные из репозитория
        List<Object[]> rows = tagRepository.findMostPopular(pageable);

        //теперь эти данные преобразую  в респонс
        return rows.stream().map(row -> {
            Tag tag = (Tag) row[0]; //первый элемент Tag
            Long count = (Long) row[1]; //количество ссылок
            return new PopularTagResponse(tag.getId(), tag.getName(), count);
        }).toList();
    }


}
