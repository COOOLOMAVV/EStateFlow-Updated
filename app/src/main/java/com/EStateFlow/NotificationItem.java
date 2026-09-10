package com.EStateFlow;

public class NotificationItem {
    private String id;
    private String title;
    private String message;
    private String timestampFormatted;
    private NotificationType type;
    private boolean isRead;
    private String targetId; // nullable

    public NotificationItem(String id, String title, String message,
                            String timestampFormatted, NotificationType type,
                            boolean isRead, String targetId) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.timestampFormatted = timestampFormatted;
        this.type = type;
        this.isRead = isRead;
        this.targetId = targetId;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getTimestampFormatted() { return timestampFormatted; }
    public NotificationType getType() { return type; }
    public boolean isRead() { return isRead; }
    public String getTargetId() { return targetId; }

    public void setRead(boolean read) { this.isRead = read; }
}
