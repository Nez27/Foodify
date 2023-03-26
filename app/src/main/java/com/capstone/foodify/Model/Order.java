package com.capstone.foodify.Model;

import java.util.List;

public class Order {
    private int id;
    private String orderTrackingNumber;
    private int shipperId;
    private String paymentMethod;
    private float shippingCost;
    private String status;
    private String address;
    private List<OrderDetail> orderDetails;

    public Order(String orderTrackingNumber, int shipperId, String paymentMethod, float shippingCost, String status, String address, List<OrderDetail> orderDetails) {
        this.orderTrackingNumber = orderTrackingNumber;
        this.shipperId = shipperId;
        this.paymentMethod = paymentMethod;
        this.shippingCost = shippingCost;
        this.status = status;
        this.address = address;
        this.orderDetails = orderDetails;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Order(String orderTrackingNumber) {
        this.orderTrackingNumber = orderTrackingNumber;
    }
    public String getOrderTrackingNumber() {
        return orderTrackingNumber;
    }

    public void setOrderTrackingNumber(String orderTrackingNumber) {
        this.orderTrackingNumber = orderTrackingNumber;
    }

    public int getShipperId() {
        return shipperId;
    }

    public void setShipperId(int shipperId) {
        this.shipperId = shipperId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public float getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(float shippingCost) {
        this.shippingCost = shippingCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public List<OrderDetail> getListOrderDetail() {
        return orderDetails;
    }

    public void setListOrderDetail(List<OrderDetail> listOrderDetail) {
        this.orderDetails = listOrderDetail;
    }
}
