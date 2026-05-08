package model;

public class Packet {

    private byte[] data;
    private byte[] payload;   // ✅ FIXED

    private String srcIp;
    private String destIp;
    private int srcPort;
    private int destPort;
    private int protocol;

    // ================= CONSTRUCTOR =================
    public Packet() {}

    public Packet(byte[] data) {
        this.data = data;
    }

    // ================= GETTERS =================
    public byte[] getData() {
        return data;
    }

    public byte[] getPayload() {
        return payload;
    }

    public String getSrcIp() {
        return srcIp;
    }

    public String getDestIp() {
        return destIp;
    }

    public int getSrcPort() {
        return srcPort;
    }

    public int getDestPort() {
        return destPort;
    }

    public int getProtocol() {
        return protocol;
    }

    // ================= SETTERS =================
    public void setData(byte[] data) {
        this.data = data;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public void setSrcIp(String srcIp) {
        this.srcIp = srcIp;
    }

    public void setDestIp(String destIp) {
        this.destIp = destIp;
    }

    public void setSrcPort(int srcPort) {
        this.srcPort = srcPort;
    }

    public void setDestPort(int destPort) {
        this.destPort = destPort;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    // ================= UTIL =================
    public int getSize() {
        return data != null ? data.length : 0;
    }

    @Override
    public String toString() {
        return "Packet{" +
                "src=" + srcIp + ":" + srcPort +
                ", dst=" + destIp + ":" + destPort +
                ", protocol=" + protocol +
                '}';
    }
}