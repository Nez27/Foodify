package com.capstone.foodify.Model;

public class User {
    private String dateOfBirth;
    private String email;
    private String fullName;
    private String identifiedCode;
    private String imageUrl;
    private String isLocked;
    private String phoneNumber;
    private String roleName;

    public User(String dateOfBirth, String email, String fullName, String identifiedCode, String imageUrl, String isLocked, String phoneNumber, String roleName) {
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.fullName = fullName;
        this.identifiedCode = identifiedCode;
        this.imageUrl = imageUrl;
        this.isLocked = isLocked;
        this.phoneNumber = phoneNumber;
        this.roleName = roleName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getIdentifiedCode() {
        return identifiedCode;
    }

    public void setIdentifiedCode(String identifiedCode) {
        this.identifiedCode = identifiedCode;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(String isLocked) {
        this.isLocked = isLocked;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
