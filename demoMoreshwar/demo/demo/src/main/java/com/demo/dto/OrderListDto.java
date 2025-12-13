package com.demo.dto;

import com.demo.model.Order;
import java.math.BigDecimal;

/**
 * A DTO (Data Transfer Object) that represents one row
 * in your "On Process" or "Complete" list.
 */
public class OrderListDto {

    private Long orderId;
    
    // --- THIS IS THE FIX ---
    // Changed from String to Integer to match your Order.java entity
    private Integer tableNo;
    // --- END OF FIX ---

    private BigDecimal totalPrice;
    
    // --- Constructor ---

    public OrderListDto(Order order) {
        this.orderId = order.getOrderId();
        this.tableNo = order.getTableNo(); // This will now work (Integer to Integer)
        this.totalPrice = order.getTotalPrice();
    }

    // --- Getters and Setters ---
    // (Updated get/set for tableNo)

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    // --- UPDATED GETTER/SETTER ---
    public Integer getTableNo() {
        return tableNo;
    }

    public void setTableNo(Integer tableNo) {
        this.tableNo = tableNo;
    }
    // --- END OF UPDATE ---

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}

