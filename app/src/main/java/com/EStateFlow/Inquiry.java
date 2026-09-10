package com.EStateFlow;

import java.util.List;
import java.util.ArrayList;

public class Inquiry {
    private String id;
    private String propertyId;
    private String propertyTitle;
    private String propertyAddress;
    private String senderName;
    private String senderEmail;
    private String senderPhone;
    private String message;
    private String timeAgo;
    private boolean isUnread;
    private List<ChatMessage> messages;

    public Inquiry(String id, String propertyId, String propertyTitle, String propertyAddress,
                   String senderName, String senderEmail, String senderPhone,
                   String message, String timeAgo, boolean isUnread, List<ChatMessage> messages) {
        this.id = id;
        this.propertyId = propertyId;
        this.propertyTitle = propertyTitle;
        this.propertyAddress = propertyAddress;
        this.senderName = senderName;
        this.senderEmail = senderEmail;
        this.senderPhone = senderPhone;
        this.message = message;
        this.timeAgo = timeAgo;
        this.isUnread = isUnread;
        this.messages = messages != null ? messages : new ArrayList<>();
    }

    public String getId() { return id; }
    public String getPropertyId() { return propertyId; }
    public String getPropertyTitle() { return propertyTitle; }
    public String getPropertyAddress() { return propertyAddress; }
    public String getSenderName() { return senderName; }
    public String getSenderEmail() { return senderEmail; }
    public String getSenderPhone() { return senderPhone; }
    public String getMessage() { return message; }
    public String getTimeAgo() { return timeAgo; }
    public boolean isUnread() { return isUnread; }
    public List<ChatMessage> getMessages() { return messages; }

    public void setUnread(boolean unread) { this.isUnread = unread; }
    public void setMessage(String message) { this.message = message; }
    public void setTimeAgo(String timeAgo) { this.timeAgo = timeAgo; }
    public void setMessages(List<ChatMessage> messages) { this.messages = messages; }

    // Legacy compat: senderName as display initial
    public String getInitial() {
        return senderName != null && !senderName.isEmpty()
                ? String.valueOf(senderName.charAt(0)).toUpperCase()
                : "?";
    }
}
