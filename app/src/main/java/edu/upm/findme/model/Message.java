package edu.upm.findme.model;

public class Message {
    int senderId;
    String content;

    public Message(int senderId, String content) {
        this.senderId = senderId;
        this.content = content;
    }

    public int getSenderId() {
        return senderId;
    }

    public String getContent() {
        return content;
    }
}
