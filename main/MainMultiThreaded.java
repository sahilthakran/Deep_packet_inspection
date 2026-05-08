package main;

import engine.DPIEngine;

public class MainMultiThreaded {

    public static void main(String[] args) {

        String inputFile = "test_dpi.pcap";

        DPIEngine.Config config = new DPIEngine.Config();
        config.numLoadBalancers = 2;
        config.fpsPerLB = 2;
        config.verbose = true;

        DPIEngine engine = new DPIEngine(config);

        engine.initialize();
        engine.start();

        engine.processFile(inputFile, "output.pcap");

        engine.waitForCompletion();

        System.out.println(engine.generateReport());

        engine.stop();

        System.out.println("Done");
    }
}