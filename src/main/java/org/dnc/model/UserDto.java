package org.dnc.model;

public class UserDto {
    private String uuid;
    private String email;
    private String firstName;
    private String lastName;
    private String photo;     // peut être null
    private String role;
    private boolean active;
    private String createdAt; // ISO date string
    private Integer loyaltyPts;

    public UserDto() {}

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public Integer getLoyaltyPts() { return loyaltyPts; }
    public void setLoyaltyPts(Integer loyaltyPts) { this.loyaltyPts = loyaltyPts; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
