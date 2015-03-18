import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.CRC32;

/**
 * CS2105 Assignment 0
 * Question 4
 * @author Andhieka Putra
 */
public class Checksum {
    private static int BUFFER_SIZE = 1 << 25; //in bytes. 32 MB.

    private BufferedInputStream inputStream;

    public void setInputFile(String inputFile) throws Exception {
        try {
            inputStream = new BufferedInputStream(new FileInputStream(inputFile));
        } catch (Exception e) {
            System.out.println("There is an error opening " + inputFile);
            throw e;
        }
    }

    public long getChecksum() throws IOException {
        CRC32 crc = new CRC32();
        byte[] buffer = new byte[BUFFER_SIZE];
        while(inputStream.available() != 0) {
            int numBytes = inputStream.read(buffer);
            crc.update(buffer, 0, numBytes);
        }
        inputStream.close();
        return crc.getValue();
    }


    public static void main(String[] args) {
        try {
            Checksum checksum = new Checksum();
            checksum.setInputFile(args[0]);
            System.out.println(checksum.getChecksum());
        } catch (Exception e) {
            System.out.println("Failed to calculate checksum. Now exiting.");
        }

    }
}
