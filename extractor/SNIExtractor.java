package extractor;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class SNIExtractor {

    // Check TLS Client Hello
    public static boolean isTLSClientHello(byte[] data) {
        if (data == null || data.length < 6) return false;

        return (data[0] == 0x16 && data[5] == 0x01);
        // 0x16 = Handshake
        // 0x01 = Client Hello
    }

    // Extract SNI (main function)
    public static Optional<String> extract(byte[] payload) {
        try {
            if (!isTLSClientHello(payload)) return Optional.empty();

            int offset = 43; // Skip to Session ID

            // Session ID
            int sessionLen = payload[offset] & 0xFF;
            offset += 1 + sessionLen;

            // Cipher Suites
            int cipherLen = readUint16(payload, offset);
            offset += 2 + cipherLen;

            // Compression Methods
            int compLen = payload[offset] & 0xFF;
            offset += 1 + compLen;

            // Extensions Length
            int extLen = readUint16(payload, offset);
            offset += 2;

            int end = offset + extLen;

            while (offset + 4 <= end) {

                int extType = readUint16(payload, offset);
                int extDataLen = readUint16(payload, offset + 2);
                offset += 4;

                // SNI Extension
                if (extType == 0x0000) {

                    int sniLen = readUint16(payload, offset + 3);

                    String sni = new String(
                            payload,
                            offset + 5,
                            sniLen,
                            StandardCharsets.UTF_8
                    );

                    return Optional.of(sni);
                }

                offset += extDataLen;
            }

        } catch (Exception e) {
            // ignore malformed packets
        }

        return Optional.empty();
    }

    // Helper: read 2 bytes big-endian
    private static int readUint16(byte[] data, int offset) {
        return ((data[offset] & 0xFF) << 8) |
               (data[offset + 1] & 0xFF);
    }
}