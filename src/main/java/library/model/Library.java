package library.model;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A class that represents a library management system with synchronized access for readers and writers.
 * The queue outside the library ensures the FIFO order
 */
public class Library {
    /**
     * A private final queue of IDs currently waiting to enter outside the library.
     */
    private final Queue<String> waitingQ = new LinkedList<>();
    /**
     * A private final queue of IDs currently inside the library, ordered by entry time.
     */
    private final Queue<String> inLibrary = new LinkedList<>();

    /**
     * A private final fair lock ensuring that threads are granted access in the order they requested it.
     */
    // fair = true helps the queue to keep track of the correct order
    private final Lock lock = new ReentrantLock(true);
    /**
     * A private final condition variable used to coordinate thread entry based on library state.
     */
    private final Condition enter = lock.newCondition();

    /**
     * A number of readers in the library
     */
    private int readersCounter = 0;
    /**
     * A number of writers in the library
     */
    private int writersCounter = 0;
    /**
     * A max number of readers in library at one moment
     */
    private static final int MAX_READERS = 5;

    /**
     * Constructs a new Library instance.
     */
    public Library() {
        // Default constructor for javadoc
    }

    /**
     * A method that manages the entry logic for readers.
     * It waits until it is reader's turn, there is no writer in the library and there is no maximum amount of readers
     * in the library
     * @param id the unique identifier of the reader
     * @throws InterruptedException if the thread is interrupted while waiting.
     */
    public void startReading(String id) throws InterruptedException {
        lock.lock();
        try {
            waitingQ.add(id);
            printState(id + " joins the queue");

            // can only enter if there is no writer inside and there is no maximum number of readers
            // !waitingQ.peek().equals(id) -> checks when the library is empty if it is reader's / writer's turn
            while (!waitingQ.peek().equals(id) || writersCounter > 0 || readersCounter >= MAX_READERS) {
                enter.await();
            }

            waitingQ.remove(id);
            readersCounter++;
            inLibrary.add(id);
            printState(id + " enters");
        } finally {
            lock.unlock();
        }
    }

    /**
     * A method that manages the exit logic for a reader and signals waiting threads.
     * @param id the unique identifier of the reader
     */
    public void stopReading(String id) {
        lock.lock();
        try {
            readersCounter--;
            inLibrary.remove(id);
            printState(id + " exits");
            // info for everybody that someone else can enter the library (this someone else is the person first in the queue)
            enter.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * A method that manages the entry logic for writers.
     * It waits until it is writer's turn, there is no one else in the library (empty)
     * @param id the unique identifier of the writer
     * @throws InterruptedException if the thread is interrupted while waiting.
     */
    public void startWriting(String id) throws InterruptedException {
        lock.lock();
        try {
            waitingQ.add(id);
            printState(id + " joins the queue");

            // writer can only enter if there is no one in the library
            while (!waitingQ.peek().equals(id) || writersCounter > 0 || readersCounter > 0) {
                enter.await();
            }

            waitingQ.remove(id);
            writersCounter++;
            inLibrary.add(id);
            printState(id + " starts to write");
        } finally {
            lock.unlock();
        }
    }

    /**
     * A method that manages the exit logic for a writer and signals waiting threads.
     * @param id the unique identifier of the writer
     */
    public void stopWriting(String id) {
        lock.lock();
        try {
            writersCounter--;
            inLibrary.remove(id);
            printState(id + " exits");
            enter.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * A helper method to print the current status of the queue and the library.
     * @param event description of the event that triggered the state print.
     */
    @SuppressWarnings("java:S106") // to silence warnings about using loggers and not system outs
    private synchronized void printState(String event) {
        int readersInQueue = 0;
        int writersInQueue = 0;

        // how many in the queue
        for (String id : waitingQ) {
            if (id.startsWith("Reader")) readersInQueue++;
            else writersInQueue++;
        }

        System.out.println("\nEVENT: " + event);
        System.out.println("LIBRARY (" + (readersCounter + writersCounter) + "): " + inLibrary);
        System.out.println(" [Library counter: readers: " + readersCounter + ", writers: " + writersCounter + "] ");
        System.out.println("QUEUE (" + waitingQ.size() + "): " + waitingQ);
        System.out.println(" [Queue counter: readers: " + readersInQueue + ", writers: " + writersInQueue + "] ");
        System.out.println("---------------------------------------------------");
    }
}
