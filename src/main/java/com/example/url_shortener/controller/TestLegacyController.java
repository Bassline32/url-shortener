package com.example.url_shortener.controller;

import com.example.url_shortener.service.LegacyService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class TestLegacyController {

    @Async("legacyExecutor")
    @GetMapping("/legacy-load")
    public CompletableFuture<String> legacyLoad() throws InterruptedException {
      //  System.out.println("Start: " + Thread.currentThread());
        Thread.sleep(1000);
      //  System.out.println("End: " + Thread.currentThread());
        return CompletableFuture.completedFuture("OK");
    }
}

