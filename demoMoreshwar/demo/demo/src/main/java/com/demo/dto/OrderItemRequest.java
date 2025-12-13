package com.demo.dto;

public class OrderItemRequest {

    private Long itemId;
    private Integer quantity;

    // --- Getters and Setters ---
    // Spring Boot needs these to read the JSON

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}

