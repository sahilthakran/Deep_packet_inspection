# DPI Engine – Deep Packet Inspection System (Java)

## Project Overview

This project is a Java-based Deep Packet Inspection (DPI) Engine developed to analyze, inspect, classify, and filter real network traffic from PCAP files using low-level packet parsing techniques.

The system reads raw packets, extracts protocol information from Ethernet, IPv4, TCP/UDP headers, identifies websites and applications using TLS SNI extraction, tracks complete network flows using Five-Tuple architecture, and applies rule-based packet filtering and blocking.

The project simulates how modern firewalls, ISPs, enterprise monitoring systems, and cybersecurity tools inspect encrypted HTTPS traffic.

---

# Main Features

- Low-Level Packet Parsing
- Ethernet Header Parsing
- IPv4 Header Parsing
- TCP/UDP Parsing
- TLS SNI Extraction
- HTTPS Website Detection
- Application Identification
- Rule-Based Packet Filtering
- IP / Domain / Application Blocking
- Five-Tuple Flow Tracking
- Multithreaded Packet Processing
- Producer Consumer Architecture
- Thread-Safe Queues
- High Performance Packet Pipeline

---

# What is Deep Packet Inspection (DPI)?

Deep Packet Inspection (DPI) is a network traffic analysis technique where the actual contents inside packets are inspected instead of only checking source and destination addresses.

Traditional firewalls mainly inspect:

- Source IP
- Destination IP
- Port Numbers

DPI systems inspect:

- IP Address
- Port Number
- Protocol Type
- TCP/UDP Headers
- Payload Data
- TLS Handshake
- HTTP Host
- TLS SNI
- Application Signatures

This allows the system to identify websites, applications, and traffic patterns even when traffic is encrypted using HTTPS.

---

# Real World Use Cases

## Internet Service Providers (ISPs)
- Website blocking
- Traffic monitoring
- Bandwidth throttling
- Application detection

## Enterprise Networks
- Blocking social media
- Monitoring employee traffic
- Restricting applications

## Cybersecurity Systems
- Malware detection
- Suspicious traffic analysis
- Intrusion detection

## Parental Control Systems
- Blocking harmful websites
- Filtering internet traffic

---

# Technologies Used

| Technology | Purpose |
|------------------------|---------------------------|
| Java                   | Main Development Language |
| ByteBuffer             | Low-Level Packet Parsing |
| PCAP Files             | Reading Network Traffic |
| TCP/IP                 | Network Communication |
| TLS SNI                | HTTPS Domain Detection |
| Multithreading         | Parallel Packet Processing |
| HashMap                | Flow Tracking |
| Producer Consumer Model| Thread Communication |

---

# Project Architecture

```text
PCAP File
   ↓
Read Raw Packet
   ↓
Parse Ethernet Header
   ↓
Parse IPv4 Header
   ↓
Parse TCP/UDP Header
   ↓
Extract Payload
   ↓
Extract TLS SNI / HTTP Host
   ↓
Identify Application
   ↓
Track Network Flow
   ↓
Apply Blocking Rules
   ↓
Forward or Drop Packet
```

---

# Project Structure

```text
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
│   ├── Packet.java
│   └── ParsedPacket.java
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
```

---

# Networking Basics

## Network Layers

| Layer             | Protocols |
|-------------------|-----------|
| Application Layer | HTTP, HTTPS |
| Transport Layer   | TCP, UDP |
| Network Layer     | IPv4 |
| Data Link Layer   | Ethernet |

---

# Packet Structure

Every network packet contains multiple protocol headers.

```text
┌─────────────────────┐
│ Ethernet Header     │
├─────────────────────┤
│ IPv4 Header         │
├─────────────────────┤
│ TCP / UDP Header    │
├─────────────────────┤
│ Payload Data        │
└─────────────────────┘
```

---

# Ethernet Header

## Size
14 Bytes

## Contains
- Source MAC Address
- Destination MAC Address
- EtherType

## EtherType Values

| EtherType | Meaning |
|-----------|---------|
| 0x0800    | IPv4 |
| 0x86DD    | IPv6 |

---

# IPv4 Header

The IPv4 header contains routing and protocol information.

## Important Fields

| Field          | Purpose |
|----------------|---------|
| Source IP      | Sender Address |
| Destination IP | Receiver Address |
| Protocol       | TCP / UDP |
| TTL            | Packet Lifetime |
| Header Length  | IP Header Size |

---

# Protocol Numbers

| Protocol | Number |
|----------|--------|
| TCP      | 6 |
| UDP      | 17 |

---

# TCP Header

## Important Fields

| Field | Purpose |
|------|---------|
| Source Port | Sender Application |
| Destination Port | Receiver Service |
| Sequence Number | Packet Ordering |
| ACK Number | Acknowledgement |
| Flags | SYN, ACK, FIN |

---

# Common Ports

| Port | Service |
|------|----------|
| 80 | HTTP |
| 443 | HTTPS |
| 53 | DNS |

---

# Five Tuple Architecture

Every network connection is uniquely identified using five values:

- Source IP
- Destination IP
- Source Port
- Destination Port
- Protocol

## Example

```text
192.168.1.10 → 142.250.183.78
54321 → 443
TCP
```

All packets with the same five values belong to the same network flow.

---

# Why Flow Tracking is Important?

The first few packets of a connection usually do not contain enough information to identify the application.

The DPI engine tracks all packets belonging to the same flow.

Once the TLS Client Hello packet is received and the SNI is extracted:

- The flow gets classified
- Blocking rules are applied
- All future packets of the same flow are controlled

---

# What is TLS SNI?

SNI stands for Server Name Indication.

When a browser opens an HTTPS website:

```text
https://www.youtube.com
```

The browser sends the domain name during the TLS Client Hello handshake.

Even though HTTPS traffic is encrypted, the SNI remains visible.

## Example

```text
SNI = www.youtube.com
```

This allows DPI systems to identify HTTPS websites without decrypting traffic.

---

# PacketParser.java Explanation

`PacketParser.java` is the core component of the project.

Its main responsibility is parsing raw packet bytes and extracting networking information.

---

# Packet Parsing Process

## Step 1 – Convert Byte Array into Buffer

```java
ByteBuffer buffer = ByteBuffer.wrap(data);
```

This converts the raw packet bytes into a readable buffer structure.

---

# Ethernet Header Parsing

```java
int etherType = ((data[12] & 0xFF) << 8) | (data[13] & 0xFF);
```

This extracts the EtherType field.

If the packet is not IPv4:

```java
if (etherType != 0x0800) {
    return null;
}
```

The packet is ignored.

---

# IPv4 Header Parsing

```java
int versionIhl = buffer.get() & 0xFF;
```

This extracts:

- IP Version
- Header Length

## Calculate Header Length

```java
int ihl = (versionIhl & 0x0F) * 4;
```

---

# Protocol Extraction

```java
buffer.position(14 + 9);
int protocol = buffer.get() & 0xFF;
```

Used to identify:

- TCP
- UDP

---

# Source and Destination IP Extraction

```java
buffer.position(14 + 12);

String srcIp = readIP(buffer);
String destIp = readIP(buffer);
```

## Example

```text
192.168.1.10
142.250.183.78
```

---

# Port Extraction

```java
int srcPort = buffer.getShort() & 0xFFFF;
int destPort = buffer.getShort() & 0xFFFF;
```

## Example

```text
54321
443
```

---

# TCP Payload Extraction

```java
int dataOffset = ((data[transportStart + 12] >> 4) & 0xF) * 4;
```

This calculates the TCP header size.

Payload begins after the TCP header.

---

# UDP Payload Extraction

UDP header size is fixed:

```text
8 bytes
```

```java
payloadStart += 8;
```

---

# readIP() Method

```java
private static String readIP(ByteBuffer buffer)
```

This converts raw bytes into a readable IP address.

## Example

```text
C0 A8 01 01
↓
192.168.1.1
```

---

# TLS SNI Extraction Logic

The DPI engine inspects TLS Client Hello packets.

## Steps

### Step 1
Verify TLS Handshake Packet

```text
0x16 = Handshake
```

### Step 2
Verify Client Hello

```text
0x01 = Client Hello
```

### Step 3
Navigate to TLS Extensions

### Step 4
Find SNI Extension

```text
0x0000
```

### Step 5
Extract Domain Name

```text
www.youtube.com
```

---

# Application Detection

The extracted SNI is mapped to applications.

## Example

```java
if(sni.contains("youtube"))
```

Then:

```text
Application = YouTube
```

---

# Blocking Rules

The system supports multiple blocking mechanisms.

| Type                 | Example |
|----------------------|---------|
| IP Blocking          | 192.168.1.10 |
| Domain Blocking      | youtube.com |
| Application Blocking | YouTube |

---

# Packet Filtering Flow

```text
Packet Arrives
      ↓
Parse Packet
      ↓
Extract SNI
      ↓
Identify Application
      ↓
Check Rules
      ↓
Blocked?
   /      \
 YES      NO
 ↓         ↓
DROP    FORWARD
```

---

# Multithreading Architecture

The project implements multithreaded packet processing for high performance.

## Used Concepts
- Producer Consumer Model
- Thread Pools
- Thread Safe Queues
- Parallel Processing

---

# Threads Used

| Thread | Responsibility |
|--------|----------------|
| Reader Thread | Reads packets from PCAP |
| Load Balancer Thread | Distributes traffic |
| Fast Path Thread | Processes packets |
| Writer Thread | Writes output |

---

# Hash Based Load Balancing

```java
hash(fiveTuple) % totalThreads
```

This ensures:

- Same flow always goes to same thread
- Proper flow tracking
- Consistent packet processing

---

# Example Flow Processing

```text
Packet 1 → SYN
Packet 2 → SYN ACK
Packet 3 → ACK
Packet 4 → TLS Client Hello
           ↓
     Extract SNI
           ↓
      youtube.com
           ↓
   Mark Flow Blocked
           ↓
 All Future Packets DROP
```

---

# Error Handling

The system handles:

- Invalid Packets
- Short Packets
- Unsupported Protocols
- Corrupted Data
- Null Buffers

## Example

```java
if(data == null || data.length < 34)
```

---

# Performance Optimizations

The project uses several optimizations:

- ByteBuffer for efficient parsing
- Minimal object creation
- Hash based thread distribution
- Producer Consumer queues
- Thread pooling
- Parallel packet processing

---

# Summary

This project demonstrates:

1. Network Protocol Parsing  
2. Deep Packet Inspection  
3. TLS SNI Extraction  
4. HTTPS Website Detection  
5. Five Tuple Flow Tracking  
6. Rule-Based Packet Filtering  
7. Producer Consumer Architecture  
8. Multithreaded System Design  
9. Thread-Safe Queue Implementation  
10. High Performance Packet Processing  

The core idea behind this project is that even encrypted HTTPS traffic exposes the destination domain during the TLS handshake using SNI, allowing the DPI engine to classify, monitor, and control application traffic efficiently.