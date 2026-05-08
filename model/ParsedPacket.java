package model;



public class ParsedPacket {

    public boolean hasIp;
    public String srcIp;
    public String destIp;

    public boolean hasTcp;
    public boolean hasUdp;

    public int srcPort;
    public int destPort;

    public int protocol;
    public int tcpFlags;

    public int payloadLength;
}
