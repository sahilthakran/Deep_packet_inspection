package util;

import java.nio.ByteOrder;

public class PlatformUtil {

    // Check system endianness
    public static boolean isLittleEndian() {
        return ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN);
    }

    // Swap 16-bit (short)
    public static short swapBytes16(short value) {
        return (short) (((value & 0xFF00) >> 8) | ((value & 0x00FF) << 8));
    }

    // Swap 32-bit (int)
    public static int swapBytes32(int value) {
        return ((value >>> 24)) |
               ((value >> 8) & 0x0000FF00) |
               ((value << 8) & 0x00FF0000) |
               ((value << 24));
    }

    // Network → Host (16-bit)
    public static short netToHost16(short netValue) {
        return isLittleEndian() ? swapBytes16(netValue) : netValue;
    }

    // Network → Host (32-bit)
    public static int netToHost32(int netValue) {
        return isLittleEndian() ? swapBytes32(netValue) : netValue;
    }

    // Host → Network (16-bit)
    public static short hostToNet16(short hostValue) {
        return netToHost16(hostValue);
    }

    // Host → Network (32-bit)
    public static int hostToNet32(int hostValue) {
        return netToHost32(hostValue);
    }
}