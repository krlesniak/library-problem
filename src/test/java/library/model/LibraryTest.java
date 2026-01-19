package library.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.*;

class LibraryTest {
    private Library library;

    @BeforeEach
    void setUp() {
        library = new Library();
    }

    @Test
    void testLibraryConstructor() {
        Library lib = new Library();
        assertNotNull(lib);
    }

    // testing 6th reader trying to enter
    @Test
    @Timeout(value = 5)
    void testReaderLimit() throws InterruptedException {
        // filling the library with 5 readers
        for (int i = 1; i <= 5; i++) { library.startReading("Reader-" + i); }

        // sixth reader is trying to enter
        Thread r6 = Thread.ofVirtual().start(() -> {
            try { library.startReading("Reader-6"); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });

        // waiting till thread enters phase WAITING
        while (r6.getState() != Thread.State.WAITING && r6.isAlive()) { Thread.onSpinWait(); }

        assertTrue(r6.isAlive(), "Sixth reader should wait");

        // leaving a spot
        library.stopReading("Reader-1");

        // sixth reader should now enter
        r6.join(2000);
        assertFalse(r6.isAlive());
        library.stopReading("Reader-6");
    }

    @Test
    void testReaderInterruption() throws InterruptedException {
        library.startWriting("W1"); // blocking the library with a writer

        Thread readerThread = Thread.ofVirtual().start(() -> {
            try { library.startReading("R1"); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });

        while (readerThread.getState() != Thread.State.WAITING) { Thread.onSpinWait(); }

        readerThread.interrupt(); // catch block
        readerThread.join(1000);
        assertFalse(readerThread.isAlive());
    }


    @Test
    @Timeout(5)
    void testWriterWaitsForReader() throws InterruptedException {
        library.startReading("R1");

        // writer is trying to enter, but has to wait because of the reader
        Thread writer = Thread.ofVirtual().start(() -> {
            try {
                library.startWriting("W1");
                library.stopWriting("W1");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // waiting until writer is awaiting
        while (writer.getState() != Thread.State.WAITING) { Thread.onSpinWait(); }
        assertTrue(writer.isAlive());

        // reader leaves -> writer enters
        library.stopReading("R1");

        writer.join(2000);
        assertFalse(writer.isAlive());
    }

    @Test
    @Timeout(5)
    void testWriterWaitsForAnotherWriter() throws InterruptedException {
        library.startWriting("W1");

        // another writer wants to enter, but has to wait in the queue
        Thread writer2 = Thread.ofVirtual().start(() -> {
            try {
                library.startWriting("W2");
                library.stopWriting("W2");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // waiting for block
        while (writer2.getState() != Thread.State.WAITING) { Thread.onSpinWait(); }
        assertTrue(writer2.isAlive());

        library.stopWriting("W1");

        writer2.join(2000);
        assertFalse(writer2.isAlive());
    }

    @Test
    void testWriterInterruption() throws InterruptedException {
        library.startReading("R1"); // blocking the library

        Thread writer = Thread.ofVirtual().start(() -> {
            try { library.startWriting("W1"); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });

        while (writer.getState() != Thread.State.WAITING) { Thread.onSpinWait(); }

        // catch block
        writer.interrupt();
        writer.join(1000);
        assertFalse(writer.isAlive());
    }
}