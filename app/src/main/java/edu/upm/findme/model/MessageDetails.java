package edu.upm.findme.model;

public class MessageDetails extends Message {

    String senderName = "";

    public MessageDetails(int senderId, String content) {
        super(senderId, content);
    }

    public MessageDetails(Message msg) {
        super(msg.getSenderId(), msg.getContent());
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}
