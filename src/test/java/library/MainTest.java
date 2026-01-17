package library;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void testMainConstructor() {
        Main mainApp = new Main();
        assertNotNull(mainApp);
    }

    @Test
    void testMainWithNoArgs() throws InterruptedException {
        // testing no parameters (else)
        runMainWithArgs(new String[]{});
    }

    @Test
    void testMainWithValidArgs() throws InterruptedException {
        // testing valid parameters (if)
        runMainWithArgs(new String[]{"2", "1"});
    }

    @Test
    void testMainWithInvalidArgs() throws InterruptedException {
        // testing catch block (for example string parameters)
        runMainWithArgs(new String[]{"abc", "def"});
    }

    // main thread and interrupting it
    private void runMainWithArgs(String[] args) throws InterruptedException {
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream("\n".getBytes()));

        Thread t = Thread.ofVirtual().start(() -> Main.main(args));

        t.interrupt(); // cacth block in main
        t.join(1000);

        assertFalse(t.isAlive(), "Main thread should terminate after interruption");
        System.setIn(originalIn);
    }
}