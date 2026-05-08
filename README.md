DPI Engine – Deep Packet Inspection System (Java)
1. Project Overview
This project is a Deep Packet Inspection (DPI) Engine built using Java.
The system reads network packets from a PCAP file, extracts useful information from packets, identifies applications/websites using SNI, and blocks traffic based on rules.

The project mainly focuses on:
Packet Parsing
TCP/IP Networking
TLS SNI Extraction
Application Detection
Packet Filtering
Flow Tracking

2. What is Deep Packet Inspection (DPI)?
Deep Packet Inspection means:
Instead of only checking source and destination IP addresses, we inspect the actual content inside packets.

Normal Firewall:
Checks:
-> Source IP
-> Destination IP
-> Port

DPI Engine:
-> Checks:
-> IP
-> Port
-> Protocol
-> Payload Data
-> TLS SNI
-> HTTP Host
-> Application Type


3. Real World Use Cases
ISPs
-> Block websites
-> Throttle YouTube/Netflix
Companies
-> Block social media
-> Monitor traffic
Cyber Security
-> Detect malware
-> Detect suspicious traffic
Parental Control
-> Block adult websites
 

1. Technologies Used
Technology	              Purpose
Java		              Main Language
ByteBuffer		          Packet Parsing
PCAP Files	              Network Traffic
TCP/IP	              	  Networking
TLS SNI	              	  Website Detection
Multithreading	          Performance




5. Project Flow
PCAP File
    ↓
Read Raw Packet
    ↓
Parse Ethernet Header
    ↓
Parse IP Header
    ↓
Parse TCP/UDP Header
    ↓
Extract Payload
    ↓
Extract SNI / HTTP Host
    ↓
Identify Application
    ↓
Apply Blocking Rules
    ↓
Forward or Drop Packet



6. Project Structure
DEEP_PACKET_INSPECTION/
│
├── engine/
│   └── DPIEngine.java
│
├── extractor/
│   └── SNIExtractor.java
│
├── main/
│   ├── MainSimple.java
│   └── MainMultiThreaded.java
│
├── model/
│   ├── AppType.java        
│   ├── ConnectionState.java      
│   ├── FiveTuple.java     
│   ├── Flow.java             
│   ├── ParsedPacket.java
│   
│
├── parser/
│   └── PacketParser.java
│
├── reader/
│   └── PcapReader.java
│
├── rules/
│   └── RuleManager.java
│
├── threading/
│   ├── FastPathProcessor.java
│   ├── LoadBalancer.java
│   └── ThreadSafeQueue.java
│
├── tracker/
│   └── ConnectionTracker.java
│
├── util/
│   └── PlatformUtil.java
│
├── test_dpi.pcap
├── run.sh
└── README.md

7. Networking Basics
Network Layers
Application Layer → HTTP, HTTPS
Transport Layer   → TCP, UDP
Network Layer     → IP
Data Link Layer   → Ethernet


8. Packet Structure
Every packet contains multiple headers.
┌─────────────────────┐
│ Ethernet Header     │
├─────────────────────┤
│ IP Header           │
├─────────────────────┤
│ TCP/UDP Header      │
├─────────────────────┤
│ Payload Data        │
└─────────────────────┘

9. Ethernet Header
Size: 14 Bytes
Contains:

-> Source MAC
-> Destination MAC
-> EtherType

Important EtherTypes:
EtherType	Meaning
0x0800	    IPv4
0x86DD		IPv6

10. IP Header
Contains:
Field	              Purpose

Source IP   	      Sender
Destination IP 	      Receiver
Protocol	          TCP/UDP
TTL                   Packet lifetime

Protocol Numbers:

Protocol	Number
TCP	          6
UDP	          17

11. TCP Header
Contains:
Field	            Purpose
Source Port      	Sender App
Destination Port	Receiver Service
Sequence Number	    Packet Order
Flags	            SYN, ACK, FIN

Common Ports:
Port	      Service
80	            HTTP
443            	HTTPS
53	            DNS

12. Five Tuple
A network connection is identified using:

Source IP
Destination IP
Source Port
Destination Port
Protocol

Example:

192.168.1.10
→ 142.250.183.78
54321 → 443
TCP

All packets with same 5 values belong to same connection.

13. What is SNI?
SNI = Server Name Indication
When browser opens HTTPS website:
https://www.youtube.com

Browser sends domain name inside TLS Client Hello.
Even though HTTPS is encrypted,
SNI is visible.
Example:
SNI = www.youtube.com
This helps DPI identify websites.

14. PacketParser.java Explanation
This is the core file of the project.
Purpose:
-> Parse raw packet bytes
-> Extract networking information

15. Packet Parsing Flow
ByteBuffer buffer = ByteBuffer.wrap(data);

Converts byte array into readable buffer.

16. Ethernet Header Parsing
code --
int etherType =
((data[12] & 0xFF) << 8) |
(data[13] & 0xFF);

Checks packet type.
If not IPv4:

if (etherType != 0x0800) {
    return null;
}

17.  IP Header Parsing
int versionIhl = buffer.get() & 0xFF;

Extracts:
-> IP Version
-> Header Length

Header length:
int ihl = (versionIhl & 0x0F) * 4;

18. Protocol Extraction
buffer.position(14 + 9);
int protocol = buffer.get() & 0xFF;

Reads:
-> TCP
-> UDP

19. Source and Destination IP
buffer.position(14 + 12);

String srcIp = readIP(buffer);
String destIp = readIP(buffer);

Example:

192.168.1.10
142.250.183.78

20. Port Extraction
int srcPort = buffer.getShort() & 0xFFFF;
int destPort = buffer.getShort() & 0xFFFF;

Example:
54321
443

21. TCP Payload Extraction
 
int dataOffset =
((data[transportStart + 12] >> 4) & 0xF) * 4;

Finds TCP header size.
Payload starts after TCP header.

22. UDP Payload Extraction
UDP header size always:
8 bytes
So:
payloadStart += 8;

23. readIP() Method
private static String readIP(ByteBuffer buffer)

Converts bytes into IP address.
Example:
C0 A8 01 01
↓
192.168.1.1

24. SNI Extraction Logic

Steps:
Step 1
Check TLS packet
0x16 = Handshake

Step 2
Check Client Hello
0x01 = Client Hello

Step 3
Navigate to Extensions

Step 4
Find SNI Extension
0x0000

Step 5
Extract Domain
www.youtube.com

25. Application Detection
Example:
if(sni.contains("youtube"))
Then:
Application = YouTube

26. Blocking Rules
Rules can block:

Type		Example
IP			192.168.1.10
Domain		youtube.com
App			YouTube

27. Blocking Flow

Packet Arrives
    ↓
Parse Packet
    ↓
Extract SNI
    ↓
Check Rules
    ↓
Blocked?
   / \
 YES  NO
 ↓     ↓
DROP  FORWARD

28. Multithreading Architecture
Project uses:
-> Producer Consumer Model
-> Thread Safe Queues

29. Threads Used
Thread     	    	Work
Reader Thread	    Reads PCAP
LB Thread	    	Load Balancing
FP Thread	    	Packet Processing
Writer Thread	    Output Writing

30. Why Multithreading?
Benefits:
-> Faster processing
-> Better CPU usage
-> Large traffic handling
-> Parallel packet inspection

31. Hash Based Load Balancing

hash(fiveTuple) % totalThreads
Ensures:
-> Same flow always goes to same thread.
-> Very important for flow tracking.

32. Flow Tracking
Flow means:
All packets belonging to same connection.
Example:
Client ↔ YouTube Server
Sored using:

HashMap<FiveTuple, Flow>

33. Why Flow Tracking Important?
Because:
First few packets may not contain SNI.

Once SNI detected:
All future packets of same flow are blocked.

34. Example Flow
Packet 1 → SYN
Packet 2 → SYN ACK
Packet 3 → ACK
Packet 4 → TLS Client Hello
              ↓
        SNI = youtube
              ↓
        Mark Flow Blocked
              ↓
All next packets DROP

35. Error Handling

Project handles:
-> Invalid packets
-> Short packets
-> Unsupported protocols
-> Corrupted data
Example:
if(data == null || data.length < 34)

36. Performance Optimizations

Used:
-> ByteBuffer
-> Minimal object creation
-> Thread pools
-> Hash based distribution

SUMMARY

This DPI engine demonstrates:

1.Network Protocol Parsing - Understanding packet structure
2.Deep Packet Inspection - Looking inside encrypted connections
3.Flow Tracking - Managing stateful connections
4.Multi-threaded Architecture - Scaling with thread pools
5.Producer-Consumer Pattern - Thread-safe queues

The key insight is that even HTTPS traffic leaks the destination domain in the TLS handshake, allowing network operators to identify and control application usage.

