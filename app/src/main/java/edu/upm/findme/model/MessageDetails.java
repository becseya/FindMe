package edu.upm.findme.model;

public class MessageDetails extends Message {

    String senderName = "";
    boolean sentByThemself;

    public MessageDetails(int senderId, String content) {
        super(senderId, content);
    }

    public MessageDetails(Message msg, int userId) {
        super(msg.getSenderId(), msg.getContent());
        this.sentByThemself = (userId == senderId);
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public boolean isSentByThemself() {
        return sentByThemself;
    }
}
