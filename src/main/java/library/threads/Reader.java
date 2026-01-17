package library.threads;

import library.model.Library;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A thread representing a library reader who enters, reads for a duration, and exits in a loop.
 */
public class Reader implements Runnable{
    /**
     * A private final unique identifier for this reader thread.
     */
    private final String id;
    /**
     * A private final reference to the shared library.
     */
    private final Library library;

    /**
     * Constructs a new Reader thread.
     * @param id unique ID for the thread.
     * @param library reference to the shared library.
     */
    public Reader(String id, Library library) {
        this.id = id;
        this.library = library;
    }

    /**
     * The main execution loop of the reader thread.
     * It handles the cycle of requesting entry, reading, exiting, and resting.
     */
    @Override
    @SuppressWarnings("java:S2245")
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                library.startReading(id);

                int readingTime = ThreadLocalRandom.current().nextInt(1000, 3001);
                Thread.sleep(readingTime); // reading last from 1 to 3 s

                library.stopReading(id);
                // Thread.sleep(3000); // time until the reader rejoin the queue
            }
        } catch (InterruptedException e) {
            // Restore the interrupted status
            Thread.currentThread().interrupt();}
    }
}
