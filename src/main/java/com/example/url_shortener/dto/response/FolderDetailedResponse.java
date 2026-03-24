package com.example.url_shortener.dto.response;

import com.example.url_shortener.entity.Folder;
import lombok.Value;

import java.util.List;


//эта дто нужна,так как она описывает папку и всё её содержимое
@Value
public class FolderDetailedResponse {
    Long id; //идентификатор папки
    String name; //имя папки
    Long parentId;
    List<FolderDetailedResponse> children; //список вложеннных папок
    List<LinkResponse> links; //список ссылок,которые лежатвнутри папки
}
