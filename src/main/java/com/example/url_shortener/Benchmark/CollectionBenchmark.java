package com.example.url_shortener.Benchmark;


import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;

public class CollectionBenchmark {

    private static final int THREADS = 10; //количество  потокв которые будут одновременно работать
    private static final int OPERATIONS = 1_000_000; //общее количество операций

    public static void main(String[] args) throws InterruptedException {
        System.out.println("MAP BENCHMARK");
        benchmarkMaps();

        System.out.println();
        System.out.println("QUEUE BENCHMARK");
        benchmarkQueues();
    }


    //бенчмарк для мап
    private static void benchmarkMaps() throws InterruptedException {
        System.out.println("HashMap(не thread safe)");
        try {
            runMApTest(new HashMap<>(), false);
        } catch (Exception e) {
            System.out.println("HashMap сломалась " + e);
        }
    }

    //бечмарк для очереди
    private static void benchmarkQueues() throws InterruptedException {
        System.out.println("LinkedBlockingQueue:");
        runQueueTest(new LinkedBlockingQueue<>());

        System.out.println("\nConcurrentLinkedQueue");
        runQueueTest(new ConcurrentLinkedQueue<>());

        System.out.println("\\nArrayBlockingQueue (capacity=1_000_000):");
        runQueueTest(new ArrayBlockingQueue<>(OPERATIONS));
    }


    //сам тест для мапы
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

    //сам тест для Очереди
    private static void runQueueTest(Queue<Integer> queue) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        CountDownLatch latch = new CountDownLatch(THREADS);

        long start = System.nanoTime();

        for (int t = 0; t < THREADS; t++) {
            final int threadId = t;
            executor.submit(() -> {
                try {
                    int startVal = threadId * (OPERATIONS / THREADS);
                    int endVal = startVal + (OPERATIONS / THREADS);

                    for (int i = startVal; i < endVal; i++) {
                        if (queue instanceof BlockingQueue<Integer> bq) {
                            try {
                                bq.put(i);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        } else {
                            queue.offer(i);
                        }
                    }

                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();

        long elapsed = System.nanoTime() - start;
        System.out.println("Time " + elapsed / 1_000_000 + "ms");
        System.out.println("Queue size" + queue.size());
    }
}
