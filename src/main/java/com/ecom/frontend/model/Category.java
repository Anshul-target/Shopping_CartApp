package com.ecom.frontend.model;
//import org.bson.types.ObjectId;
public class Category {
    private String id;

    private String name;
    private String imageName;
    private Boolean isActive; // Default value set to true

    // Getter for id
    public String getId() {
        return id;
    }

    // Setter for id
    public void setId(String id) {
        this.id = id;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Setter for name
    public void setName(String name) {
        this.name = name;
    }

    // Getter for imageName
    public String getImageName() {
        return imageName;
    }

    // Setter for imageName
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    // Getter for isActive
    public Boolean getIsActive() {
        return isActive;
    }

    // Setter for isActive
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
