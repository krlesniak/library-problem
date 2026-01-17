package library.threads;

import library.model.Library;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A thread representing a library writer who enters, writes for a duration, and exits in a loop.
 */
public class Writer implements Runnable{
    /**
     * A private final unique identifier for this writer thread.
     */
    private final String id;
    /**
     * A private final reference to the shared library.
     */
    private final Library library;

    /**
     * Constructs a new Writer thread.
     * @param id unique ID for the thread.
     * @param library reference to the shared library.
     */
    public Writer(String id, Library library) {
        this.id = id;
        this.library = library;
    }

    /**
     * The main execution loop of the writer thread.
     * It handles the cycle of requesting entry, writing, exiting, and resting.
     */
    @Override
    @SuppressWarnings("java:S2245")
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                library.startWriting(id);

                int writingTime = ThreadLocalRandom.current().nextInt(1000, 3001);
                Thread.sleep(writingTime); // writing last from 1 to 3 s

                library.stopWriting(id);
                // Thread.sleep(3000); // time until writer rejoin the queue
            }
        } catch (InterruptedException e) {
            // Restore the interrupted status
            Thread.currentThread().interrupt();}
    }
}
