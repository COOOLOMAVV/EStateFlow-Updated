package com.EStateFlow;

public class AgentProfile {
    private String name;
    private String title;
    private String licenseNo;
    private String phone;
    private String email;
    private double rating;
    private int reviewsCount;
    private String totalSalesVolume;
    private int activeListingsCount;
    private String bio;

    public AgentProfile() {
        this.name = "Sarah Jenkins";
        this.title = "Premier Real Estate Agent • Team Vancouver";
        this.licenseNo = "RE-94821-BC";
        this.phone = "+1 (604) 555-0192";
        this.email = "sarah.jenkins@estateflow.com";
        this.rating = 4.9;
        this.reviewsCount = 124;
        this.totalSalesVolume = "$48.2M";
        this.activeListingsCount = 12;
        this.bio = "Specializing in premium residential townhomes, modern urban lofts, and luxury family estates across Greater Vancouver and the Pacific Northwest.";
    }

    public String getName() { return name; }
    public String getTitle() { return title; }
    public String getLicenseNo() { return licenseNo; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public double getRating() { return rating; }
    public int getReviewsCount() { return reviewsCount; }
    public String getTotalSalesVolume() { return totalSalesVolume; }
    public int getActiveListingsCount() { return activeListingsCount; }
    public String getBio() { return bio; }

    public void setActiveListingsCount(int count) { this.activeListingsCount = count; }
}
