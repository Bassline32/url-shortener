package util;


import java.util.concurrent.atomic.AtomicLong;

public class ClickCounter {

    //обычный счётчик ++
    private long unsafeCounter = 0;
    //счётчик synchronized
    private long synchronizedCounter = 0;
    //с AtomicLong
    private final AtomicLong atomicCounter = new AtomicLong(0);


    //обычный counter++
    public void incrementUnsafe() {
        unsafeCounter++;
    }

    //с synchronized
    public void incrementSynchronized() {
        synchronizedCounter++;

    }

    //с AtomicLongС
    public void incrementAtomic() {
        atomicCounter.incrementAndGet();
    }





    public long getUnsafeCounter() {
        return unsafeCounter;
    }

    public long getSynchronizedCounter() {
        return synchronizedCounter;
    }

    public long getAtomicCounter() {
        return atomicCounter.get();
    }
}
