package util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClickCounterTest {

    //создаю тестовый метод
    @Test
    void testCounters() throws InterruptedException {

        //создаю экземпляр счётчика
        ClickCounter counter = new ClickCounter();

        //настраиваю параметры теста
        int threadsCount = 100; //сколько потоков запустим
        int incrementsPerThreads = 10_000; //сколько раз каждый поток увеличивает  счётчик
        int expected = threadsCount * incrementsPerThreads; // ожидаемое итоговое количестов счётчика

        //создание списка потоков
        List<Thread> threads = new ArrayList<>();

        //запуск потоков
        //цикл для создания 100 потоков
        for (int i = 0; i < threadsCount; i++) {
            //создаём новый пток с лямбдой
            Thread t = new Thread(() -> {
                //Вот тут каждый потток 10000 раз вызывает три метода увеличения счётчика
                for (int j = 0; j < incrementsPerThreads; j++) {
                    counter.incrementUnsafe();
                    counter.incrementSynchronized();
                    counter.incrementAtomic();
                }
            });
            threads.add(t); //добавляем потток в список
            t.start(); //запускае м поток
        }
        //ожидание завершения потоков
        for (Thread t : threads) {
            t.join();
        }
        //Выводим информацию
        System.out.println("Ecpexted (ожидаемое итоговое количестов счётчика) " + expected);
        System.out.println("Unsafe (обычный счётчик ++) " + counter.getUnsafeCounter());
        System.out.println("synchronizedCounter " + counter.getSynchronizedCounter());
        System.out.println("Atomic " + counter.getAtomicCounter());

        //провверка результатов
        assertEquals(expected, counter.getSynchronizedCounter());
        assertEquals(expected, counter.getAtomicCounter());
    }
}
