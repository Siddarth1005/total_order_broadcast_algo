public class Main {

    public static void main(String[] args) {
        System.out.println("=== Test 1: Messages arrive in order ===");
        test1();

        System.out.println("\n=== Test 2: Messages arrive out of order at follower ===");
        test2();

        System.out.println("\n=== Test 3: Multiple followers sending concurrently ===");
        test3();

        System.out.println("\n=== Test 4: All nodes deliver in same order ===");
        test4();
    }

    // A sends m1 then m2 in order — no buffering needed
    static void test1() {
        Node[] nodes = new Node[3];
        for (int i = 0; i < 3; i++) nodes[i] = new Node(i, nodes);
        nodes[0].electAsLeader();

        Node leader = nodes[0];
        Node followerB = nodes[1];

        Message m1 = new Message(0, 0, 1);
        Message m2 = new Message(0, 0, 2);

        System.out.println("Leader sends m1:");
        leader.sendMessage(m1);

        System.out.println("Leader sends m2:");
        leader.sendMessage(m2);

        System.out.println("Expected: all nodes deliver m1(seq=1) then m2(seq=2)");
    }

    // Follower receives seq 3 before seq 2 — should buffer until seq 2 arrives
    static void test2() {
        Node[] nodes = new Node[3];
        for (int i = 0; i < 3; i++) nodes[i] = new Node(i, nodes);
        nodes[0].electAsLeader();

        Node leader = nodes[0];
        Node followerB = nodes[1];

        Message m1 = new Message(0, 0, 1);
        Message m2 = new Message(0, 0, 2);
        Message m3 = new Message(0, 0, 3);

        // Leader sequences all three
        leader.sendMessage(m1);
        leader.sendMessage(m2);
        leader.sendMessage(m3);

        // Simulate out of order delivery to follower B
        // by directly calling receiveMessage in wrong order
        System.out.println("Follower B receives seq 3 first:");
        followerB.receiveMessage(m3);

        System.out.println("Follower B receives seq 2:");
        followerB.receiveMessage(m2);

        System.out.println("Follower B receives seq 1 — should deliver all in order:");
        followerB.receiveMessage(m1);

        System.out.println("Expected: follower B delivers m1(seq=1), m2(seq=2), m3(seq=3)");
    }

    // Multiple followers send concurrently — leader sequences them
    static void test3() {
        Node[] nodes = new Node[4];
        for (int i = 0; i < 4; i++) nodes[i] = new Node(i, nodes);
        nodes[0].electAsLeader();

        Node leader = nodes[0];
        Node followerB = nodes[1];
        Node followerC = nodes[2];
        Node followerD = nodes[3];

        Message m1 = new Message(1, 0, 1); // from follower B
        Message m2 = new Message(2, 0, 2); // from follower C
        Message m3 = new Message(3, 0, 3); // from follower D

        System.out.println("Follower B sends m1:");
        followerB.sendMessage(m1);

        System.out.println("Follower C sends m2:");
        followerC.sendMessage(m2);

        System.out.println("Follower D sends m3:");
        followerD.sendMessage(m3);

        System.out.println("Expected: all nodes deliver m1, m2, m3 in same order");
    }

    // All nodes must deliver in the same order regardless of who sends
    static void test4() {
        Node[] nodes = new Node[3];
        for (int i = 0; i < 3; i++) nodes[i] = new Node(i, nodes);
        nodes[0].electAsLeader();

        Node leader = nodes[0];
        Node followerB = nodes[1];
        Node followerC = nodes[2];

        // B and C send concurrently
        Message m1 = new Message(1, 0, 1); // from B
        Message m2 = new Message(2, 0, 2); // from C

        System.out.println("Follower B and C send concurrently:");
        followerB.sendMessage(m1);
        followerC.sendMessage(m2);

        System.out.println("Expected: all nodes deliver m1(seq=1) then m2(seq=2) in same order");
    }
}