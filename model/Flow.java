package model;

public class Flow {

    private FiveTuple tuple;

    private AppType appType = AppType.UNKNOWN;
    private String sni = "";

    private long packetCount = 0;
    private long bytes = 0;

    private long lastSeen = System.currentTimeMillis();

    private boolean blocked = false;

    public Flow(FiveTuple tuple) {
        this.tuple = tuple;
    }

    public void update(int packetSize) {
        this.packetCount++;
        this.bytes += packetSize;
        this.lastSeen = System.currentTimeMillis();
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public long getPacketCount() {
        return packetCount;
    }

    public long getBytes() {
        return bytes;
    }

    public void setAppType(AppType appType) {
        this.appType = appType;
    }

    public void setSni(String sni) {
        this.sni = sni;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
}