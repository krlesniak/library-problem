package library.threads;

import library.model.Library;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ReaderTest {
    @Test
    void testReaderThreadExecution() throws InterruptedException {
        Library lib = new Library();
        // Start as a virtual thread to keep logic
        Thread reader = Thread.ofVirtual().start(new Reader("TestReader", lib));

        while (reader.getState() == Thread.State.NEW) {
            Thread.yield();
        }
        assertTrue(reader.isAlive());

        reader.interrupt();
        reader.join(1000);
        assertFalse(reader.isAlive());
    }

    @Test
    void testReaderSleepCoverage() throws InterruptedException {
        CountDownLatch leftLibrary = new CountDownLatch(1);
        Library spyLibrary = new Library() {
            @Override
            public void stopReading(String id) {
                super.stopReading(id);
                leftLibrary.countDown();
            }
        };

        Thread reader = Thread.ofVirtual().start(new Reader("R-Deep", spyLibrary));

        boolean exited = leftLibrary.await(5, TimeUnit.SECONDS);
        assertTrue(exited);

        while (reader.getState() != Thread.State.TIMED_WAITING) {
            Thread.onSpinWait();
        }

        reader.interrupt();
        reader.join(1000);
        assertFalse(reader.isAlive());
    }
}