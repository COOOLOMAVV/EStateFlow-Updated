package com.EStateFlow;

public class Conversation {
    private String id;
    private String initial;
    private String senderName;
    private String timestamp;
    private String subject;
    private String snippet;
    private boolean isUnread;

    public Conversation(String id, String initial, String senderName, String timestamp, String subject, String snippet, boolean isUnread) {
        this.id = id;
        this.initial = initial;
        this.senderName = senderName;
        this.timestamp = timestamp;
        this.subject = subject;
        this.snippet = snippet;
        this.isUnread = isUnread;
    }

    public String getId() { return id; }
    public String getInitial() { return initial; }
    public String getSenderName() { return senderName; }
    public String getTimestamp() { return timestamp; }
    public String getSubject() { return subject; }
    public String getSnippet() { return snippet; }
    public boolean isUnread() { return isUnread; }
    public void setUnread(boolean unread) { isUnread = unread; }
}
