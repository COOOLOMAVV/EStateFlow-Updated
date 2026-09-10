package com.EStateFlow;

public class ChatMessage {
    private String id;
    private String sender; // "user", "agent", "client"
    private String text;
    private String time;
    private boolean isFromMe;

    public ChatMessage(String id, String sender, String text, String time, boolean isFromMe) {
        this.id = id;
        this.sender = sender;
        this.text = text;
        this.time = time;
        this.isFromMe = isFromMe;
    }

    public String getId() { return id; }
    public String getSender() { return sender; }
    public String getText() { return text; }
    public String getTime() { return time; }
    public boolean isFromMe() { return isFromMe; }
}
