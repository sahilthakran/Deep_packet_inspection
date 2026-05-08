package parser;

import model.Packet;
import java.nio.ByteBuffer;

public class PacketParser {

    public static Packet parse(byte[] data) {

        if (data == null || data.length < 34) {
            return null;
        }

        try {

            // Only IPv4 packets
            int etherType = ((data[12] & 0xFF) << 8) | (data[13] & 0xFF);

            if (etherType != 0x0800) {
                return null;
            }

            Packet packet = new Packet();

            ByteBuffer buffer = ByteBuffer.wrap(data);

            // IP header start
            buffer.position(14);

            int versionIhl = buffer.get() & 0xFF;
            int ihl = (versionIhl & 0x0F) * 4;

            // Protocol
            buffer.position(23);
            int protocol = buffer.get() & 0xFF;

            // Only TCP or UDP
            if (protocol != 6 && protocol != 17) {
                return null;
            }

            // Source IP
            buffer.position(26);
            String srcIp = readIP(buffer);

            // Destination IP
            String destIp = readIP(buffer);

            packet.setSrcIp(srcIp);
            packet.setDestIp(destIp);
            packet.setProtocol(protocol);

            // TCP/UDP ports
            int transportStart = 14 + ihl;

            buffer.position(transportStart);

            int srcPort = buffer.getShort() & 0xFFFF;
            int destPort = buffer.getShort() & 0xFFFF;

            packet.setSrcPort(srcPort);
            packet.setDestPort(destPort);

            return packet;

        } catch (Exception e) {
            return null;
        }
    }

    private static String readIP(ByteBuffer buffer) {

        int a = buffer.get() & 0xFF;
        int b = buffer.get() & 0xFF;
        int c = buffer.get() & 0xFF;
        int d = buffer.get() & 0xFF;

        return a + "." + b + "." + c + "." + d;
    }
}