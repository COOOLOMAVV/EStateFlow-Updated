package com.EStateFlow;

import java.util.Arrays;
import java.util.List;

public class Property {
    private String id;
    private String title;
    private String location;      // address line (e.g. "439 East 14th Ave, Mount Pleasant")
    private String cityStateZip;  // e.g. "Vancouver, BC • V5T 2N1"
    private String price;         // formatted price string
    private double priceNumeric;  // numeric value for filtering
    private boolean isRental;
    private String tagText;       // e.g. "Townhouse • For Sale"
    private int beds;
    private String baths;
    private int sqft;
    private double rating;
    private String category;
    private int imageResId;
    private boolean isFavorite;
    private String description;
    private String status;        // "Active", "New Listing", etc.
    private List<String> availableDates;
    private List<String> availableTimeSlots;
    private List<String> amenities;
    private int yearBuilt;
    private String agentName;
    private String agentTitle;
    private String agentPhone;
    private String agentEmail;

    // Full constructor (matches ES Old model fields)
    public Property(String id, String title, String location, String cityStateZip,
                    String price, double priceNumeric, boolean isRental,
                    String tagText, int beds, String baths, int sqft, double rating,
                    String category, int imageResId, boolean isFavorite, String description,
                    String status, List<String> availableDates, List<String> availableTimeSlots,
                    List<String> amenities, int yearBuilt,
                    String agentName, String agentTitle, String agentPhone, String agentEmail) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.cityStateZip = cityStateZip;
        this.price = price;
        this.priceNumeric = priceNumeric;
        this.isRental = isRental;
        this.tagText = tagText;
        this.beds = beds;
        this.baths = baths;
        this.sqft = sqft;
        this.rating = rating;
        this.category = category;
        this.imageResId = imageResId;
        this.isFavorite = isFavorite;
        this.description = description;
        this.status = status != null ? status : "Active";
        this.availableDates = availableDates != null ? availableDates
                : Arrays.asList("Mon, Oct 14", "Tue, Oct 15", "Wed, Oct 16", "Thu, Oct 17");
        this.availableTimeSlots = availableTimeSlots != null ? availableTimeSlots
                : Arrays.asList("10:00 AM", "1:30 PM", "4:00 PM", "5:30 PM");
        this.amenities = amenities != null ? amenities
                : Arrays.asList("Modern Design", "High Speed Internet", "Near Transit");
        this.yearBuilt = yearBuilt > 0 ? yearBuilt : 2021;
        this.agentName = agentName != null ? agentName : "Sarah Jenkins";
        this.agentTitle = agentTitle != null ? agentTitle : "Premier Real Estate Agent • Team Vancouver";
        this.agentPhone = agentPhone != null ? agentPhone : "+1 (604) 555-0192";
        this.agentEmail = agentEmail != null ? agentEmail : "sarah.jenkins@estateflow.com";
    }

    // Legacy compact constructor — keeps existing MainActivity.java working
    public Property(String id, String title, String location, String price, double priceNumeric,
                    String tagText, int beds, String baths, int sqft, double rating,
                    String category, int imageResId, boolean isFavorite, String description) {
        this(id, title, location, "", price, priceNumeric, false,
                tagText, beds, baths, sqft, rating, category, imageResId, isFavorite,
                description, "Active", null, null, null, 2021,
                null, null, null, null);
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getCityStateZip() { return cityStateZip; }
    public String getPrice() { return price; }
    public double getPriceNumeric() { return priceNumeric; }
    public boolean isRental() { return isRental; }
    public String getTagText() { return tagText; }
    public int getBeds() { return beds; }
    public String getBaths() { return baths; }
    public int getSqft() { return sqft; }
    public double getRating() { return rating; }
    public String getCategory() { return category; }
    public int getImageResId() { return imageResId; }
    public boolean isFavorite() { return isFavorite; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public List<String> getAvailableDates() { return availableDates; }
    public List<String> getAvailableTimeSlots() { return availableTimeSlots; }
    public List<String> getAmenities() { return amenities; }
    public int getYearBuilt() { return yearBuilt; }
    public String getAgentName() { return agentName; }
    public String getAgentTitle() { return agentTitle; }
    public String getAgentPhone() { return agentPhone; }
    public String getAgentEmail() { return agentEmail; }

    // Setters
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    public void setStatus(String status) { this.status = status; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
}
