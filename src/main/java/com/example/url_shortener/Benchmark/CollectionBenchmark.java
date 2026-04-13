package com.example.url_shortener.Benchmark;


import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CollectionBenchmark {

    private static final int THREADS = 10_000; //количество  потокв которые будут одновременно работать
    private static final int OPERATIONS = 1_000_000; //общее количество операций

    public static void main(String[] args) throws InterruptedException {
        System.out.println("MAP BENCHMARK");
        benchmarkMaps();

        System.out.println();
        System.out.println("QUEUE BENCHMARK");
        benchmarkQueues;
    }


    //бенчмарк для мап
    private static void benchmarkMaps() throws InterruptedException {
    }

    //сам тест
    private static void runMApTest(Map<Integer, Integer> map, boolean threadSafe) throws InterruptedException {

        //создание пула потоков
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        //механизм ожидания завершения всех потоков
        //создаёт счётчик
        CountDownLatch latch = new CountDownLatch(THREADS);

        //засекаем время
        long start = System.nanoTime();

        //запускаем 10 потоков
        for (int t = 0; t < THREADS; t++) {
            final int threadId = t;

            //отправляем задачу в пул потоков
            executor.submit(() -> {
                try {
                    //диапазон ключей для каждог потока
                    int startKey = threadId * (OPERATIONS / THREADS);
                    int endKey = startKey + (OPERATIONS / THREADS);
                    //основная нагрузка-запись  в мап
                    for (int i = startKey; i < endKey; i++) {
                        map.put(i, i);
                    }
                    //сигнал о завершении потока
                } finally {
                    latch.countDown();
                }
            });
        }
        //ожидание завершения всех потоков
        latch.await();
        //завершение пула потоков
        executor.shutdown();

        //измеряем время
        long elapsed = System.nanoTime() - start;
        System.out.println("Time: " + elapsed / 1_000_000 + "ms");

        //проверяем размер map
        System.out.println("Map size" + map.size());

        //проверка корректности для Thread-safe коллекций
        if (threadSafe && map.size() != OPERATIONS) {
            System.out.println("Ожидали" + OPERATIONS + "а получили " + map.size());
        }


    }


}
