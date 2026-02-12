package com.example.url_shortener.mapper;

import com.example.url_shortener.model.Click;

public class ClickMapper {
    public static Click mapClickEntityToDto (com.example.url_shortener.entity.ClickEntity clickEntity) {
        Click click = new Click();
        click.setId(clickEntity.getId());
        click.setShortCode(clickEntity.getShortCode());
        click.setTimestamp(clickEntity.getTimestamp());
        click.setIpAddress(clickEntity.getIpAddress());
        click.setUserAgent(clickEntity.getUserAgent());
        click.setReferer(clickEntity.getReferer());
        return click;
    }


}
