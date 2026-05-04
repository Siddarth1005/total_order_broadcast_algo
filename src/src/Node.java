import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Node {

    private final int nodeID;
    private int nextSeqToDeliver;
    private volatile Node leader;
    private final Set<Message> buffer;
    private int nextSeqToAssign;
    private final Queue<Message> pendingMessages;
    private final Node[] nodes;
    private volatile boolean isLeader;

    public Node(int nodeID, Node[] nodes) {
        this.nodeID = nodeID;
        this.nodes = nodes;
        this.nextSeqToDeliver = 0;
        this.nextSeqToAssign = 0;
        this.isLeader = false;
        this.buffer = Collections.synchronizedSet(new HashSet<>());
        this.pendingMessages = new ConcurrentLinkedQueue<>();
    }

    // Called externally to designate this node as leader
    public void electAsLeader() {
        this.isLeader = true;
        this.leader = this;
        for (Node node : nodes) node.leader = this;
    }

    public void sendMessage(Message message) {
        if (isLeader) {
            pendingMessages.add(message);
            broadcastInOrder();
        } else {
            leader.receiveMessage(message);
        }
    }

    public void receiveMessage(Message message) {
        if (isLeader) {
            pendingMessages.add(message);
            broadcastInOrder();
        } else {
            bufferAndDeliver(message);
        }
    }

    private void broadcastInOrder() {
        while (!pendingMessages.isEmpty()) {
            Message msg = pendingMessages.poll();
            nextSeqToAssign++;
            msg.setSequenceID(nextSeqToAssign);
            for (Node node : nodes) {
                if (node.nodeID == this.nodeID) bufferAndDeliver(msg);
                else node.receiveMessage(msg);
            }
        }
    }

    private void bufferAndDeliver(Message message) {
        buffer.add(message);
        boolean progress = true;
        while (progress) {
            progress = false;
            List<Message> toDeliver = new ArrayList<>();
            for (Message msg : buffer) {
                if (msg.getSequenceID() == nextSeqToDeliver + 1) {
                    toDeliver.add(msg);
                }
            }
            for (Message msg : toDeliver) {
                buffer.remove(msg);
                deliverToApplication(msg);
                nextSeqToDeliver = msg.getSequenceID();
                progress = true;
            }
        }
    }

    private void deliverToApplication(Message message) {
        System.out.println("Node " + nodeID + " delivered message " +
                message.getMessageID() + " with seq " + message.getSequenceID() +
                " from node " + message.getSenderID());
    }

    public int getNodeID() { return nodeID; }
    public boolean isLeader() { return isLeader; }
}