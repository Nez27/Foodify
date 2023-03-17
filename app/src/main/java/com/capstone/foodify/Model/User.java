package com.capstone.foodify.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private int id;
    @SerializedName("addressDto")
    private Address address;
    private String dateOfBirth;
    private String email;
    private String fullName;
    private String identifiedCode;
    private String imageUrl;
    private boolean isLocked;
    private String phoneNumber;
    private String roleName;
    private int defaultAddress;
    private List<Address> addresses;

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
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

    public boolean getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(boolean isLocked) {
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

    public List<Address> getAddresses() {
        return addresses;
    }

    public int getDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(int defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }
}
