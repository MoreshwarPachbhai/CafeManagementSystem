package com.demo.dto;

import com.demo.model.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * A DTO (Data Transfer Object) that represents one row
 * in your "History" list.
 * It includes only the data you requested.
 */
public class HistoryOrderDto {

    private Integer staffId;
    private Long orderId;
    private LocalDateTime date;
    private BigDecimal totalPrice;
    
    // --- Constructor ---

    public HistoryOrderDto(Order order) {
        this.orderId = order.getOrderId();
        this.date = order.getDate();
        this.totalPrice = order.getTotalPrice();
        
        // Get staffId from the associated Staff object
        if (order.getStaff() != null) {
            this.staffId = order.getStaff().getStaffId();
        } else {
            this.staffId = null; // Or some default
        }
    }

    // --- Getters and Setters ---
    // Spring Boot needs these to build the JSON

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
