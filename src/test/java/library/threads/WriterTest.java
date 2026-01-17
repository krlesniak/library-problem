package library.threads;

import library.model.Library;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class WriterTest {
    @Test
    void testWriterThreadExecution() throws InterruptedException {
        Library lib = new Library();
        // Start as virtual thread
        Thread writer = Thread.ofVirtual().start(new Writer("TestWriter", lib));

        while (writer.getState() == Thread.State.NEW) {
            Thread.yield();
        }
        assertTrue(writer.isAlive());

        writer.interrupt();
        writer.join(1000);
        assertFalse(writer.isAlive());
    }

    @Test
    void testWriterInterruptionInSleep() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Library library = new Library() {
            @Override
            public void startWriting(String id) throws InterruptedException {
                latch.countDown();
                super.startWriting(id);
            }
        };

        Thread writer = Thread.ofVirtual().start(new Writer("Writer-1", library));

        latch.await(2, TimeUnit.SECONDS);

        writer.interrupt();
        writer.join(500);
        assertFalse(writer.isAlive());
    }

    @Test
    void testReaderSleepCoverage() throws InterruptedException {
        CountDownLatch leftLibrary = new CountDownLatch(1);
        Library spyLibrary = new Library() {
            @Override
            public void stopWriting(String id) {
                super.stopWriting(id);
                leftLibrary.countDown();
            }
        };

        Thread writer = Thread.ofVirtual().start(new Writer("W-Deep", spyLibrary));

        boolean exited = leftLibrary.await(5, TimeUnit.SECONDS);
        assertTrue(exited);

        while (writer.getState() != Thread.State.TIMED_WAITING) {
            Thread.onSpinWait();
        }

        writer.interrupt();
        writer.join(1000);
        assertFalse(writer.isAlive());
    }
}