package threading;

import model.Packet;
import rules.RuleManager;
import tracker.ConnectionTracker;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FastPathProcessor implements Runnable {

    private int id;
    private RuleManager ruleManager;
    private ConnectionTracker tracker;
    private ThreadSafeQueue<Packet> outputQueue;

    private BlockingQueue<Packet> inputQueue = new LinkedBlockingQueue<>();

    public FastPathProcessor(int id,
                             RuleManager ruleManager,
                             ConnectionTracker tracker,
                             ThreadSafeQueue<Packet> outputQueue) {
        this.id = id;
        this.ruleManager = ruleManager;
        this.tracker = tracker;
        this.outputQueue = outputQueue;
    }

    public void submit(Packet packet) {
        inputQueue.offer(packet);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Packet packet = inputQueue.take();

                // Apply rules (dummy for now)
                if (!ruleManager.isBlocked(packet)) {
                    outputQueue.push(packet);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}