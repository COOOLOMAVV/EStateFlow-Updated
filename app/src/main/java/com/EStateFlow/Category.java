package com.EStateFlow;

public class Category {
    private String id;
    private String name;
    private int iconResId;
    private boolean isSelected;

    public Category(String id, String name, int iconResId, boolean isSelected) {
        this.id = id;
        this.name = name;
        this.iconResId = iconResId;
        this.isSelected = isSelected;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getIconResId() { return iconResId; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}
