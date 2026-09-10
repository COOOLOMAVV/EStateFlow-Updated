package com.EStateFlow;

public class ViewingAppointment {
    private String id;
    private String propertyId;
    private String propertyTitle;
    private String propertyAddress;
    private String clientName;
    private String clientPhone;
    private String clientEmail;
    private String date;
    private String timeSlot;
    private String type;
    private AppointmentStatus status;
    private String notes;
    private long timestamp;

    public ViewingAppointment(String id, String propertyId, String propertyTitle,
                              String propertyAddress, String clientName, String clientPhone,
                              String clientEmail, String date, String timeSlot,
                              String type, AppointmentStatus status, String notes, long timestamp) {
        this.id = id;
        this.propertyId = propertyId;
        this.propertyTitle = propertyTitle;
        this.propertyAddress = propertyAddress;
        this.clientName = clientName;
        this.clientPhone = clientPhone;
        this.clientEmail = clientEmail;
        this.date = date;
        this.timeSlot = timeSlot;
        this.type = type;
        this.status = status;
        this.notes = notes;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public String getPropertyId() { return propertyId; }
    public String getPropertyTitle() { return propertyTitle; }
    public String getPropertyAddress() { return propertyAddress; }
    public String getClientName() { return clientName; }
    public String getClientPhone() { return clientPhone; }
    public String getClientEmail() { return clientEmail; }
    public String getDate() { return date; }
    public String getTimeSlot() { return timeSlot; }
    public String getType() { return type; }
    public AppointmentStatus getStatus() { return status; }
    public String getNotes() { return notes; }
    public long getTimestamp() { return timestamp; }

    public void setStatus(AppointmentStatus status) { this.status = status; }
}
