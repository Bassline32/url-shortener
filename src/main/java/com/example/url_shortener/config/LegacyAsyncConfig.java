package com.example.url_shortener.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

//класс конфигурации/Отвечает за создание инастройку пула потоков

//содержин в себе инструкцию  по созданию пула(бин)
@Configuration
@EnableAsync
public class LegacyAsyncConfig {

    //к заданию 8.2
    @Bean(name = "legacyExecutor")
    public Executor legacyExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(2); //спринг всегда будет  держать  в готовности два потока
        executor.setMaxPoolSize(2); //ограничение максимум двумя потоками
        executor.setQueueCapacity(100); //тут пишу сколько в очереди могу накопить  задач. 101 задача приведёт к ошибке
        executor.setThreadNamePrefix("legacy-"); //задал имя потоку
        executor.initialize(); //завершение настройки

        return executor;
    }

    //к 8.3
    @Bean(name = "platformExecutor")
    public Executor platformExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("platform-");
        executor.initialize();

        return executor;
    }

    //к 8.3
    @Bean(name = "virtualExecutor")
    public Executor virtualExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

}
