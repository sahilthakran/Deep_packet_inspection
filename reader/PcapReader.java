package reader;

import java.io.DataInputStream;
import java.io.FileInputStream;

public class PcapReader {

    private DataInputStream input;

    // Open PCAP file
    public boolean open(String filePath) {
        try {
            input = new DataInputStream(new FileInputStream(filePath));

            // Skip global header (24 bytes)
            input.skipBytes(24);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Read next packet
    public byte[] readNextPacket() {

        try {

            // ================= PACKET HEADER =================
            byte[] header = new byte[16];

            input.readFully(header);

            // Included packet length 
            int length =
                    ((header[8] & 0xFF)) |
                    ((header[9] & 0xFF) << 8) |
                    ((header[10] & 0xFF) << 16) |
                    ((header[11] & 0xFF) << 24);

            // Validation
            if (length <= 0 || length > 65535) {
                return null;
            }

            // ================= PACKET DATA =================
            byte[] data = new byte[length];

            input.readFully(data);

            return data;

        } catch (Exception e) {
            return null;
        }
    }

    // Close reader
    public void close() {
        try {
            if (input != null) {
                input.close();
            }
        } catch (Exception ignored) {
        }
    }
}