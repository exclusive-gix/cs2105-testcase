
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class ReliableNetworkTests {
    private static final Integer PORT_RECEIVE = 2000;

    @Before
    public void setupFileReceiver() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileReceiver fileReceiver = new FileReceiver(PORT_RECEIVE);
                    fileReceiver.receiveFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Test
    public void testTransferSmallImage() throws Exception {
        FileSender fileSender = new FileSender("doge.jpg", "localhost", PORT_RECEIVE.toString(), "doge-copy.jpg");
        fileSender.sendFile();

        Checksum inspector = new Checksum();
        inspector.setInputFile("doge.jpg");
        long originalChecksum = inspector.getChecksum();

        inspector.setInputFile("doge-copy.jpg");
        long cloneChecksum = inspector.getChecksum();

        assertEquals(originalChecksum, cloneChecksum);

        Path path = Paths.get("doge-copy.jpg");
        Files.delete(path);
    }

    @Test
    public void testTransferLargeImage() throws Exception {
        FileSender fileSender = new FileSender("rc.jpg", "localhost", PORT_RECEIVE.toString(), "rc-copy.jpg");
        fileSender.sendFile();

        Checksum inspector = new Checksum();
        inspector.setInputFile("rc.jpg");
        long originalChecksum = inspector.getChecksum();

        inspector.setInputFile("rc-copy.jpg");
        long cloneChecksum = inspector.getChecksum();

        assertEquals(originalChecksum, cloneChecksum);

        Path path = Paths.get("rc-copy.jpg");
        Files.delete(path);
    }
}