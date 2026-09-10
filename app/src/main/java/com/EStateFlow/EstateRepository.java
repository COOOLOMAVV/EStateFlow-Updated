package com.EStateFlow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * EstateRepository — ported from ES Old's EstateRepository.kt
 *
 * Holds all mutable data lists and provides functions for:
 *   - toggleFavorite
 *   - bookAppointment
 *   - updateAppointmentStatus
 *   - sendInquiry
 *   - replyToInquiry
 *   - markNotificationAsRead / markAllNotificationsAsRead
 *   - addProperty
 *   - Initial data seeding (properties, appointments, inquiries, notifications)
 */
public class EstateRepository {

    private final List<Property> properties;
    private final List<ViewingAppointment> appointments;
    private final List<Inquiry> inquiries;
    private final List<NotificationItem> notifications;
    private final AgentProfile agentProfile;

    public EstateRepository() {
        properties = new ArrayList<>(getInitialProperties());
        appointments = new ArrayList<>(getInitialAppointments());
        inquiries = new ArrayList<>(getInitialInquiries());
        notifications = new ArrayList<>(getInitialNotifications());
        agentProfile = new AgentProfile();
    }

    // ─── Accessors ───────────────────────────────────────────────────────────────

    public List<Property> getProperties() { return properties; }
    public List<ViewingAppointment> getAppointments() { return appointments; }
    public List<Inquiry> getInquiries() { return inquiries; }
    public List<NotificationItem> getNotifications() { return notifications; }
    public AgentProfile getAgentProfile() { return agentProfile; }

    // ─── Property functions ───────────────────────────────────────────────────────

    /** Toggles the isFavorite flag for the property matching propertyId. */
    public void toggleFavorite(String propertyId) {
        for (Property p : properties) {
            if (p.getId().equals(propertyId)) {
                p.setFavorite(!p.isFavorite());
                break;
            }
        }
    }

    /**
     * addProperty — ported from EstateRepository.kt addProperty()
     * Creates a new Property and prepends it to the list, also adds a notification.
     */
    public Property addProperty(String title, String address, String cityStateZip,
                                double price, boolean isRental,
                                double beds, String baths, int sqft,
                                String propertyType, String description,
                                List<String> amenities) {
        String formattedPrice = isRental
                ? String.format("$%,.0f / mo", price)
                : String.format("$%,.0f", price);

        int imageResId;
        switch (propertyType.toLowerCase()) {
            case "townhouse": imageResId = R.drawable.property_townhouse_1787131686098; break;
            case "apartment":
            case "condo":    imageResId = R.drawable.property_skyline_1787131724474; break;
            case "loft":     imageResId = R.drawable.property_urban_loft_1787131739575; break;
            case "house":    imageResId = R.drawable.property_pinecrest_1787131698724; break;
            default:         imageResId = R.drawable.property_sunny_meadows_1787131753558; break;
        }

        List<String> finalAmenities = (amenities == null || amenities.isEmpty())
                ? Arrays.asList("Modern Design", "High Speed Internet", "Near Transit")
                : amenities;

        String tagText = propertyType + (isRental ? " • For Rent" : " • For Sale");

        Property newProperty = new Property(
                UUID.randomUUID().toString(), title, address, cityStateZip,
                formattedPrice, price, isRental,
                tagText, (int) beds, baths, sqft, 4.8,
                propertyType, imageResId, false, description,
                "New Listing", null, null, finalAmenities, 2023,
                null, null, null, null
        );

        properties.add(0, newProperty);

        // Add listing notification
        notifications.add(0, new NotificationItem(
                UUID.randomUUID().toString(),
                "New Property Published",
                newProperty.getTitle() + " is now live on Estateflow with active viewing bookings.",
                "Just now",
                NotificationType.LISTING_UPDATE,
                false,
                newProperty.getId()
        ));

        return newProperty;
    }

    // ─── Appointment functions ────────────────────────────────────────────────────

    /**
     * bookAppointment — ported from EstateRepository.kt bookAppointment()
     * Creates a new ViewingAppointment, prepends it, and fires a notification.
     */
    public ViewingAppointment bookAppointment(String propertyId, String date, String timeSlot,
                                              String clientName, String clientPhone,
                                              String clientEmail, String notes) {
        Property property = findPropertyById(propertyId);

        String resolvedName  = (clientName  != null && !clientName.trim().isEmpty())  ? clientName  : "You (Verified Buyer)";
        String resolvedPhone = (clientPhone != null && !clientPhone.trim().isEmpty()) ? clientPhone : "+1 (555) 019-8234";
        String resolvedEmail = (clientEmail != null && !clientEmail.trim().isEmpty()) ? clientEmail : "buyer@estateflow.com";
        String resolvedNotes = (notes       != null && !notes.trim().isEmpty())       ? notes       : "Scheduled via Estateflow instant booking.";

        ViewingAppointment newAppt = new ViewingAppointment(
                UUID.randomUUID().toString(),
                propertyId,
                property != null ? property.getTitle() : "Property Viewing",
                property != null ? property.getLocation() : "Address",
                resolvedName, resolvedPhone, resolvedEmail,
                date, timeSlot, "Viewing",
                AppointmentStatus.UPCOMING, resolvedNotes,
                System.currentTimeMillis()
        );

        appointments.add(0, newAppt);

        // Booking notification
        notifications.add(0, new NotificationItem(
                UUID.randomUUID().toString(),
                "Viewing Appointment Booked",
                "Viewing for " + newAppt.getPropertyTitle() + " confirmed on " + date + " at " + timeSlot + ".",
                "Just now",
                NotificationType.VIEWING_BOOKED,
                false,
                newAppt.getId()
        ));

        return newAppt;
    }

    /**
     * updateAppointmentStatus — ported from EstateRepository.kt updateAppointmentStatus()
     */
    public void updateAppointmentStatus(String appointmentId, AppointmentStatus newStatus) {
        for (ViewingAppointment a : appointments) {
            if (a.getId().equals(appointmentId)) {
                a.setStatus(newStatus);
                break;
            }
        }
    }

    // ─── Inquiry / Chat functions ─────────────────────────────────────────────────

    /**
     * sendInquiry — ported from EstateRepository.kt sendInquiry()
     * If an inquiry for the same property already exists, appends a message.
     * Otherwise creates a new Inquiry and fires a notification.
     */
    public Inquiry sendInquiry(String propertyId, String messageText,
                               String senderName, String senderEmail, String senderPhone) {
        Property property = findPropertyById(propertyId);

        // Look for existing inquiry for this property from this sender
        Inquiry existing = null;
        for (Inquiry inq : inquiries) {
            if (inq.getPropertyId().equals(propertyId) && inq.getSenderName().equals(senderName)) {
                existing = inq;
                break;
            }
        }

        if (existing != null) {
            List<ChatMessage> updatedMessages = new ArrayList<>(existing.getMessages());
            updatedMessages.add(new ChatMessage(
                    UUID.randomUUID().toString(), "user", messageText, "Just now", true
            ));
            existing.setMessage(messageText);
            existing.setTimeAgo("Just now");
            existing.setMessages(updatedMessages);
            return existing;
        } else {
            List<ChatMessage> initialMessages = new ArrayList<>();
            initialMessages.add(new ChatMessage(
                    UUID.randomUUID().toString(), "user", messageText, "Just now", true
            ));

            Inquiry newInquiry = new Inquiry(
                    UUID.randomUUID().toString(),
                    propertyId,
                    property != null ? property.getTitle() : "Property Inquiry",
                    property != null ? property.getLocation() : "",
                    senderName, senderEmail, senderPhone,
                    messageText, "Just now", true, initialMessages
            );
            inquiries.add(0, newInquiry);

            // Inquiry notification
            notifications.add(0, new NotificationItem(
                    UUID.randomUUID().toString(),
                    "Inquiry Sent to " + (property != null ? property.getAgentName() : "Agent"),
                    "Your inquiry regarding " + (property != null ? property.getTitle() : "property") + " has been delivered.",
                    "Just now",
                    NotificationType.NEW_INQUIRY,
                    false,
                    newInquiry.getId()
            ));

            return newInquiry;
        }
    }

    /** Convenience overload using default sender info (buyer). */
    public Inquiry sendInquiry(String propertyId, String messageText) {
        return sendInquiry(propertyId, messageText,
                "You (Prospective Buyer)", "buyer@estateflow.com", "+1 (555) 019-8234");
    }

    /**
     * replyToInquiry — ported from EstateRepository.kt replyToInquiry()
     * Appends a ChatMessage to the specified inquiry.
     */
    public void replyToInquiry(String inquiryId, String replyText, boolean isAgent) {
        for (Inquiry inq : inquiries) {
            if (inq.getId().equals(inquiryId)) {
                List<ChatMessage> updatedMessages = new ArrayList<>(inq.getMessages());
                updatedMessages.add(new ChatMessage(
                        UUID.randomUUID().toString(),
                        isAgent ? "agent" : "user",
                        replyText, "Just now", isAgent
                ));
                inq.setUnread(false);
                inq.setMessage(replyText);
                inq.setTimeAgo("Just now");
                inq.setMessages(updatedMessages);
                break;
            }
        }
    }

    // ─── Notification functions ───────────────────────────────────────────────────

    /** markNotificationAsRead — ported from EstateRepository.kt */
    public void markNotificationAsRead(String id) {
        for (NotificationItem n : notifications) {
            if (n.getId().equals(id)) {
                n.setRead(true);
                break;
            }
        }
    }

    /** markAllNotificationsAsRead — ported from EstateRepository.kt */
    public void markAllNotificationsAsRead() {
        for (NotificationItem n : notifications) {
            n.setRead(true);
        }
    }

    /** Returns count of unread notifications. */
    public int getUnreadNotificationsCount() {
        int count = 0;
        for (NotificationItem n : notifications) {
            if (!n.isRead()) count++;
        }
        return count;
    }

    /** Returns count of saved (favorite) properties. */
    public int getSavedCount() {
        int count = 0;
        for (Property p : properties) {
            if (p.isFavorite()) count++;
        }
        return count;
    }

    /** Returns list of favorite properties. */
    public List<Property> getSavedProperties() {
        List<Property> saved = new ArrayList<>();
        for (Property p : properties) {
            if (p.isFavorite()) saved.add(p);
        }
        return saved;
    }

    // ─── Helper ──────────────────────────────────────────────────────────────────

    private Property findPropertyById(String propertyId) {
        for (Property p : properties) {
            if (p.getId().equals(propertyId)) return p;
        }
        return null;
    }

    public Inquiry findInquiryById(String inquiryId) {
        for (Inquiry inq : inquiries) {
            if (inq.getId().equals(inquiryId)) return inq;
        }
        return null;
    }

    // ─── Seed Data ────────────────────────────────────────────────────────────────

    /** getInitialProperties — ported from EstateRepository.kt */
    private List<Property> getInitialProperties() {
        return Arrays.asList(
                new Property(
                        "prop_townhouse", "Cozy Family Townhouse",
                        "439 East 14th Ave, Mount Pleasant", "Vancouver, BC • V5T 2N1",
                        "$1,249,000", 1249000, false,
                        "Townhouse • For Sale", 3, "2.5", 1540, 4.9,
                        "Townhouses", R.drawable.property_townhouse_1787131686098,
                        true,
                        "Beautifully updated Mount Pleasant townhouse offering modern interiors, open-plan living, private rooftop deck, and direct garage access. Steps away from parks, schools, and transit.",
                        "Active",
                        Arrays.asList("Mon, Oct 14", "Tue, Oct 15", "Wed, Oct 16", "Thu, Oct 17"),
                        Arrays.asList("10:00 AM", "1:30 PM", "4:00 PM", "5:30 PM"),
                        Arrays.asList("Private Rooftop Deck", "Direct Garage Access", "Open-Plan Living", "Central A/C", "Pet Friendly", "Parks & Transit"),
                        2020, "Sarah Jenkins", "Premier Real Estate Agent • Team Vancouver",
                        "+1 (604) 555-0192", "sarah.jenkins@estateflow.com"
                ),
                new Property(
                        "prop_pinecrest", "The Pinecrest Manor",
                        "1208 Pinecrest Way, Seattle", "Seattle, WA • 98101",
                        "$1,250,000", 1250000, false,
                        "Houses • For Sale", 4, "3", 2400, 4.7,
                        "Houses", R.drawable.property_pinecrest_1787131698724,
                        false,
                        "Stately contemporary manor featuring expansive landscaped grounds, high ceilings, custom chef's kitchen, master suite retreat, and seamless indoor-outdoor entertaining spaces.",
                        "Active",
                        Arrays.asList("Mon, Oct 14", "Tue, Oct 15", "Thu, Oct 17", "Sat, Oct 19"),
                        Arrays.asList("9:30 AM", "11:00 AM", "2:00 PM", "4:30 PM"),
                        Arrays.asList("Chef's Gourmet Kitchen", "Sprawling Backyard", "2-Car Attached Garage", "Smart Home Automation", "Energy Efficient"),
                        2018, "Sarah Jenkins", "Premier Real Estate Agent • Team Vancouver",
                        "+1 (604) 555-0192", "sarah.jenkins@estateflow.com"
                ),
                new Property(
                        "prop_skyline", "Skyline Vista Apartment",
                        "Apt 402, 45 Broadway, New York", "New York, NY • 10006",
                        "$3,400 / mo", 3400, true,
                        "Apartments • For Rent", 2, "2", 1150, 4.8,
                        "Apartments", R.drawable.property_skyline_1787131724474,
                        true,
                        "Luxury high-rise apartment featuring floor-to-ceiling panoramic skyline windows, hardwood floors, marble countertops, 24/7 concierge, gym, and rooftop lounge.",
                        "Active",
                        Arrays.asList("Tue, Oct 15", "Wed, Oct 16", "Fri, Oct 18", "Sun, Oct 20"),
                        Arrays.asList("10:00 AM", "12:30 PM", "3:00 PM", "6:00 PM"),
                        Arrays.asList("Panoramic Views", "24/7 Concierge", "Fitness Center", "In-Unit Washer/Dryer", "Rooftop Pool & Lounge"),
                        2019, "Sarah Jenkins", "Premier Real Estate Agent • Team Vancouver",
                        "+1 (604) 555-0192", "sarah.jenkins@estateflow.com"
                ),
                new Property(
                        "prop_urban_loft", "Urban Loft & Studio",
                        "88 Industrial Blvd, Portland", "Portland, OR • 97209",
                        "$620,000", 620000, false,
                        "Condos • For Sale", 1, "1.5", 950, 4.6,
                        "Condos", R.drawable.property_urban_loft_1787131739575,
                        false,
                        "Authentic timber and brick architectural loft in the heart of the Pearl District. Soaring 14-foot ceilings, oversized industrial windows, and bespoke modern fixtures.",
                        "Active",
                        Arrays.asList("Mon, Oct 14", "Wed, Oct 16", "Thu, Oct 17", "Sat, Oct 19"),
                        Arrays.asList("11:00 AM", "1:30 PM", "3:30 PM", "5:00 PM"),
                        Arrays.asList("Exposed Brickwork", "14ft High Ceilings", "Secure Underground Parking", "Bike Storage", "Coffee Bar On-Site"),
                        2017, "Sarah Jenkins", "Premier Real Estate Agent • Team Vancouver",
                        "+1 (604) 555-0192", "sarah.jenkins@estateflow.com"
                ),
                new Property(
                        "prop_sunny_meadows", "Sunny Meadows Estate",
                        "554 Grassland Dr, Austin", "Austin, TX • 78704",
                        "$890,000", 890000, false,
                        "Houses • For Sale", 3, "2.5", 1850, 4.5,
                        "Houses", R.drawable.property_sunny_meadows_1787131753558,
                        false,
                        "Sun-drenched suburban oasis with pristine green gardens, covered patio pergola, modern open layout, solar panels, and proximity to top-ranked school districts.",
                        "Active",
                        Arrays.asList("Tue, Oct 15", "Wed, Oct 16", "Fri, Oct 18", "Sat, Oct 19"),
                        Arrays.asList("10:00 AM", "1:00 PM", "3:00 PM", "5:00 PM"),
                        Arrays.asList("Solar Panel System", "Covered Pergola Patio", "Lush Private Yard", "EV Charging Station", "Bonus Flex Room"),
                        2022, "Sarah Jenkins", "Premier Real Estate Agent • Team Vancouver",
                        "+1 (604) 555-0192", "sarah.jenkins@estateflow.com"
                )
        );
    }

    /** getInitialAppointments — ported from EstateRepository.kt */
    private List<ViewingAppointment> getInitialAppointments() {
        return Arrays.asList(
                new ViewingAppointment(
                        "appt_1", "prop_townhouse",
                        "439 East 14th Ave - Viewing",
                        "439 East 14th Ave, Mount Pleasant",
                        "John Doe", "+1 (604) 772-9102", "john.doe@gmail.com",
                        "Mon, Oct 14", "1:30 PM", "Viewing",
                        AppointmentStatus.UPCOMING,
                        "Interested in rooftop deck access and parking dimensions.",
                        System.currentTimeMillis()
                ),
                new ViewingAppointment(
                        "appt_2", "prop_urban_loft",
                        "1088 Richards St - Walkthrough",
                        "1088 Richards St, Yaletown",
                        "Clara Webb", "+1 (604) 381-4490", "clara.webb@outlook.com",
                        "Tue, Oct 15", "10:00 AM", "Walkthrough",
                        AppointmentStatus.UPCOMING,
                        "Bringing interior designer for measurement assessment.",
                        System.currentTimeMillis()
                ),
                new ViewingAppointment(
                        "appt_3", "prop_pinecrest",
                        "1208 Pinecrest Way - Private Tour",
                        "1208 Pinecrest Way, Seattle",
                        "Marcus Aurelius", "+1 (206) 555-8819", "marcus.aurelius@stoic.org",
                        "Wed, Oct 16", "3:00 PM", "Viewing",
                        AppointmentStatus.UPCOMING,
                        "Looking for multi-generational family home.",
                        System.currentTimeMillis()
                ),
                new ViewingAppointment(
                        "appt_4", "prop_sunny_meadows",
                        "554 Grassland Dr - Inspection",
                        "554 Grassland Dr, Austin",
                        "Diana Prince", "+1 (512) 555-9012", "diana.prince@themiscira.org",
                        "Thu, Oct 17", "11:30 AM", "Inspection",
                        AppointmentStatus.UPCOMING,
                        "Home inspection contingency verification.",
                        System.currentTimeMillis()
                )
        );
    }

    /** getInitialInquiries — ported from EstateRepository.kt */
    private List<Inquiry> getInitialInquiries() {
        return Arrays.asList(
                new Inquiry(
                        "inq_marcus", "prop_townhouse",
                        "439 East 14th Ave, Mount Pleasant",
                        "439 East 14th Ave, Mount Pleasant",
                        "Marcus Aurelius", "marcus.aurelius@stoic.org", "+1 (206) 555-8819",
                        "Interested in 439 East 14th Ave • 2 hours ago",
                        "2 hours ago", true,
                        Arrays.asList(
                                new ChatMessage("msg_m1", "client",
                                        "Hi Sarah, I noticed the townhouse in Mount Pleasant has a rooftop deck. Is it private to this unit or shared among the complex?",
                                        "2:15 PM", false),
                                new ChatMessage("msg_m2", "agent",
                                        "Hello Marcus! It is 100% private to unit 439, deeded exclusively with direct internal stair access. Would you like to schedule an in-person viewing?",
                                        "2:25 PM", true),
                                new ChatMessage("msg_m3", "client",
                                        "That sounds perfect! I'd love to see it this week if possible.",
                                        "2:30 PM", false)
                        )
                ),
                new Inquiry(
                        "inq_diana", "prop_urban_loft",
                        "902 Beatty St / 88 Industrial",
                        "902 Beatty St, Vancouver",
                        "Diana Prince", "diana.prince@themiscira.org", "+1 (512) 555-9012",
                        "Requested details for 902 Beatty St • Yesterday",
                        "Yesterday", false,
                        Arrays.asList(
                                new ChatMessage("msg_d1", "client",
                                        "Good afternoon Sarah, could you provide the strata council bylaws regarding EV charging station installation?",
                                        "Yesterday 4:40 PM", false),
                                new ChatMessage("msg_d2", "agent",
                                        "Hi Diana! The building is EV-ready and already approved Level 2 charger retrofits for all stalls. I can email you the full strata minutes package.",
                                        "Yesterday 5:05 PM", true)
                        )
                ),
                new Inquiry(
                        "inq_john", "prop_townhouse",
                        "439 East 14th Ave, Mount Pleasant",
                        "439 East 14th Ave, Mount Pleasant",
                        "John Doe", "john.doe@gmail.com", "+1 (604) 772-9102",
                        "Confirmed viewing for Monday at 1:30 PM",
                        "2 days ago", false,
                        Arrays.asList(
                                new ChatMessage("msg_j1", "client",
                                        "Hi Sarah, looking forward to viewing the townhouse on Monday at 1:30 PM. Where should we meet?",
                                        "Oct 12 11:00 AM", false),
                                new ChatMessage("msg_j2", "agent",
                                        "Hi John! We will meet right by the front entrance garden gate on 14th Ave. See you there!",
                                        "Oct 12 11:15 AM", true)
                        )
                )
        );
    }

    /** getInitialNotifications — ported from EstateRepository.kt */
    private List<NotificationItem> getInitialNotifications() {
        return Arrays.asList(
                new NotificationItem("notif_1", "Viewing Scheduled",
                        "John Doe booked a viewing for 439 East 14th Ave on Mon, Oct 14 at 1:30 PM.",
                        "10 mins ago", NotificationType.VIEWING_BOOKED, false, "appt_1"),
                new NotificationItem("notif_2", "New Lead Inquiry",
                        "Marcus Aurelius sent an inquiry regarding Cozy Family Townhouse.",
                        "2 hours ago", NotificationType.NEW_INQUIRY, false, "inq_marcus"),
                new NotificationItem("notif_3", "Price Update",
                        "Skyline Vista Apartment rental terms updated to $3,400 / mo.",
                        "Yesterday", NotificationType.PRICE_DROP, true, "prop_skyline"),
                new NotificationItem("notif_4", "New Property Published",
                        "The Pinecrest Manor was successfully verified and listed on Estateflow.",
                        "3 days ago", NotificationType.LISTING_UPDATE, true, "prop_pinecrest")
        );
    }
}
