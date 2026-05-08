package threading;

import model.Packet;

public class LoadBalancer {

    private FastPathProcessor[] processors;
    private int index = 0;

    public LoadBalancer(FastPathProcessor[] processors) {
        this.processors = processors;
    }

    public void distribute(Packet packet) {
        processors[index].submit(packet);
        index = (index + 1) % processors.length;
    }
}