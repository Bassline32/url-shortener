package com.example.url_shortener.dto.response;

//ответ, содержащий ссылки, лежащие в папке в конкретной

import lombok.Value;

@Value
//дто ошка для ссылки
public class LinkResponse {
    Long id;
    String name; //название ссылки (shortCode в шорт урл энтити)
    String url; //сама ссылка (jriginalUrl)
}
