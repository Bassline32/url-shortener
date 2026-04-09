package com.example.url_shortener.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class LegacyService {
    @Async("legacyExecutor")
    public void load() throws InterruptedException {
     //   System.out.println("Start: " + Thread.currentThread());
        Thread.sleep(1000);
     //   System.out.println("End: " + Thread.currentThread());

    }
}
