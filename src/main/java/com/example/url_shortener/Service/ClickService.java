package com.example.url_shortener.Service;


import com.example.url_shortener.model.Click;
import com.example.url_shortener.repository.ClickRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ClickService {

    private final ClickRepository clickRepository;

    @Autowired
    public ClickService(ClickRepository clickRepository) {
        this.clickRepository = clickRepository;
    }

    //сохраняем новый клик в бд
    public void save(Click click) {
        clickRepository.save(click);
    }

    //возвращаем список всех кликов
    public List<Click> getAllCliks() {
        return clickRepository.findAll();
    }

    //Возвращаем количество кликов по указанному короткому коду
    // Stream API
    public Optional<Click> getClickCountByShortCode (String shortCode) {
        List<Click> clicks = clickRepository.findByAllShortCode(shortCode);
        return clicks.stream().max(Comparator.comparing(Click :: getTimestamp));
    }

}
