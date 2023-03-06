package com.capstone.foodify.Model.Address;

public class Address {
    private int id;
    private String address;
    private String ward;
    private String district;

    public Address(int id, String address, String ward, String district) {
        this.id = id;
        this.address = address;
        this.ward = ward;
        this.district = district;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}
