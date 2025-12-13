package com.demo.dto;

import java.util.List;

/**
 * This class is a Data Transfer Object (DTO).
 * It perfectly matches the JSON structure our JavaScript will send.
 * Spring Boot will automatically convert the JSON into this Java object.
 */
public class OrderRequest {

    // --- Form Fields ---
    private String staffId;
    private String tableNo;
    private String customerName;
    private String customerContact;

    // --- Cart Items ---
    // This is a list of the class we created in the previous step
    private List<OrderItemRequest> items;

    
    // --- Getters and Setters ---
    // Spring Boot needs all of these to create the object from the JSON

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getTableNo() {
        return tableNo;
    }

    public void setTableNo(String tableNo) {
        this.tableNo = tableNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerContact() {
        return customerContact;
    }

    public void setCustomerContact(String customerContact) {
        this.customerContact = customerContact;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
}
