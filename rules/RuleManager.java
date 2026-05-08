package rules;

import model.Packet;

public class RuleManager {

    public void blockIP(String ip) {}
    public void unblockIP(String ip) {}

    public void blockDomain(String domain) {}
    public void unblockDomain(String domain) {}

    public boolean isBlocked(Packet packet) {
        return false;
    }
}