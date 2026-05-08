package main;

import reader.PcapReader;
import parser.PacketParser;
import model.Packet;

public class MainSimple {

    public static void main(String[] args) {

        String inputFile = "./test_dpi.pcap";

        PcapReader reader = new PcapReader();

        if (!reader.open(inputFile)) {
            System.out.println("File can't open");
            return;
        }

        int count = 0;
        byte[] rawData;

        while ((rawData = reader.readNextPacket()) != null) {

            Packet parsed = PacketParser.parse(rawData);

            if (parsed == null) {
                System.out.println("Parsing failed");
                continue;
            }

            System.out.println("Packet #" + count);
            System.out.println("Src: " + parsed.getSrcIp() + ":" + parsed.getSrcPort());
            System.out.println("Dst: " + parsed.getDestIp() + ":" + parsed.getDestPort());
            System.out.println("Protocol: " + parsed.getProtocol());
            System.out.println("----------------------");

            count++;
        }

        reader.close();

        System.out.println("Total Packets: " + count);
    }
}