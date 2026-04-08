package com.example.url_shortener.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestLegacyController {

    @GetMapping("/legacy-load")
    public String legacyLoad () throws InterruptedException {
        Thread.sleep(1000); //иммитация долгого I/O
        return "OK";
    }
}
