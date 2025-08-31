package org.dnc.model;

public class LoyaltyDto {
    private String lastName;
    private String firstName;
    private String createdAt;
    private Integer points;
    private String avatar;

    public LoyaltyDto() {}

    public String getLastName()  { return lastName; }
    public void setLastName(String v) { this.lastName = v; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String v) { this.firstName = v; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String v) { this.createdAt = v; }

    public Integer getPoints()   { return points; }
    public void setPoints(Integer v) { this.points = v; }

    public String getAvatar()    { return avatar; }
    public void setAvatar(String v) { this.avatar = v; }
}
