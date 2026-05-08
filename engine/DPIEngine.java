package engine;

import model.*;
import rules.RuleManager;
import tracker.ConnectionTracker;
import threading.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class DPIEngine {

    // ================= CONFIG =================
    public static class Config {
        public int numLoadBalancers = 2;
        public int fpsPerLB = 2;
        public int queueSize = 10000;
        public String rulesFile = "";
        public boolean verbose = false;
    }

    private Config config;

    // Core components
    private RuleManager ruleManager;
    private ConnectionTracker connectionTracker;

    // Thread managers (simplified)
    private LoadBalancer loadBalancer;
    private FastPathProcessor[] processors;

    // Output
    private ThreadSafeQueue<Packet> outputQueue;
    private Thread outputThread;

    // Control
    private AtomicBoolean running = new AtomicBoolean(false);
    private AtomicBoolean processingComplete = new AtomicBoolean(false);

    // ================= CONSTRUCTOR =================
    public DPIEngine(Config config) {
        this.config = config;
        this.ruleManager = new RuleManager();
        this.connectionTracker = new ConnectionTracker(0, 10000);

        this.outputQueue = new ThreadSafeQueue<>(config.queueSize);
    }

    // ================= INIT =================
    public boolean initialize() {

        // Init processors
        int totalFP = config.numLoadBalancers * config.fpsPerLB;
        processors = new FastPathProcessor[totalFP];

        for (int i = 0; i < totalFP; i++) {
            processors[i] = new FastPathProcessor(i, ruleManager, connectionTracker, outputQueue);
        }

        // Init Load Balancer
        loadBalancer = new LoadBalancer(processors);

        return true;
    }

    // ================= START =================
    public void start() {
        running.set(true);

        // Start FP threads
        for (FastPathProcessor fp : processors) {
            new Thread(fp).start();
        }

        // Start output thread
        outputThread = new Thread(this::outputThreadFunc);
        outputThread.start();
    }

    // ================= STOP =================
    public void stop() {
        running.set(false);
    }

    // ================= PROCESS FILE =================
    public boolean processFile(String inputFile, String outputFile) {

        System.out.println("Processing PCAP: " + inputFile);
    
        reader.PcapReader reader = new reader.PcapReader();
    
        if (!reader.open(inputFile)) {
            System.out.println("Failed to open PCAP");
            return false;
        }
    
        byte[] rawData;
    
        while ((rawData = reader.readNextPacket()) != null) {
    
            Packet packet = parser.PacketParser.parse(rawData);
    
            if (packet != null) {
                loadBalancer.distribute(packet);
            }
        }
    
        reader.close();
    
        processingComplete.set(true);
    
        return true;
    }
    // ================= WAIT =================
    public void waitForCompletion() {
        try {
            if (outputThread != null) {
                outputThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // ================= OUTPUT THREAD =================
    private void outputThreadFunc() {

        while (running.get() || !outputQueue.isEmpty()) {
            try {
                Packet packet = outputQueue.pop();

                if (packet != null) {
                    handleOutput(packet);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleOutput(Packet packet) {
        if (config.verbose) {
            System.out.println("Forwarded: " + packet);
        }
    }

    // ================= RULES =================
    public void blockIP(String ip) {
        ruleManager.blockIP(ip);
    }

    public void unblockIP(String ip) {
        ruleManager.unblockIP(ip);
    }

    public void blockDomain(String domain) {
        ruleManager.blockDomain(domain);
    }

    public void unblockDomain(String domain) {
        ruleManager.unblockDomain(domain);
    }

    // ================= REPORT =================
    public String generateReport() {

        StringBuilder sb = new StringBuilder();
        sb.append("\n===== DPI REPORT =====\n");
        sb.append("Active Connections: ").append(connectionTracker.getActiveCount()).append("\n");
        sb.append("Stats: ").append(connectionTracker.getStats()).append("\n");

        return sb.toString();
    }

    public void printStatus() {
        System.out.println("Running: " + running.get());
    }

    public boolean isRunning() {
        return running.get();
    }
}