import org.junit.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class UnreliableNetworkTests {
    private static final Integer PORT_RECEIVE = 2000;
    private static final Integer PORT_UNRELINET = 5000;
    private static final float DATA_CORRUPT_RATE = 0.1f;
    private static final float ACK_CORRUPT_RATE = 0.0f;
    private static final float DATA_LOSS_RATE = 0.0f;
    private static final float ACK_LOSS_RATE = 0.0f;

    FileReceiver fileReceiver = null;

    @BeforeClass
    public static void setupUnreliableNetwork() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new UnreliNET(
                        DATA_CORRUPT_RATE,
                        ACK_CORRUPT_RATE,
                        DATA_LOSS_RATE,
                        ACK_LOSS_RATE,
                        PORT_UNRELINET,
                        PORT_RECEIVE
                );
            }
        }).start();
    }

    @Before
    public void setupFileReceiver() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    fileReceiver = new FileReceiver(PORT_RECEIVE);
                    fileReceiver.receiveFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @After
    public void closeFileReceiver() {
        if (fileReceiver != null) {
            fileReceiver.close();
        }
    }

    @After
    public void cleanUpFiles() throws IOException {
        Path p1 = Paths.get("doge-copy.jpg");
        Path p2 = Paths.get("rc-copy.jpg");
        try {
            Files.delete(p1);
        } catch (Exception e) {}
        try {
            Files.delete(p2);
        } catch (Exception e) {}
    }

    @Test
    public void testTransferSmallImage() throws Exception {
        FileSender fileSender = new FileSender("doge.jpg", "localhost", PORT_UNRELINET.toString(), "doge-copy.jpg");
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

    //@Ignore
    @Test
    public void testTransferLargeImage() throws Exception {
        FileSender fileSender = new FileSender("rc.jpg", "localhost", PORT_UNRELINET.toString(), "rc-copy.jpg");
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