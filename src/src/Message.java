public class Message {
    int senderID;
    int sequenceID;
    int messageID;

    public Message(int senderID, int sequenceID, int messageID) {
        this.senderID = senderID;
        this.sequenceID = sequenceID;
        this.messageID = messageID;
    }

    public int getSenderID() {
        return senderID;
    }

    public int getSequenceID() {
        return sequenceID;
    }

    public int getMessageID() {
        return messageID;
    }

    public void setSenderID(int senderID) {
        this.senderID = senderID;
    }

    public void setSequenceID(int sequenceID) {
        this.sequenceID = sequenceID;
    }

    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }
}
