package library;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    @Test
    void testMainConstructor() {
        // for an empty constructor
        Main mainApp = new Main();
        assertNotNull(mainApp);
    }

    @Test
    void testMainCoverage() throws InterruptedException {
        // system in mock
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream("\n".getBytes()));

        // main thread in virtual thread
        Thread t = Thread.ofVirtual().start(() -> Main.main(new String[]{}));

        // waiting until main thread enters phase IMED_WAITING
        long timeout = System.currentTimeMillis() + 2000;
        while (t.getState() != Thread.State.TIMED_WAITING && t.isAlive()) {
            if (System.currentTimeMillis() > timeout) break;
            Thread.onSpinWait();
        }

        // interrupting therad and entering catch block
        t.interrupt();
        t.join(1000);

        assertFalse(t.isAlive(), "Thread main should end");

        // setting back original system out
        System.setIn(originalIn);
    }
}