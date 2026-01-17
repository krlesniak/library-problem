package library;

import library.model.Library;
import library.threads.Reader;
import library.threads.Writer;

import java.util.Scanner;

/**
 * A class that represents an entry point for the Library Management System.
 * It handles user input and thread initialization.
 */
public class Main {
    /**
     * Default constructor for the Main class.
     */
    public Main() {
        // Default constructor for javadoc
    }
    /**
     * A main method that reads simulation parameters from the console and starts the threads.
     * @param args Command line arguments (not used).
     */
    @SuppressWarnings("java:S106") // ignoring warnibngs about system outs
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n-=-=-=-=-=-=- Library management system -=-=-=-=-=-=-\n");

        System.out.println("|---------------------------------------------------|");
        System.out.println("|           Setting up the library...               |");
        System.out.println("|---------------------------------------------------|");


        int numReaders = 10;
        int numWriters = 3;

        if (args.length >= 2) {
            try {
                numReaders = Integer.parseInt(args[0]);
                numWriters = Integer.parseInt(args[1]);
                System.out.println("\nReaders = " + numReaders + ", Writers = " + numWriters);
            } catch (NumberFormatException e) {
                System.out.println("\nError: Parameters must be numbers. Using default values, readers = 10, writers = 3.\n");
            }
        } else {
            System.out.println("\nNo parameters. Using default values: readers = 10, writers = 3.\n");
        }
        Library library = new Library();

        for (int i = 1; i <= numReaders; i++) {
            Thread.ofVirtual()
                    .name("Reader-" + i)
                    .start(new Reader("Reader-" + i, library));
            try { Thread.sleep(600); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        for (int i = 1; i <= numWriters; i++) {
            Thread.ofVirtual()
                    .name("Writer-" + i)
                    .start(new Writer("Writer-" + i, library));
            try { Thread.sleep(600); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); }

        scanner.close();
    }
}
