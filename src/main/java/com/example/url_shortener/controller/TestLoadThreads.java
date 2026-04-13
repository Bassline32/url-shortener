package com.example.url_shortener.controller;


import com.example.url_shortener.config.LegacyAsyncConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.SortedMap;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/testLoadThreads")
@RequiredArgsConstructor
public class TestLoadThreads {

    @Async("platformExecutor")
    @GetMapping("/platform-load") //c фиксированным пулом потоков.
    public CompletableFuture<String> platformLoad() throws InterruptedException {
        Thread.sleep(1000);
        System.out.println("Platform: " + Thread.currentThread() + "is virtual= " + Thread.currentThread()
                .isVirtual());
        return CompletableFuture.completedFuture("OK");
    }

    @Async("virtualExecutor")
    @GetMapping("/virtual-load")
    public CompletableFuture<String> virtualLoad() throws InterruptedException {
        Thread.sleep(1000);
        System.out.println("Virtual: " + Thread.currentThread() + "is virtual= " + Thread.currentThread()
                .isVirtual());
        return CompletableFuture.completedFuture("OK");
    }

}
