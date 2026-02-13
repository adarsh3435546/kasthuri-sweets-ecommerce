package com.kasthurisweets.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {

    private Long orderId;
    private String customerName;
    private String phoneNumber;
    private String address;
    private String city;
    private String pincode;

    private LocalDateTime orderDate;
    private String status;
    private double totalAmount;

    private List<OrderItemResponse> items;

    public OrderResponse(Long orderId,
                         String customerName,
                         String phoneNumber,
                         String address,
                         String city,
                         String pincode,
                         LocalDateTime orderDate,
                         String status,
                         double totalAmount,
                         List<OrderItemResponse> items) {

        this.orderId = orderId;
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.city = city;
        this.pincode = pincode;
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = totalAmount;
        this.items = items;
    }

    public Long getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getPincode() { return pincode; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public String getStatus() { return status; }
    public double getTotalAmount() { return totalAmount; }
    public List<OrderItemResponse> getItems() { return items; }
}
